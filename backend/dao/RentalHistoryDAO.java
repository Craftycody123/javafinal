package backend.dao;

import backend.db.DBConnection;
import backend.models.RentalHistory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for RentalHistory operations
 */
public class RentalHistoryDAO {
    
    /**
     * Create a new rental history entry
     * @param rentalHistory RentalHistory object to create
     * @return true if successful, false otherwise
     */
    public boolean createRentalHistory(RentalHistory rentalHistory) {
        String sql = "INSERT INTO rental_history (booking_id, user_id, vehicle_id, start_date, end_date, " +
                    "actual_return_date, total_amount, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, rentalHistory.getBookingId());
            stmt.setInt(2, rentalHistory.getUserId());
            stmt.setInt(3, rentalHistory.getVehicleId());
            stmt.setDate(4, Date.valueOf(rentalHistory.getStartDate()));
            stmt.setDate(5, Date.valueOf(rentalHistory.getEndDate()));
            
            if (rentalHistory.getActualReturnDate() != null) {
                stmt.setDate(6, Date.valueOf(rentalHistory.getActualReturnDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setBigDecimal(7, rentalHistory.getTotalAmount());
            stmt.setString(8, rentalHistory.getStatus().getValue());
            stmt.setString(9, rentalHistory.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rentalHistory.setHistoryId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating rental history: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get rental history by ID
     * @param historyId history ID
     * @return RentalHistory object or null if not found
     */
    public RentalHistory getRentalHistoryById(int historyId) {
        String sql = "SELECT * FROM rental_history WHERE history_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, historyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRentalHistory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get rental history by booking ID
     * @param bookingId booking ID
     * @return RentalHistory object or null if not found
     */
    public RentalHistory getRentalHistoryByBookingId(int bookingId) {
        String sql = "SELECT * FROM rental_history WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRentalHistory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history by booking ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get rental history by user ID
     * @param userId user ID
     * @return List of rental history for the user
     */
    public List<RentalHistory> getRentalHistoryByUserId(int userId) {
        String sql = "SELECT * FROM rental_history WHERE user_id = ? ORDER BY created_at DESC";
        List<RentalHistory> historyList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historyList.add(mapResultSetToRentalHistory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history by user ID: " + e.getMessage());
        }
        return historyList;
    }
    
    /**
     * Get rental history by vehicle ID
     * @param vehicleId vehicle ID
     * @return List of rental history for the vehicle
     */
    public List<RentalHistory> getRentalHistoryByVehicleId(int vehicleId) {
        String sql = "SELECT * FROM rental_history WHERE vehicle_id = ? ORDER BY created_at DESC";
        List<RentalHistory> historyList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historyList.add(mapResultSetToRentalHistory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history by vehicle ID: " + e.getMessage());
        }
        return historyList;
    }
    
    /**
     * Get all rental history
     * @return List of all rental history
     */
    public List<RentalHistory> getAllRentalHistory() {
        String sql = "SELECT * FROM rental_history ORDER BY created_at DESC";
        List<RentalHistory> historyList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                historyList.add(mapResultSetToRentalHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all rental history: " + e.getMessage());
        }
        return historyList;
    }
    
    /**
     * Get rental history by status
     * @param status rental status
     * @return List of rental history with specified status
     */
    public List<RentalHistory> getRentalHistoryByStatus(RentalHistory.RentalStatus status) {
        String sql = "SELECT * FROM rental_history WHERE status = ? ORDER BY created_at DESC";
        List<RentalHistory> historyList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historyList.add(mapResultSetToRentalHistory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history by status: " + e.getMessage());
        }
        return historyList;
    }
    
    /**
     * Get completed rental history
     * @return List of completed rental history
     */
    public List<RentalHistory> getCompletedRentalHistory() {
        return getRentalHistoryByStatus(RentalHistory.RentalStatus.COMPLETED);
    }
    
    /**
     * Get rental history by date range
     * @param startDate start date
     * @param endDate end date
     * @return List of rental history in the date range
     */
    public List<RentalHistory> getRentalHistoryByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM rental_history WHERE start_date >= ? AND end_date <= ? ORDER BY start_date ASC";
        List<RentalHistory> historyList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historyList.add(mapResultSetToRentalHistory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history by date range: " + e.getMessage());
        }
        return historyList;
    }
    
    /**
     * Get recent rental history (last 30 days)
     * @return List of recent rental history
     */
    public List<RentalHistory> getRecentRentalHistory() {
        String sql = "SELECT * FROM rental_history WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) ORDER BY created_at DESC";
        List<RentalHistory> historyList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                historyList.add(mapResultSetToRentalHistory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent rental history: " + e.getMessage());
        }
        return historyList;
    }
    
    /**
     * Update rental history information
     * @param rentalHistory RentalHistory object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateRentalHistory(RentalHistory rentalHistory) {
        String sql = "UPDATE rental_history SET booking_id = ?, user_id = ?, vehicle_id = ?, " +
                    "start_date = ?, end_date = ?, actual_return_date = ?, total_amount = ?, " +
                    "status = ?, notes = ? WHERE history_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rentalHistory.getBookingId());
            stmt.setInt(2, rentalHistory.getUserId());
            stmt.setInt(3, rentalHistory.getVehicleId());
            stmt.setDate(4, Date.valueOf(rentalHistory.getStartDate()));
            stmt.setDate(5, Date.valueOf(rentalHistory.getEndDate()));
            
            if (rentalHistory.getActualReturnDate() != null) {
                stmt.setDate(6, Date.valueOf(rentalHistory.getActualReturnDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setBigDecimal(7, rentalHistory.getTotalAmount());
            stmt.setString(8, rentalHistory.getStatus().getValue());
            stmt.setString(9, rentalHistory.getNotes());
            stmt.setInt(10, rentalHistory.getHistoryId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating rental history: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update actual return date
     * @param historyId history ID
     * @param actualReturnDate actual return date
     * @return true if successful, false otherwise
     */
    public boolean updateActualReturnDate(int historyId, LocalDate actualReturnDate) {
        String sql = "UPDATE rental_history SET actual_return_date = ? WHERE history_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(actualReturnDate));
            stmt.setInt(2, historyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating actual return date: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update rental status
     * @param historyId history ID
     * @param status new status
     * @return true if successful, false otherwise
     */
    public boolean updateRentalStatus(int historyId, RentalHistory.RentalStatus status) {
        String sql = "UPDATE rental_history SET status = ? WHERE history_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            stmt.setInt(2, historyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating rental status: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Add notes to rental history
     * @param historyId history ID
     * @param notes notes to add
     * @return true if successful, false otherwise
     */
    public boolean addNotes(int historyId, String notes) {
        String sql = "UPDATE rental_history SET notes = ? WHERE history_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notes);
            stmt.setInt(2, historyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding notes: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Delete rental history
     * @param historyId history ID
     * @return true if successful, false otherwise
     */
    public boolean deleteRentalHistory(int historyId) {
        String sql = "DELETE FROM rental_history WHERE history_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, historyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting rental history: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get rental history count by status
     * @param status rental status
     * @return count of rental history with specified status
     */
    public int getRentalHistoryCountByStatus(RentalHistory.RentalStatus status) {
        String sql = "SELECT COUNT(*) FROM rental_history WHERE status = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting rental history count by status: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Get total revenue from completed rentals
     * @return total revenue
     */
    public BigDecimal getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM rental_history WHERE status = 'completed'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get average rental duration
     * @return average rental duration in days
     */
    public double getAverageRentalDuration() {
        String sql = "SELECT AVG(DATEDIFF(end_date, start_date)) FROM rental_history WHERE status = 'completed'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average rental duration: " + e.getMessage());
        }
        return 0.0;
    }
    
    /**
     * Map ResultSet to RentalHistory object
     * @param rs ResultSet
     * @return RentalHistory object
     * @throws SQLException if database error occurs
     */
    private RentalHistory mapResultSetToRentalHistory(ResultSet rs) throws SQLException {
        RentalHistory rentalHistory = new RentalHistory();
        rentalHistory.setHistoryId(rs.getInt("history_id"));
        rentalHistory.setBookingId(rs.getInt("booking_id"));
        rentalHistory.setUserId(rs.getInt("user_id"));
        rentalHistory.setVehicleId(rs.getInt("vehicle_id"));
        rentalHistory.setStartDate(rs.getDate("start_date").toLocalDate());
        rentalHistory.setEndDate(rs.getDate("end_date").toLocalDate());
        
        Date actualReturnDate = rs.getDate("actual_return_date");
        if (actualReturnDate != null) {
            rentalHistory.setActualReturnDate(actualReturnDate.toLocalDate());
        }
        
        rentalHistory.setTotalAmount(rs.getBigDecimal("total_amount"));
        rentalHistory.setStatus(RentalHistory.RentalStatus.fromString(rs.getString("status")));
        rentalHistory.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rentalHistory.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return rentalHistory;
    }
}
