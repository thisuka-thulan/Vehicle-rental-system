import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class ReceiptDialog extends JDialog {

    private Rental rental;

    public ReceiptDialog(Window parent, Rental rental) {
        super(parent, "Receipt - " + rental.getRentalId(), Dialog.ModalityType.APPLICATION_MODAL);
        this.rental = rental;
        setSize(450, 470);
        setLocationRelativeTo(parent);

        JTextArea area = new JTextArea(rental.getReceiptText());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(12, 14, 12, 14));

        JButton saveBtn = new JButton("Save to File");
        JButton closeBtn = new JButton("Close");
        Theme.styleButton(saveBtn, Theme.GOLD, Theme.NAVY);
        Theme.styleButton(closeBtn, Theme.NAVY, Color.WHITE);
        saveBtn.addActionListener(e -> saveReceipt());
        closeBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.setBackground(Theme.BG);
        buttons.add(saveBtn);
        buttons.add(closeBtn);

        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void saveReceipt() {
        try {
            String path = DataStore.saveReceipt(rental);
            JOptionPane.showMessageDialog(this, "Receipt saved to:\n" + path);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save the receipt: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
