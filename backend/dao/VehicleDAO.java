package backend.dao;

import backend.db.DBConnection;
import backend.models.Vehicle;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vehicle operations
 */
public class VehicleDAO {

    // ✅ Optional constructor (for dependency injection if needed)
    public VehicleDAO() {}

    /**
     * Create a new vehicle
     * @param vehicle Vehicle object to create
     * @return true if successful, false otherwise
     */
    public boolean createVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (make, model, year, color, license_plate, vehicle_type, " +
                "fuel_type, transmission, seating_capacity, mileage, status, daily_rate, " +
                "image_path, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getColor());
            stmt.setString(5, vehicle.getLicensePlate());
            stmt.setString(6, vehicle.getVehicleType().getValue());
            stmt.setString(7, vehicle.getFuelType().getValue());
            stmt.setString(8, vehicle.getTransmission().getValue());
            stmt.setInt(9, vehicle.getSeatingCapacity());
            stmt.setInt(10, vehicle.getMileage());
            stmt.setString(11, vehicle.getStatus().getValue());
            stmt.setBigDecimal(12, vehicle.getDailyRate());
            stmt.setString(13, vehicle.getImagePath());
            stmt.setString(14, vehicle.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vehicle.setVehicleId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating vehicle: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get vehicle by ID
     * @param vehicleId vehicle ID
     * @return Vehicle object or null if not found
     */
    public Vehicle getVehicleById(int vehicleId) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get vehicle by license plate
     */
    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicles WHERE license_plate = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle by license plate: " + e.getMessage());
        }
        return null;
    }

    /**
     * ✅ Update vehicle status (added for integration with BookingDAO)
     * @param vehicleId vehicle ID
     * @param status new status
     * @return true if successful, false otherwise
     */
    public boolean updateVehicleStatus(int vehicleId, Vehicle.VehicleStatus status) {
        String sql = "UPDATE vehicles SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getValue());
            stmt.setInt(2, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        String sql = "SELECT * FROM vehicles ORDER BY created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByStatus(Vehicle.VehicleStatus status) {
        String sql = "SELECT * FROM vehicles WHERE status = ? ORDER BY created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by status: " + e.getMessage());
        }
        return vehicles;
    }

    public List<Vehicle> getAvailableVehicles() {
        return getVehiclesByStatus(Vehicle.VehicleStatus.AVAILABLE);
    }

    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType vehicleType) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_type = ? ORDER BY created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicles by type: " + e.getMessage());
        }
        return vehicles;
    }

    public List<Vehicle> getAvailableVehiclesByType(Vehicle.VehicleType vehicleType) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_type = ? AND status = 'available' ORDER BY created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting available vehicles by type: " + e.getMessage());
        }
        return vehicles;
    }

    public List<Vehicle> searchVehicles(String searchTerm) {
        String sql = "SELECT * FROM vehicles WHERE make LIKE ? OR model LIKE ? ORDER BY created_at DESC";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET make = ?, model = ?, year = ?, color = ?, " +
                "license_plate = ?, vehicle_type = ?, fuel_type = ?, transmission = ?, " +
                "seating_capacity = ?, mileage = ?, status = ?, daily_rate = ?, " +
                "image_path = ?, description = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getColor());
            stmt.setString(5, vehicle.getLicensePlate());
            stmt.setString(6, vehicle.getVehicleType().getValue());
            stmt.setString(7, vehicle.getFuelType().getValue());
            stmt.setString(8, vehicle.getTransmission().getValue());
            stmt.setInt(9, vehicle.getSeatingCapacity());
            stmt.setInt(10, vehicle.getMileage());
            stmt.setString(11, vehicle.getStatus().getValue());
            stmt.setBigDecimal(12, vehicle.getDailyRate());
            stmt.setString(13, vehicle.getImagePath());
            stmt.setString(14, vehicle.getDescription());
            stmt.setInt(15, vehicle.getVehicleId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
        }
        return false;
    }

    public boolean updateVehicleMileage(int vehicleId, int mileage) {
        String sql = "UPDATE vehicles SET mileage = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mileage);
            stmt.setInt(2, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle mileage: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
        }
        return false;
    }

    public boolean licensePlateExists(String licensePlate) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE license_plate = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking license plate: " + e.getMessage());
        }
        return false;
    }

    public int getVehicleCountByStatus(Vehicle.VehicleStatus status) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting vehicle count by status: " + e.getMessage());
        }
        return 0;
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setColor(rs.getString("color"));
        v.setLicensePlate(rs.getString("license_plate"));
        v.setVehicleType(Vehicle.VehicleType.fromString(rs.getString("vehicle_type")));
        v.setFuelType(Vehicle.FuelType.fromString(rs.getString("fuel_type")));
        v.setTransmission(Vehicle.Transmission.fromString(rs.getString("transmission")));
        v.setSeatingCapacity(rs.getInt("seating_capacity"));
        v.setMileage(rs.getInt("mileage"));
        v.setStatus(Vehicle.VehicleStatus.fromString(rs.getString("status")));
        v.setDailyRate(rs.getBigDecimal("daily_rate"));
        v.setImagePath(rs.getString("image_path"));
        v.setDescription(rs.getString("description"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) v.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) v.setUpdatedAt(updatedAt.toLocalDateTime());

        return v;
    }
}
