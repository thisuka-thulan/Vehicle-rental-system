import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private RentalSystem system;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JRadioButton adminRadio, customerRadio;
    private JLabel statusLabel;

    private static final Color BG = new Color(17, 24, 39);
    private static final Color SURFACE = new Color(31, 41, 55);
    private static final Color ACCENT = new Color(45, 212, 191);
    private static final Color TEXT = new Color(243, 244, 246);
    private static final Color MUTED = new Color(156, 163, 175);

    public LoginFrame(RentalSystem system) {
        this.system = system;
        setTitle("GlobeDrive Rentals - Sign In");
        setSize(440, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);

        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 32, 22, 32));
        card.setPreferredSize(new Dimension(360, 480));

        JLabel titleLabel = new JLabel("GlobeDrive Rentals");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        titleLabel.setForeground(TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Your next journey starts here");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Login as:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabel.setForeground(TEXT);

        adminRadio = new JRadioButton("Admin", true);
        customerRadio = new JRadioButton("Customer");
        adminRadio.setOpaque(false);
        customerRadio.setOpaque(false);
        adminRadio.setForeground(TEXT);
        customerRadio.setForeground(TEXT);
        ButtonGroup group = new ButtonGroup();
        group.add(adminRadio);
        group.add(customerRadio);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.setOpaque(false);
        rolePanel.add(adminRadio);
        rolePanel.add(customerRadio);
        rolePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabel.setForeground(TEXT);
        usernameField = new JTextField();
        styleField(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setForeground(TEXT);
        passwordField = new JPasswordField();
        styleField(passwordField);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, ACCENT, BG);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(this::handleLogin);

        JButton registerBtn = new JButton("Register as New Customer");
        styleButton(registerBtn, new Color(55, 65, 81), TEXT);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> new RegisterDialog(this, system).setVisible(true));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(248, 113, 113));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(25));
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(rolePanel);
        card.add(Box.createVerticalStrut(14));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(22));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        JLabel hint = new JLabel("<html><center>Demo admin: admin / admin123<br>Demo customer: kasun / pass123</center></html>");
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setForeground(MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setBorder(new EmptyBorder(12, 0, 0, 0));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(card);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(hint);
        root.add(content);
        setContentPane(root);
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(280, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBackground(BG);
        field.setForeground(TEXT);
        field.setCaretColor(ACCENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new LineBorder(new Color(75, 85, 99), 1, true));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(280, 40));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        if (adminRadio.isSelected()) {
            if (system.authenticateAdmin(username, password)) {
                dispose();
                SwingUtilities.invokeLater(() -> new AdminDashboard(system).setVisible(true));
            } else {
                statusLabel.setText("Invalid admin credentials.");
            }
            return;
        }

        Customer customer = system.authenticateCustomer(username, password);
        if (customer != null) {
            dispose();
            SwingUtilities.invokeLater(() -> new CustomerDashboard(system, customer).setVisible(true));
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }
}
