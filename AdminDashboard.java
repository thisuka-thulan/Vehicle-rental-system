import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    private RentalSystem system;

    // vehicles tab
    private DefaultTableModel vehicleModel;
    private JTable vehicleTable;
    private JTextField searchField;
    private JComboBox<String> typeBox, statusBox;
    private ArrayList<Vehicle> shownVehicles = new ArrayList<>(); // same order as the table rows

    // rentals tab
    private DefaultTableModel rentalModel;
    private JTable rentalTable;
    private JCheckBox activeOnlyBox;
    private ArrayList<Rental> shownRentals = new ArrayList<>();

    // customers tab
    private DefaultTableModel customerModel;

    public AdminDashboard(RentalSystem system) {
        this.system = system;
        setTitle("Admin Dashboard - GlobeDrive Rentals");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton logoutBtn = new JButton("Logout");
        Theme.styleButton(logoutBtn, Theme.RED, Color.WHITE);
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(system).setVisible(true));
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicles", buildVehiclesTab());
        tabs.addTab("Rentals", buildRentalsTab());
        tabs.addTab("Customers", buildCustomersTab());
        tabs.addChangeListener(e -> refreshAll());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(Theme.buildHeader("Admin Dashboard", logoutBtn), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        add(root);

        refreshAll();
    }

    private void refreshAll() {
        refreshVehicles();
        refreshRentals();
        refreshCustomers();
    }

    // ================= Vehicles tab =================

    private JPanel buildVehiclesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG);

        // search / filter bar
        searchField = new JTextField(14);
        typeBox = new JComboBox<>(new String[]{"All", "Car", "Bike"});
        statusBox = new JComboBox<>(new String[]{"All", "Available", "Rented"});
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        Theme.styleButton(searchBtn, Theme.NAVY, Color.WHITE);
        Theme.styleButton(clearBtn, new Color(150, 160, 170), Color.WHITE);

        searchBtn.addActionListener(e -> refreshVehicles());
        searchField.addActionListener(e -> refreshVehicles());
        typeBox.addActionListener(e -> refreshVehicles());
        statusBox.addActionListener(e -> refreshVehicles());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            typeBox.setSelectedIndex(0);
            statusBox.setSelectedIndex(0);
            refreshVehicles();
        });

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        filterBar.setBackground(Theme.BG);
        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);
        filterBar.add(new JLabel("Type:"));
        filterBar.add(typeBox);
        filterBar.add(new JLabel("Status:"));
        filterBar.add(statusBox);
        filterBar.add(searchBtn);
        filterBar.add(clearBtn);
        panel.add(filterBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Type", "Brand", "Model", "Extra", "Rate/Day", "Available"};
        vehicleModel = Theme.readOnlyModel(cols);
        vehicleTable = Theme.createTable(vehicleModel);
        panel.add(Theme.wrap(vehicleTable), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Vehicle");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        Theme.styleButton(addBtn, Theme.GREEN, Color.WHITE);
        Theme.styleButton(editBtn, Theme.GOLD, Theme.NAVY);
        Theme.styleButton(deleteBtn, Theme.RED, Color.WHITE);
        addBtn.addActionListener(e -> handleAddVehicle());
        editBtn.addActionListener(e -> handleEditVehicle());
        deleteBtn.addActionListener(e -> handleDeleteVehicle());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(Theme.BG);
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshVehicles() {
        vehicleModel.setRowCount(0);
        shownVehicles.clear();

        String status = (String) statusBox.getSelectedItem();
        for (Vehicle v : system.searchVehicles(searchField.getText(), (String) typeBox.getSelectedItem(), 0, false)) {
            if (status.equals("Available") && !v.isAvailable()) continue;
            if (status.equals("Rented") && v.isAvailable()) continue;

            shownVehicles.add(v);
            vehicleModel.addRow(new Object[]{
                    v.getVehicleId(), v.getTypeName(), v.getBrand(), v.getModel(), v.getExtraInfo(),
                    Theme.money(v.getRatePerDay()), v.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private Vehicle getSelectedVehicle() {
        int row = vehicleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return shownVehicles.get(row);
    }

    private void handleAddVehicle() {
        VehicleDialog dialog = new VehicleDialog(this, system, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshVehicles();
        }
    }

    private void handleEditVehicle() {
        Vehicle v = getSelectedVehicle();
        if (v == null) return;

        VehicleDialog dialog = new VehicleDialog(this, system, v);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshVehicles();
        }
    }

    private void handleDeleteVehicle() {
        Vehicle v = getSelectedVehicle();
        if (v == null) return;

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete " + v.getBrand() + " " + v.getModel() + " (" + v.getVehicleId() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        String error = system.removeVehicle(v.getVehicleId());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Cannot Delete", JOptionPane.ERROR_MESSAGE);
        }
        refreshVehicles();
    }

    // ================= Rentals tab =================

    private JPanel buildRentalsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG);

        activeOnlyBox = new JCheckBox("Show active / overdue only");
        activeOnlyBox.setOpaque(false);
        activeOnlyBox.addActionListener(e -> refreshRentals());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        top.setBackground(Theme.BG);
        top.add(activeOnlyBox);
        panel.add(top, BorderLayout.NORTH);

        String[] cols = {"Rental ID", "Vehicle", "Customer", "Rented On", "Due Date", "Returned On",
                "Cost", "Late Fee", "Status"};
        rentalModel = Theme.readOnlyModel(cols);
        rentalTable = Theme.createTable(rentalModel);
        Theme.colorStatusColumn(rentalTable, 8);
        panel.add(Theme.wrap(rentalTable), BorderLayout.CENTER);

        JButton returnBtn = new JButton("Return Selected");
        JButton receiptBtn = new JButton("View Receipt");
        Theme.styleButton(returnBtn, Theme.GREEN, Color.WHITE);
        Theme.styleButton(receiptBtn, Theme.PURPLE, Color.WHITE);
        returnBtn.addActionListener(e -> handleReturn());
        receiptBtn.addActionListener(e -> showReceipt());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(Theme.BG);
        btnPanel.add(returnBtn);
        btnPanel.add(receiptBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRentals() {
        rentalModel.setRowCount(0);
        shownRentals.clear();

        // newest rentals first
        ArrayList<Rental> all = system.getRentals();
        for (int i = all.size() - 1; i >= 0; i--) {
            Rental r = all.get(i);
            if (activeOnlyBox.isSelected() && r.isReturned()) continue;

            shownRentals.add(r);
            rentalModel.addRow(new Object[]{
                    r.getRentalId(),
                    r.getVehicle().getBrand() + " " + r.getVehicle().getModel(),
                    r.getCustomer().getName(),
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
            JOptionPane.showMessageDialog(this, "This vehicle has already been returned.");
            return;
        }

        String msg = "Return " + r.getVehicle().getBrand() + " " + r.getVehicle().getModel()
                + " from " + r.getCustomer().getName() + "?\n\n"
                + "Due date: " + r.getDueDate() + "\n"
                + "Late days: " + r.getLateDays() + "\n"
                + "Late fee: " + Theme.money(r.getLateFee());
        int ok = JOptionPane.showConfirmDialog(this, msg, "Confirm Return", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        system.returnRental(r);
        refreshAll();
        new ReceiptDialog(this, r).setVisible(true);
    }

    private void showReceipt() {
        Rental r = getSelectedRental();
        if (r != null) {
            new ReceiptDialog(this, r).setVisible(true);
        }
    }

    // ================= Customers tab =================

    private JPanel buildCustomersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG);

        String[] cols = {"Customer ID", "Name", "Phone", "Username", "Total Rentals", "Currently Renting"};
        customerModel = Theme.readOnlyModel(cols);
        JTable table = Theme.createTable(customerModel);
        panel.add(Theme.wrap(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshCustomers() {
        customerModel.setRowCount(0);
        for (Customer c : system.getCustomers()) {
            ArrayList<Rental> mine = system.getRentalsByCustomer(c);
            int active = 0;
            for (Rental r : mine) {
                if (!r.isReturned()) active++;
            }
            customerModel.addRow(new Object[]{
                    c.getCustomerId(), c.getName(), c.getPhone(), c.getUsername(), mine.size(), active
            });
        }
    }
}
