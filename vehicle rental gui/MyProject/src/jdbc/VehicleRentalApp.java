package jdbc;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class VehicleRentalApp {
    static Connection conn;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VehicleRentalApp().landingPage());
    }

    VehicleRentalApp() {
        try {
        	String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String dbName = "Assigned_Student_RA2411026010499"; // your DB
            String dbUrl = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String user = "root";
            String pass = "Pujan43@";

            // Load driver and create DB if not exists
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection tmpConn = DriverManager.getConnection(baseUrl, user, pass);
            Statement stmt = tmpConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.close();
            tmpConn.close();

            // Connect to the DB
            conn = DriverManager.getConnection(dbUrl, user, pass);
            stmt = conn.createStatement();
            String createTable = "CREATE TABLE IF NOT EXISTS Vehicles (" +
                    "vehicle_id INT PRIMARY KEY, " +
                    "model VARCHAR(50), " +
                    "status VARCHAR(20), " +
                    "rent_per_day DECIMAL(10,2))";
            stmt.executeUpdate(createTable);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void landingPage() {
        JFrame f = new JFrame("Vehicle Rental System");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 300);
        f.setLayout(new BorderLayout());

        JLabel title = new JLabel("Vehicle Rental System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        f.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 150));

        String[] options = {"Insert Vehicle", "Update Vehicle", "Delete Vehicle", "View Vehicles", "Exit"};

        for (String opt : options) {
            JButton btn = new JButton(opt);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(400, 50));
            btn.setPreferredSize(new Dimension(400, 50));
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            switch (opt) {
                case "Insert Vehicle" -> btn.addActionListener(e -> insertPage());
                case "Update Vehicle" -> btn.addActionListener(e -> updatePage());
                case "Delete Vehicle" -> btn.addActionListener(e -> deletePage());
                case "View Vehicles" -> btn.addActionListener(e -> viewPage());
                case "Exit" -> btn.addActionListener(e -> System.exit(0));
            }
        }

        f.add(buttonPanel, BorderLayout.CENTER);
        f.setVisible(true);
    }

    void insertPage() {
        JFrame f = new JFrame("Insert Vehicle");
        f.setSize(350, 250);
        f.setLayout(new GridLayout(5, 2, 8, 8));

        JTextField id = new JTextField();
        JTextField model = new JTextField();
        JTextField status = new JTextField();
        JTextField rent = new JTextField();

        f.add(new JLabel("Vehicle ID:")); f.add(id);
        f.add(new JLabel("Model:")); f.add(model);
        f.add(new JLabel("Status:")); f.add(status);
        f.add(new JLabel("Rent per Day:")); f.add(rent);

        JButton save = new JButton("Insert");
        save.addActionListener(e -> {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Vehicles VALUES (?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.setString(2, model.getText());
                ps.setString(3, status.getText());
                ps.setDouble(4, Double.parseDouble(rent.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(f, "Vehicle Inserted!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        f.add(new JLabel()); f.add(save);
        f.setVisible(true);
    }

    void updatePage() {
        JFrame f = new JFrame("Update Vehicle");
        f.setSize(350, 250);
        f.setLayout(new GridLayout(5, 2, 8, 8));

        JTextField id = new JTextField();
        JTextField model = new JTextField();
        JTextField status = new JTextField();
        JTextField rent = new JTextField();

        f.add(new JLabel("Vehicle ID (to update):")); f.add(id);
        f.add(new JLabel("New Model:")); f.add(model);
        f.add(new JLabel("New Status:")); f.add(status);
        f.add(new JLabel("New Rent per Day:")); f.add(rent);

        JButton update = new JButton("Update");
        update.addActionListener(e -> {
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Vehicles SET model=?, status=?, rent_per_day=? WHERE vehicle_id=?");
                ps.setString(1, model.getText());
                ps.setString(2, status.getText());
                ps.setDouble(3, Double.parseDouble(rent.getText()));
                ps.setInt(4, Integer.parseInt(id.getText()));
                int r = ps.executeUpdate();
                if(r>0) JOptionPane.showMessageDialog(f, "Vehicle Updated!");
                else JOptionPane.showMessageDialog(f, "No vehicle found with that ID");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        f.add(new JLabel()); f.add(update);
        f.setVisible(true);
    }

    void deletePage() {
        JFrame f = new JFrame("Delete Vehicle");
        f.setSize(300, 180);
        f.setLayout(new GridLayout(2, 2, 8, 8));

        JTextField id = new JTextField();
        f.add(new JLabel("Vehicle ID:")); f.add(id);

        JButton delete = new JButton("Delete");
        delete.addActionListener(e -> {
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM Vehicles WHERE vehicle_id=?");
                ps.setInt(1, Integer.parseInt(id.getText()));
                int r = ps.executeUpdate();
                if(r>0) JOptionPane.showMessageDialog(f, "Vehicle Deleted!");
                else JOptionPane.showMessageDialog(f, "No vehicle found with that ID");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        f.add(new JLabel()); f.add(delete);
        f.setVisible(true);
    }

    void viewPage() {
        JFrame f = new JFrame("View Vehicles");
        f.setSize(500, 300);

        String[] cols = {"Vehicle ID", "Model", "Status", "Rent/Day"};
        String[][] data = new String[0][];
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery("SELECT * FROM Vehicles");
            rs.last();
            int rows = rs.getRow();
            rs.beforeFirst();
            data = new String[rows][4];
            int i = 0;
            while (rs.next()) {
                data[i][0] = String.valueOf(rs.getInt(1));
                data[i][1] = rs.getString(2);
                data[i][2] = rs.getString(3);
                data[i][3] = String.valueOf(rs.getDouble(4));
                i++;
            }
        } catch (Exception e) { e.printStackTrace(); }

        JTable table = new JTable(data, cols);
        f.add(new JScrollPane(table));
        f.setVisible(true);
    }
}
