import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*
 * One dialog for both adding and editing a vehicle.
 * existing == null  -> add mode, otherwise edit mode.
 */
public class VehicleDialog extends JDialog {

    private RentalSystem system;
    private Vehicle existing;
    private boolean saved = false;

    private JComboBox<String> typeBox;
    private JTextField idField, brandField, modelField, rateField, extraField;
    private JLabel extraLabel;

    public VehicleDialog(JFrame parent, RentalSystem system, Vehicle existing) {
        super(parent, existing == null ? "Add Vehicle" : "Edit Vehicle", true);
        this.system = system;
        this.existing = existing;
        setSize(380, 360);
        setLocationRelativeTo(parent);
        setResizable(false);

        typeBox = new JComboBox<>(new String[]{"Car", "Bike"});
        idField = new JTextField();
        brandField = new JTextField();
        modelField = new JTextField();
        rateField = new JTextField();
        extraField = new JTextField();
        extraLabel = new JLabel("Number of seats");

        typeBox.addActionListener(e -> updateExtraLabel());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 12));
        form.setBackground(Theme.BG);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.add(new JLabel("Type"));
        form.add(typeBox);
        form.add(new JLabel("Vehicle ID"));
        form.add(idField);
        form.add(new JLabel("Brand"));
        form.add(brandField);
        form.add(new JLabel("Model"));
        form.add(modelField);
        form.add(new JLabel("Rate per day (Rs.)"));
        form.add(rateField);
        form.add(extraLabel);
        form.add(extraField);

        if (existing != null) {
            typeBox.setSelectedItem(existing.getTypeName());
            typeBox.setEnabled(false);
            idField.setText(existing.getVehicleId());
            idField.setEditable(false);
            brandField.setText(existing.getBrand());
            modelField.setText(existing.getModel());
            rateField.setText(String.valueOf(existing.getRatePerDay()));
            extraField.setText(String.valueOf(existing.getExtraValue()));
            updateExtraLabel();
        }

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        Theme.styleButton(saveBtn, Theme.GOLD, Theme.NAVY);
        Theme.styleButton(cancelBtn, new Color(150, 160, 170), Color.WHITE);
        saveBtn.addActionListener(e -> handleSave());
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBackground(Theme.BG);
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }

    public boolean isSaved() {
        return saved;
    }

    private void updateExtraLabel() {
        if ("Car".equals(typeBox.getSelectedItem())) {
            extraLabel.setText("Number of seats");
        } else {
            extraLabel.setText("Engine CC");
        }
    }

    private void handleSave() {
        String id = idField.getText().trim();
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String rateText = rateField.getText().trim();
        String extraText = extraField.getText().trim();

        if (id.isEmpty() || brand.isEmpty() || model.isEmpty() || rateText.isEmpty() || extraText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double rate;
        int extra;
        try {
            rate = Double.parseDouble(rateText);
            extra = Integer.parseInt(extraText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rate must be a number and seats / CC must be a whole number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (rate <= 0 || extra <= 0) {
            JOptionPane.showMessageDialog(this, "Rate and seats / CC must be greater than zero.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existing == null) {
            Vehicle v;
            if ("Car".equals(typeBox.getSelectedItem())) {
                v = new Car(id, brand, model, rate, extra);
            } else {
                v = new Bike(id, brand, model, rate, extra);
            }
            if (!system.addVehicle(v)) {
                JOptionPane.showMessageDialog(this, "A vehicle with ID " + id + " already exists.",
                        "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            system.updateVehicle(existing, brand, model, rate, extra);
        }

        saved = true;
        dispose();
    }
}
