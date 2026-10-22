import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CustomerDashboard extends JFrame {

    private RentalSystem system;
    private Customer customer;

    // browse tab
    private DefaultTableModel vehicleModel;
    private JTable vehicleTable;
    private JTextField searchField, maxRateField;
    private JComboBox<String> typeBox;
    private ArrayList<Vehicle> shownVehicles = new ArrayList<>();

    // my rentals tab
    private DefaultTableModel rentalModel;
    private JTable rentalTable;
    private ArrayList<Rental> shownRentals = new ArrayList<>();

    public CustomerDashboard(RentalSystem system, Customer customer) {
        this.system = system;
        this.customer = customer;
        setTitle("GlobeDrive Rentals - " + customer.getName());
        setSize(860, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton logoutBtn = new JButton("Logout");
        Theme.styleButton(logoutBtn, Theme.RED, Color.WHITE);
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(system).setVisible(true));
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Vehicles", buildBrowseTab());
        tabs.addTab("My Rentals", buildMyRentalsTab());
        tabs.addChangeListener(e -> {
            refreshVehicles();
            refreshRentals();
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(Theme.buildHeader("Welcome, " + customer.getName(), logoutBtn), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        add(root);

        refreshVehicles();
        refreshRentals();
    }

    // ================= Browse tab =================

    private JPanel buildBrowseTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG);

        searchField = new JTextField(12);
        typeBox = new JComboBox<>(new String[]{"All", "Car", "Bike"});
        maxRateField = new JTextField(6);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        Theme.styleButton(searchBtn, Theme.NAVY, Color.WHITE);
        Theme.styleButton(clearBtn, new Color(150, 160, 170), Color.WHITE);

        searchBtn.addActionListener(e -> refreshVehicles());
        searchField.addActionListener(e -> refreshVehicles());
        maxRateField.addActionListener(e -> refreshVehicles());
        typeBox.addActionListener(e -> refreshVehicles());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            maxRateField.setText("");
            typeBox.setSelectedIndex(0);
            refreshVehicles();
        });

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        filterBar.setBackground(Theme.BG);
        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);
        filterBar.add(new JLabel("Type:"));
        filterBar.add(typeBox);
        filterBar.add(new JLabel("Max rate/day (Rs.):"));
        filterBar.add(maxRateField);
        filterBar.add(searchBtn);
        filterBar.add(clearBtn);
        panel.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Type", "Brand", "Model", "Extra", "Rate/Day"};
        vehicleModel = Theme.readOnlyModel(cols);
        vehicleTable = Theme.createTable(vehicleModel);
        panel.add(Theme.wrap(vehicleTable), BorderLayout.CENTER);

        JButton rentBtn = new JButton("Rent Selected Vehicle");
        Theme.styleButton(rentBtn, Theme.GREEN, Color.WHITE);
        rentBtn.addActionListener(e -> handleRent());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(Theme.BG);
        btnPanel.add(rentBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // blank = no limit, -1 = what was typed isn't a number
    private double readMaxRate() {
        String text = maxRateField.getText().trim();
        if (text.isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void refreshVehicles() {
        double maxRate = readMaxRate();
        if (maxRate < 0) {
            JOptionPane.showMessageDialog(this, "Max rate must be a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        vehicleModel.setRowCount(0);
        shownVehicles.clear();
        for (Vehicle v : system.searchVehicles(searchField.getText(), (String) typeBox.getSelectedItem(), maxRate, true)) {
            shownVehicles.add(v);
            vehicleModel.addRow(new Object[]{
                    v.getVehicleId(), v.getTypeName(), v.getBrand(), v.getModel(),
                    v.getExtraInfo(), Theme.money(v.getRatePerDay())
            });
        }
    }

    private void handleRent() {
        int row = vehicleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vehicle v = shownVehicles.get(row);

        String daysStr = JOptionPane.showInputDialog(this,
                "Enter number of rental days for " + v.getBrand() + " " + v.getModel() + ":");
        if (daysStr == null || daysStr.trim().isEmpty()) {
            return;
        }

        int days;
        try {
            days = Integer.parseInt(daysStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (days <= 0 || days > 365) {
            JOptionPane.showMessageDialog(this, "Days must be between 1 and 365.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String summary = "Vehicle: " + v.getBrand() + " " + v.getModel() + "\n"
                + "Days: " + days + "\n"
                + "Rental cost: " + Theme.money(v.calculateRentalCost(days)) + "\n"
                + "Due back by: " + LocalDate.now().plusDays(days) + "\n"
                + "Late fee: " + Theme.money(v.getRatePerDay() * Rental.LATE_FEE_MULTIPLIER) + " per extra day\n\n"
                + "Confirm this rental?";
        int ok = JOptionPane.showConfirmDialog(this, summary, "Confirm Rental", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }

        Rental rental = system.rentVehicle(v.getVehicleId(), customer, days);
        if (rental == null) {
            JOptionPane.showMessageDialog(this, "Could not rent this vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            new ReceiptDialog(this, rental).setVisible(true);
        }
        refreshVehicles();
        refreshRentals();
    }

    // ================= My Rentals tab =================

    private JPanel buildMyRentalsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG);

        String[] cols = {"Rental ID", "Vehicle", "Rented On", "Due Date", "Returned On", "Cost", "Late Fee", "Status"};
        rentalModel = Theme.readOnlyModel(cols);
        rentalTable = Theme.createTable(rentalModel);
        Theme.colorStatusColumn(rentalTable, 7);
        panel.add(Theme.wrap(rentalTable), BorderLayout.CENTER);

        JButton returnBtn = new JButton("Return Selected Vehicle");
        JButton receiptBtn = new JButton("View Receipt");
        JButton refreshBtn = new JButton("Refresh");
        Theme.styleButton(returnBtn, new Color(210, 120, 40), Color.WHITE);
        Theme.styleButton(receiptBtn, Theme.PURPLE, Color.WHITE);
        Theme.styleButton(refreshBtn, Theme.GOLD, Theme.NAVY);
        returnBtn.addActionListener(e -> handleReturn());
        receiptBtn.addActionListener(e -> showReceipt());
        refreshBtn.addActionListener(e -> {
            refreshVehicles();
            refreshRentals();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(Theme.BG);
        btnPanel.add(returnBtn);
        btnPanel.add(receiptBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRentals() {
        rentalModel.setRowCount(0);
        shownRentals.clear();

        ArrayList<Rental> mine = system.getRentalsByCustomer(customer);
        for (int i = mine.size() - 1; i >= 0; i--) {
            Rental r = mine.get(i);
            shownRentals.add(r);
            rentalModel.addRow(new Object[]{
                    r.getRentalId(),
                    r.getVehicle().getBrand() + " " + r.getVehicle().getModel(),
                    r.getRentDate(),
                    r.getDueDate(),
                    r.isReturned() ? r.getReturnDate().toString() : "-",
                    Theme.money(r.getTotalCost()),
                    Theme.money(r.getLateFee()),
                    r.getStatus()
            });
        }
    }

    private Rental getSelectedRental() {
        int row = rentalTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return shownRentals.get(row);
    }

    private void handleReturn() {
        Rental r = getSelectedRental();
        if (r == null) return;

        if (r.isReturned()) {
            JOptionPane.showMessageDialog(this, "You have already returned this vehicle.");
            return;
        }

        String msg = "Return " + r.getVehicle().getBrand() + " " + r.getVehicle().getModel() + "?\n\n"
                + "Due date: " + r.getDueDate() + "\n"
                + "Late days: " + r.getLateDays() + "\n"
                + "Late fee: " + Theme.money(r.getLateFee());
        int ok = JOptionPane.showConfirmDialog(this, msg, "Confirm Return", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        system.returnRental(r);
        refreshVehicles();
        refreshRentals();
        new ReceiptDialog(this, r).setVisible(true);
    }

    private void showReceipt() {
        Rental r = getSelectedRental();
        if (r != null) {
            new ReceiptDialog(this, r).setVisible(true);
        }
    }
}
