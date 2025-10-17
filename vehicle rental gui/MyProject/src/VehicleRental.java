package jdbc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class VehicleRentalApp extends JFrame {
    private DatabaseHelper db;
    private DefaultTableModel tableModel;
    private JTable table;

    public VehicleRentalApp() {
        db = new DatabaseHelper();

        setTitle("Vehicle Rental System");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Model", "Status", "Rent/Day"}, 0);
        table = new JTable(tableModel);
        loadVehicles();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel panel = new JPanel();
        JButton addBtn = new JButton("Add Vehicle");
        JButton updateBtn = new JButton("Update Vehicle");
        JButton deleteBtn = new JButton("Delete Vehicle");
        JButton refreshBtn = new JButton("Refresh");

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        add(panel, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addVehicle());
        updateBtn.addActionListener(e -> updateVehicle());
        deleteBtn.addActionListener(e -> deleteVehicle());
        refreshBtn.addActionListener(e -> loadVehicles());
    }

    private void loadVehicles() {
        tableModel.setRowCount(0);
        try {
            List<String[]> vehicles = db.getAllVehicles();
            for (String[] v : vehicles) {
                tableModel.addRow(v);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + e.getMessage());
        }
    }

    private void addVehicle() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Vehicle ID:"));
            String model = JOptionPane.showInputDialog(this, "Enter Model:");
            String status = JOptionPane.showInputDialog(this, "Enter Status (Available/Rented):");
            double rent = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Rent/Day:"));

            db.insertVehicle(id, model, status, rent);
            loadVehicles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding vehicle: " + e.getMessage());
        }
    }

    private void updateVehicle() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Vehicle ID to Update:"));
            String model = JOptionPane.showInputDialog(this, "Enter New Model:");
            String status = JOptionPane.showInputDialog(this, "Enter New Status:");
            double rent = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter New Rent/Day:"));

            db.updateVehicle(id, model, status, rent);
            loadVehicles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating vehicle: " + e.getMessage());
        }
    }

    private void deleteVehicle() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Vehicle ID to Delete:"));
            db.deleteVehicle(id);
            loadVehicles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting vehicle: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VehicleRentalApp().setVisible(true);
        });
    }
}
