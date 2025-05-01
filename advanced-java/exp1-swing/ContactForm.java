import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ContactForm extends JFrame implements ActionListener {

    JTextField tfname, tfphone, tfemail, tfaddress;
    JTable table;
    DefaultTableModel tableModel;

    public ContactForm() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("./images/background.png"));
        JLabel lblimage = new JLabel(i1);
        lblimage.setBounds(0, 0, 380, 900);
        add(lblimage);

        JLabel lblname = new JLabel("Name");
        lblname.setBounds(60, 40, 150, 25);
        lblname.setForeground(Color.WHITE);
        lblimage.add(lblname);

        tfname = new JTextField();
        tfname.setBounds(140, 40, 150, 25);
        lblimage.add(tfname);

        JLabel lblphone = new JLabel("Phone");
        lblphone.setBounds(60, 80, 150, 25);
        lblphone.setForeground(Color.WHITE);
        lblimage.add(lblphone);

        tfphone = new JTextField();
        tfphone.setBounds(140, 80, 150, 25);
        lblimage.add(tfphone);

        JLabel lblemail = new JLabel("Email");
        lblemail.setBounds(60, 120, 150, 25);
        lblemail.setForeground(Color.WHITE);
        lblimage.add(lblemail);

        tfemail = new JTextField();
        tfemail.setBounds(140, 120, 150, 25);
        lblimage.add(tfemail);

        JLabel lbladdress = new JLabel("Address");
        lbladdress.setBounds(60, 160, 150, 25);
        lbladdress.setForeground(Color.WHITE);
        lblimage.add(lbladdress);

        tfaddress = new JTextField();
        tfaddress.setBounds(140, 160, 150, 25);
        lblimage.add(tfaddress);

        JButton enter = new JButton("ENTER");
        enter.setBackground(Color.BLACK);
        enter.setForeground(Color.WHITE);
        enter.setBounds(60, 200, 230, 30);
        enter.addActionListener(this);
        lblimage.add(enter);

        tableModel = new DefaultTableModel(new Object[]{"Name", "Phone", "Email", "Address"}, 0);
        table = new JTable(tableModel);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.BLACK);
        tableHeader.setForeground(Color.WHITE);

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(30, 240, 290, 150);
        lblimage.add(jsp);

        setSize(380, 500);
        setLocation(220, 100);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String name = tfname.getText().trim();
        String phone = tfphone.getText().trim();
        String email = tfemail.getText().trim();
        String address = tfaddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields must be filled out.",
                "Form Incomplete",
                JOptionPane.ERROR_MESSAGE);

        } else if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } else {
            tableModel.addRow(new Object[]{name, phone, email, address});

            tfname.setText("");
            tfphone.setText("");
            tfemail.setText("");
            tfaddress.setText("");
        }
    }

    public static void main(String[] args) {
        new ContactForm();
    }
}
