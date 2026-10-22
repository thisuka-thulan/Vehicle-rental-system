import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Theme {

    public static final Color NAVY = new Color(18, 42, 74);
    public static final Color GOLD = new Color(226, 160, 40);
    public static final Color BG = new Color(245, 249, 252);
    public static final Color RED = new Color(200, 60, 60);
    public static final Color GREEN = new Color(40, 150, 100);
    public static final Color PURPLE = new Color(120, 90, 170);

    public static void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    // table model where nothing can be edited by double clicking
    public static DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(27);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(250, 225, 170));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 236, 242));
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    public static JScrollPane wrap(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(8, 12, 8, 12));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public static JPanel buildHeader(String title, JButton rightButton) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setPreferredSize(new Dimension(800, 62));
        header.setBorder(new EmptyBorder(10, 22, 10, 22));

        JLabel label = new JLabel(title);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(label, BorderLayout.WEST);
        header.add(rightButton, BorderLayout.EAST);
        return header;
    }

    // Overdue = red, Returned = green
    public static void colorStatusColumn(JTable table, int column) {
        table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                String s = String.valueOf(value);
                if (s.equals("Overdue")) {
                    c.setForeground(new Color(200, 40, 40));
                } else if (s.equals("Returned")) {
                    c.setForeground(new Color(30, 130, 80));
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
    }

    public static String money(double amount) {
        return String.format("Rs.%,.2f", amount);
    }
}
