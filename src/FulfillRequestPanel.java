import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FulfillRequestPanel extends JPanel {
    JTable table;
    DefaultTableModel model;
    JButton fulfillButton;

    public FulfillRequestPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Name", "Blood Group", "Units", "Status"});
        table = new JTable(model);
        loadRequests();

        add(new JScrollPane(table), BorderLayout.CENTER);

        fulfillButton = new JButton("Fulfill Selected Request");
        add(fulfillButton, BorderLayout.SOUTH);

        fulfillButton.addActionListener(e -> fulfillSelectedRequest());
    }

    void loadRequests() {
        try (Connection con = DB.getConnection()) {
            model.setRowCount(0);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM blood_requests WHERE status = 'Pending'");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("request_id"),
                    rs.getString("requester_name"),
                    rs.getString("blood_group"),
                    rs.getInt("units_requested"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void fulfillSelectedRequest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to fulfill.");
            return;
        }

        int requestId = (int) model.getValueAt(selectedRow, 0);
        String bloodGroup = (String) model.getValueAt(selectedRow, 2);
        int unitsRequested = (int) model.getValueAt(selectedRow, 3);

        try (Connection con = DB.getConnection()) {
            // Check stock
            PreparedStatement checkStock = con.prepareStatement("SELECT units FROM blood_stock WHERE blood_group = ?");
            checkStock.setString(1, bloodGroup);
            ResultSet rs = checkStock.executeQuery();
            if (rs.next()) {
                int available = rs.getInt("units");
                if (available < unitsRequested) {
                    JOptionPane.showMessageDialog(this, "Not enough stock available.");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Blood group not found in stock.");
                return;
            }

            // Deduct units
            PreparedStatement deduct = con.prepareStatement("UPDATE blood_stock SET units = units - ? WHERE blood_group = ?");
            deduct.setInt(1, unitsRequested);
            deduct.setString(2, bloodGroup);
            deduct.executeUpdate();

            // Update request status
            PreparedStatement updateReq = con.prepareStatement("UPDATE blood_requests SET status = 'Fulfilled' WHERE request_id = ?");
            updateReq.setInt(1, requestId);
            updateReq.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request fulfilled successfully!");
            loadRequests();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
