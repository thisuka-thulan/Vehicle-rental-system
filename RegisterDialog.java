import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private static final Color NAVY = new Color(18, 42, 74);
    private static final Color GOLD = new Color(226, 160, 40);
    private static final Color BG = new Color(245, 249, 252);

    private JTextField nameField, phoneField, usernameField;
    private JPasswordField passwordField;
    private RentalSystem system;

    public RegisterDialog(JFrame parent, RentalSystem system) {
        super(parent, "Register New Customer", true);
        this.system = system;
        setSize(370, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel heading = new JLabel("Create your account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(NAVY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(14));

        nameField = addField(panel, "Full Name");
        phoneField = addField(panel, "Phone Number");
        usernameField = addField(panel, "Choose Username");

        JLabel passLabel = new JLabel("Choose Password");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(290, 30));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setBorder(new LineBorder(new Color(190, 200, 210)));
        panel.add(passLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(GOLD);
        registerBtn.setForeground(NAVY);
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setBorderPainted(false);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(290, 36));
        registerBtn.addActionListener(e -> handleRegister());

        panel.add(registerBtn);
        setContentPane(panel);
    }

    private JTextField addField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(290, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(new LineBorder(new Color(190, 200, 210)));
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
        return field;
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer newCustomer = system.registerCustomer(name, phone, username, password);
        if (newCustomer == null) {
            JOptionPane.showMessageDialog(this, "Username already taken. Try another.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Registration successful!\nYour Customer ID: " + newCustomer.getCustomerId() + "\nYou can now log in.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
