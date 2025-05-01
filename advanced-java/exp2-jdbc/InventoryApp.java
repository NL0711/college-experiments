import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class InventoryApp extends JFrame {
    JTextField nameField, priceField, quantityField;
    JComboBox<String> supplierComboBox;
    JTable productTable, supplierTable, supplierProductsTable;
    DefaultTableModel productModel, supplierModel, supplierProductsModel;
    Map<Integer, String> supplierMap;

    JTabbedPane tabbedPane;
    Conn con;

    public InventoryApp() {
        con = new Conn();
        setTitle("Product Inventory Management");
        setSize(500, 500);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Products", createProductPanel());    
        tabbedPane.addTab("Suppliers", createSupplierPanel());
        tabbedPane.addTab("Supplier Products", createSupplierProductsPanel());

        supplierMap = new HashMap<>();
        loadSuppliersMap();
    }

    public JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Supplier:"));
        supplierComboBox = new JComboBox<>();
        formPanel.add(supplierComboBox);

        JButton addButton = new JButton("Add Product");
        JButton deleteButton = new JButton("Delete Product");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        productModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity", "Supplier"}, 0);
        productTable = new JTable(productModel);
        loadProducts();

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        return panel;
    }

    public JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField supplierNameField = new JTextField();
        JTextField supplierContactField = new JTextField();

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(supplierNameField);

        formPanel.add(new JLabel("Contact:"));
        formPanel.add(supplierContactField);

        JButton addButton = new JButton("Add Supplier");
        JButton deleteButton = new JButton("Delete Supplier");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        supplierModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact"}, 0);
        supplierTable = new JTable(supplierModel);
        loadSuppliers();

        panel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addSupplier(supplierNameField, supplierContactField));
        deleteButton.addActionListener(e -> deleteSupplier());

        return panel;
    }

    public JPanel createSupplierProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton viewButton = new JButton("View Products by Selected Supplier");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(viewButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        supplierProductsModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity", "Supplier"}, 0);
        supplierProductsTable = new JTable(supplierProductsModel);

        panel.add(new JScrollPane(supplierProductsTable), BorderLayout.CENTER);

        viewButton.addActionListener(e -> loadFilteredProducts());

        return panel;
    }

    public void loadProducts() {
        productModel.setRowCount(0);
        String query = "SELECT p.id, p.name, p.price, p.quantity, s.name as supplier FROM products p JOIN suppliers s ON p.supplier_id = s.id";
        try {
            ResultSet rs = con.executeQuery(query);
            while (rs.next()) {
                productModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("supplier")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceField.getText());
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and quantity must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedSupplier = (String) supplierComboBox.getSelectedItem();
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "Select a supplier", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int supplierId = getSupplierId(selectedSupplier);

        String sql = "INSERT INTO products (name, price, quantity, supplier_id) VALUES (?, ?, ?, ?)";
        try {
            con.executeUpdate(sql, name, price, quantity, supplierId);
            JOptionPane.showMessageDialog(this, "Product added");
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) productModel.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM products WHERE id=?";
        try {
            con.executeUpdate(sql, id);
            JOptionPane.showMessageDialog(this, "Product deleted");
            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSuppliers() {
        supplierModel.setRowCount(0);
        String query = "SELECT * FROM suppliers";
        try {
            ResultSet rs = con.executeQuery(query);
            while (rs.next()) {
                supplierModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSuppliersMap() {
        supplierMap.clear();
        supplierComboBox.removeAllItems();
        String query = "SELECT * FROM suppliers";
        try {
            ResultSet rs = con.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                supplierMap.put(id, name);
                supplierComboBox.addItem(name);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier(JTextField nameField, JTextField contactField) {
        String name = nameField.getText();
        if (name.isEmpty())
            return;
        
        String contact = contactField.getText();
        if (contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier contact cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO suppliers (name, contact) VALUES (?, ?)";
        try {
            con.executeUpdate(sql, name, contact);
            JOptionPane.showMessageDialog(this, "Supplier added");
            loadSuppliers();
            loadSuppliersMap();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1)
            return;

        int id = (int) supplierModel.getValueAt(selectedRow, 0);
        String sql = "DELETE FROM suppliers WHERE id=?";
        try {
            con.executeUpdate(sql, id);
            JOptionPane.showMessageDialog(this, "Supplier deleted");
            loadSuppliers();
            loadSuppliersMap();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFilteredProducts() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1)
            return;

        int supplierId = (int) supplierModel.getValueAt(selectedRow, 0);
        String query = "SELECT p.id, p.name, p.price, p.quantity, s.name as supplier FROM products p JOIN suppliers s ON p.supplier_id = s.id WHERE p.supplier_id = ?";
        supplierProductsModel.setRowCount(0);

        try (java.sql.Connection conn = con.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                supplierProductsModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("supplier")
                });
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSupplierId(String supplierName) {
        for (Map.Entry<Integer, String> entry : supplierMap.entrySet()) {
            if (entry.getValue().equals(supplierName)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryApp().setVisible(true));
    }
}
