package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class  {
    public static void main(String[] args) {
        String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC",
               dbName = "Assigned_Student_RA2411026010499",
               dbUrl  = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=UTC",
               user   = "root",
               pass   = "Pujan43@";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 1: Create database if not exists
            try (Connection conn = DriverManager.getConnection(baseUrl, user, pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            }

            // Step 2: Connect to new DB
            try (Connection conn = DriverManager.getConnection(dbUrl, user, pass);
                 Scanner sc = new Scanner(System.in)) {

                // Step 3: Create Vehicles table
                String createTable = "CREATE TABLE IF NOT EXISTS Vehicles (" +
                                     "vehicle_id INT PRIMARY KEY, " +
                                     "model VARCHAR(50), " +
                                     "status VARCHAR(20), " +
                                     "rent_per_day DECIMAL(10,2))";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(createTable);
                }

                int choice;
                do {
                    System.out.println("\n===== Vehicle Rental System =====");
                    System.out.println("1. Insert Vehicle");
                    System.out.println("2. Update Vehicle");
                    System.out.println("3. Delete Vehicle");
                    System.out.println("4. View All Vehicles");
                    System.out.println("5. Exit");
                    System.out.print("Enter choice: ");
                    choice = sc.nextInt();
                    sc.nextLine(); // consume newline

                    if (choice == 1) {
                        // Insert Vehicle
                        System.out.print("Enter Vehicle ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Model: ");
                        String model = sc.nextLine();
                        System.out.print("Enter Status (Available/Rented): ");
                        String status = sc.nextLine();
                        System.out.print("Enter Rent per Day: ");
                        double rent = sc.nextDouble();
                        sc.nextLine();

                        String sql = "INSERT INTO Vehicles VALUES (?, ?, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, id);
                            ps.setString(2, model);
                            ps.setString(3, status);
                            ps.setDouble(4, rent);
                            ps.executeUpdate();
                            System.out.println("Vehicle inserted successfully!");
                        } catch (SQLException e) {
                            System.out.println("Error inserting vehicle: " + e.getMessage());
                        }

                    } else if (choice == 2) {
                        // Update Vehicle
                        System.out.print("Enter Vehicle ID to update: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter new Model: ");
                        String model = sc.nextLine();
                        System.out.print("Enter new Status: ");
                        String status = sc.nextLine();
                        System.out.print("Enter new Rent per Day: ");
                        double rent = sc.nextDouble();
                        sc.nextLine();

                        String sql = "UPDATE Vehicles SET model=?, status=?, rent_per_day=? WHERE vehicle_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, model);
                            ps.setString(2, status);
                            ps.setDouble(3, rent);
                            ps.setInt(4, id);
                            int r = ps.executeUpdate();
                            if (r > 0) System.out.println("Vehicle updated successfully!");
                            else System.out.println("No vehicle found with ID " + id);
                        } catch (SQLException e) {
                            System.out.println("Error updating vehicle: " + e.getMessage());
                        }

                    } else if (choice == 3) {
                        // Delete Vehicle
                        System.out.print("Enter Vehicle ID to delete: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        String sql = "DELETE FROM Vehicles WHERE vehicle_id=?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, id);
                            int r = ps.executeUpdate();
                            if (r > 0) System.out.println("Vehicle deleted successfully!");
                            else System.out.println("No vehicle found with ID " + id);
                        } catch (SQLException e) {
                            System.out.println("Error deleting vehicle: " + e.getMessage());
                        }

                    } else if (choice == 4) {
                        // View All Vehicles
                        String sql = "SELECT * FROM Vehicles";
                        try (PreparedStatement ps = conn.prepareStatement(sql);
                             ResultSet rs = ps.executeQuery()) {
                            System.out.println("\n--- Vehicle Records ---");
                            while (rs.next()) {
                                System.out.println("ID: " + rs.getInt("vehicle_id") +
                                                   ", Model: " + rs.getString("model") +
                                                   ", Status: " + rs.getString("status") +
                                                   ", Rent/Day: " + rs.getDouble("rent_per_day"));
                            }
                        } catch (SQLException e) {
                            System.out.println("Error retrieving vehicles: " + e.getMessage());
                        }

                    } else if (choice == 5) {
                        System.out.println("Exiting... Goodbye!");
                    } else {
                        System.out.println("Invalid choice, try again.");
                    }

                } while (choice != 5);
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Add the driver to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
