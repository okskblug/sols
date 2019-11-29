

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

public class TestMouse {

    public static void main(String[] args) {
        new TestMouse();
    }

    private JLabel counter;
    private int count;

    public TestMouse() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                DefaultTableModel model = new DefaultTableModel(
                        new Object[]{"A", "B", "B", "B", "B", "B", "B"},
                        10
                );
                JTable table = new JTable(model);
                counter = new JLabel("0");

                final JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem viewProfile = new JMenuItem("View Profile");
                viewProfile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                popupMenu.add(viewProfile);
                table.setComponentPopupMenu(popupMenu);
                table.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("clicked");
                        count++;
                        counter.setText(String.valueOf(count));
                    }

                });

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(counter, BorderLayout.NORTH);
                frame.add(new JScrollPane(table));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}