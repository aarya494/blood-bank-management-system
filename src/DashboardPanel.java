import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {

    static final String DB_URL = "jdbc:mysql://localhost:3306/blood_bank_db";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "malware@494";

    public DashboardPanel(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBackground(new Color(40, 40, 40));

        JButton btnRequest = new JButton("Request Blood");
        JButton btnDonate = new JButton("Donate Blood");
        JButton btnInventory = new JButton("View Inventory");
        JButton btnDonors = new JButton("View Donors");
        JButton btnDonations = new JButton("View Donations");
        JButton btnLogout = new JButton("Logout");

        for (JButton btn : new JButton[]{btnRequest, btnDonate, btnInventory, btnDonors, btnDonations, btnLogout}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(btn);
        }

        add(sidebar, BorderLayout.WEST);

        // Main content area
        JPanel content = new JPanel(new CardLayout());

        // Request Blood Panel
        JPanel requestPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        requestPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField txtName = new JTextField();
        JTextField txtGroup = new JTextField();
        JTextField txtUnits = new JTextField();
        JButton btnSubmit = new JButton("Submit Request");
        JLabel lblStatus = new JLabel("");
        requestPanel.add(new JLabel("Patient Name:")); requestPanel.add(txtName);
        requestPanel.add(new JLabel("Blood Group:")); requestPanel.add(txtGroup);
        requestPanel.add(new JLabel("Units Needed:")); requestPanel.add(txtUnits);
        requestPanel.add(new JLabel("")); requestPanel.add(btnSubmit);
        requestPanel.add(new JLabel("")); requestPanel.add(lblStatus);
        content.add(requestPanel, "request");

        // Donate Blood Panel
        JPanel donatePanel = new JPanel(new GridLayout(8, 2, 10, 10));
        donatePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField txtDonorName = new JTextField();
        JTextField txtDonorGroup = new JTextField();
        JTextField txtAge = new JTextField();
        JTextField txtGender = new JTextField();
        JTextField txtContact = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtDonatedUnits = new JTextField();
        JButton btnDonateSubmit = new JButton("Submit Donation");
        JLabel lblDonateStatus = new JLabel("");
        donatePanel.add(new JLabel("Donor Name:")); donatePanel.add(txtDonorName);
        donatePanel.add(new JLabel("Blood Group:")); donatePanel.add(txtDonorGroup);
        donatePanel.add(new JLabel("Age:")); donatePanel.add(txtAge);
        donatePanel.add(new JLabel("Gender:")); donatePanel.add(txtGender);
        donatePanel.add(new JLabel("Contact No.:")); donatePanel.add(txtContact);
        donatePanel.add(new JLabel("Address:")); donatePanel.add(txtAddress);
        donatePanel.add(new JLabel("Units Donated:")); donatePanel.add(txtDonatedUnits);
        donatePanel.add(new JLabel("")); donatePanel.add(btnDonateSubmit);
        donatePanel.add(new JLabel("")); donatePanel.add(lblDonateStatus);
        content.add(donatePanel, "donate");

        // Inventory Panel
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        JTable inventoryTable = new JTable();
        inventoryPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        content.add(inventoryPanel, "inventory");

        // Donors Panel
        JPanel donorsPanel = new JPanel(new BorderLayout());
        JTable donorsTable = new JTable();
        donorsPanel.add(new JScrollPane(donorsTable), BorderLayout.CENTER);
        content.add(donorsPanel, "donors");

        // Donations Panel
        JPanel donationsPanel = new JPanel(new BorderLayout());
        JTable donationsTable = new JTable();
        donationsPanel.add(new JScrollPane(donationsTable), BorderLayout.CENTER);
        content.add(donationsPanel, "donations");

        add(content, BorderLayout.CENTER);
        CardLayout contentLayout = (CardLayout) content.getLayout();

        // Navigation Actions
        btnRequest.addActionListener(e -> contentLayout.show(content, "request"));
        btnDonate.addActionListener(e -> contentLayout.show(content, "donate"));
        btnInventory.addActionListener(e -> {
            refreshTable(inventoryTable, "SELECT * FROM blood_inventory");
            contentLayout.show(content, "inventory");
        });
        btnDonors.addActionListener(e -> {
            refreshTable(donorsTable, "SELECT * FROM donors");
            contentLayout.show(content, "donors");
        });
        btnDonations.addActionListener(e -> {
            refreshTable(donationsTable, "SELECT donations.donation_id, donors.name, donations.blood_group, donations.units, donations.donation_date, donations.expiry_date FROM donations JOIN donors ON donations.donor_id = donors.donor_id");
            contentLayout.show(content, "donations");
        });
        btnLogout.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        // Blood Request Submit Logic
        btnSubmit.addActionListener(e -> {
            String name = txtName.getText().trim();
            String group = txtGroup.getText().trim().toUpperCase();
            int units;

            try {
                units = Integer.parseInt(txtUnits.getText().trim());
            } catch (NumberFormatException ex) {
                lblStatus.setText("❌ Units must be a number.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String checkQuery = "SELECT units_available FROM blood_inventory WHERE blood_group = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, group);
                ResultSet rs = checkStmt.executeQuery();

                int available = 0;
                if (rs.next()) {
                    available = rs.getInt("units_available");
                }

                String status;
                if (available >= units) {
                    String updateInventory = "UPDATE blood_inventory SET units_available = units_available - ? WHERE blood_group = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateInventory);
                    updateStmt.setInt(1, units);
                    updateStmt.setString(2, group);
                    updateStmt.executeUpdate();
                    status = "Fulfilled";
                    lblStatus.setText("✅ Request Fulfilled.");
                } else {
                    status = "Pending";
                    lblStatus.setText("⚠️ Request Pending (not enough units).");
                }

                String insertRequest = "INSERT INTO blood_requests (patient_name, blood_group, units_needed, request_date, status) VALUES (?, ?, ?, NOW(), ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertRequest);
                insertStmt.setString(1, name);
                insertStmt.setString(2, group);
                insertStmt.setInt(3, units);
                insertStmt.setString(4, status);
                insertStmt.executeUpdate();

                txtName.setText(""); txtGroup.setText(""); txtUnits.setText("");

            } catch (SQLException ex) {
                lblStatus.setText("❌ DB Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Donate Blood Submit Logic
        btnDonateSubmit.addActionListener(e -> {
            String name = txtDonorName.getText().trim();
            String group = txtDonorGroup.getText().trim().toUpperCase();
            String ageStr = txtAge.getText().trim();
            String gender = txtGender.getText().trim();
            String contact = txtContact.getText().trim();
            String address = txtAddress.getText().trim();
            int units;

            try {
                units = Integer.parseInt(txtDonatedUnits.getText().trim());
            } catch (NumberFormatException ex) {
                lblDonateStatus.setText("❌ Units must be a number.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Insert into donors
                String insertDonor = "INSERT INTO donors (name, blood_group, age, gender, contact, address) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement donorStmt = conn.prepareStatement(insertDonor, Statement.RETURN_GENERATED_KEYS);
                donorStmt.setString(1, name);
                donorStmt.setString(2, group);
                donorStmt.setInt(3, Integer.parseInt(ageStr));
                donorStmt.setString(4, gender);
                donorStmt.setString(5, contact);
                donorStmt.setString(6, address);
                donorStmt.executeUpdate();

                ResultSet rs = donorStmt.getGeneratedKeys();
                int donorId = 0;
                if (rs.next()) {
                    donorId = rs.getInt(1);
                }

                // Insert into donations with expiry date
                String insertDonation = "INSERT INTO donations (donor_id, blood_group, units, donation_date, expiry_date) VALUES (?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 35 DAY))";
                PreparedStatement donationStmt = conn.prepareStatement(insertDonation);
                donationStmt.setInt(1, donorId);
                donationStmt.setString(2, group);
                donationStmt.setInt(3, units);
                donationStmt.executeUpdate();

                // Update inventory
                String updateInventory = "INSERT INTO blood_inventory (blood_group, units_available) VALUES (?, ?) ON DUPLICATE KEY UPDATE units_available = units_available + ?";
                PreparedStatement inventoryStmt = conn.prepareStatement(updateInventory);
                inventoryStmt.setString(1, group);
                inventoryStmt.setInt(2, units);
                inventoryStmt.setInt(3, units);
                inventoryStmt.executeUpdate();

                lblDonateStatus.setText("✅ Donation recorded.");

                txtDonorName.setText(""); txtDonorGroup.setText(""); txtAge.setText(""); txtGender.setText(""); txtContact.setText(""); txtAddress.setText(""); txtDonatedUnits.setText("");

            } catch (SQLException ex) {
                lblDonateStatus.setText("❌ DB Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // Helper: refresh table from query
    private void refreshTable(JTable table, String query) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= colCount; i++) {
                model.addColumn(rsmd.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 0; i < colCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
            table.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
