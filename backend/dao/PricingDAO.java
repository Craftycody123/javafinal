package backend.dao;

import backend.db.DBConnection;
import backend.models.Pricing;
import backend.models.Vehicle;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Pricing operations
 */
public class PricingDAO {
    
    /**
     * Create a new pricing rule
     * @param pricing Pricing object to create
     * @return true if successful, false otherwise
     */
    public boolean createPricing(Pricing pricing) {
        String sql = "INSERT INTO pricing (vehicle_type, base_daily_rate, weekend_multiplier, " +
                    "holiday_multiplier, long_term_discount, insurance_daily_rate) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, pricing.getVehicleType().getValue());
            stmt.setBigDecimal(2, pricing.getBaseDailyRate());
            stmt.setBigDecimal(3, pricing.getWeekendMultiplier());
            stmt.setBigDecimal(4, pricing.getHolidayMultiplier());
            stmt.setBigDecimal(5, pricing.getLongTermDiscount());
            stmt.setBigDecimal(6, pricing.getInsuranceDailyRate());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pricing.setPricingId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating pricing: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get pricing by ID
     * @param pricingId pricing ID
     * @return Pricing object or null if not found
     */
    public Pricing getPricingById(int pricingId) {
        String sql = "SELECT * FROM pricing WHERE pricing_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pricingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPricing(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting pricing by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get pricing by vehicle type
     * @param vehicleType vehicle type
     * @return Pricing object or null if not found
     */
    public Pricing getPricingByVehicleType(Vehicle.VehicleType vehicleType) {
        String sql = "SELECT * FROM pricing WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleType.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPricing(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting pricing by vehicle type: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all pricing rules
     * @return List of all pricing rules
     */
    public List getAllPricing() {
        String sql = "SELECT * FROM pricing ORDER BY vehicle_type";
        List pricingList = new ArrayList();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                pricingList.add(mapResultSetToPricing(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pricing: " + e.getMessage());
        }
        return pricingList;
    }
    
    /**
     * Update pricing information
     * @param pricing Pricing object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updatePricing(Pricing pricing) {
        String sql = "UPDATE pricing SET vehicle_type = ?, base_daily_rate = ?, weekend_multiplier = ?, " +
                    "holiday_multiplier = ?, long_term_discount = ?, insurance_daily_rate = ?, " +
                    "updated_at = CURRENT_TIMESTAMP WHERE pricing_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pricing.getVehicleType().getValue());
            stmt.setBigDecimal(2, pricing.getBaseDailyRate());
            stmt.setBigDecimal(3, pricing.getWeekendMultiplier());
            stmt.setBigDecimal(4, pricing.getHolidayMultiplier());
            stmt.setBigDecimal(5, pricing.getLongTermDiscount());
            stmt.setBigDecimal(6, pricing.getInsuranceDailyRate());
            stmt.setInt(7, pricing.getPricingId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pricing: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update base daily rate for a vehicle type
     * @param vehicleType vehicle type
     * @param newRate new base daily rate
     * @return true if successful, false otherwise
     */
    public boolean updateBaseDailyRate(Vehicle.VehicleType vehicleType, BigDecimal newRate) {
        String sql = "UPDATE pricing SET base_daily_rate = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, newRate);
            stmt.setString(2, vehicleType.getValue());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating base daily rate: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update weekend multiplier for a vehicle type
     * @param vehicleType vehicle type
     * @param multiplier new weekend multiplier
     * @return true if successful, false otherwise
     */
    public boolean updateWeekendMultiplier(Vehicle.VehicleType vehicleType, BigDecimal multiplier) {
        String sql = "UPDATE pricing SET weekend_multiplier = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, multiplier);
            stmt.setString(2, vehicleType.getValue());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating weekend multiplier: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update holiday multiplier for a vehicle type
     * @param vehicleType vehicle type
     * @param multiplier new holiday multiplier
     * @return true if successful, false otherwise
     */
    public boolean updateHolidayMultiplier(Vehicle.VehicleType vehicleType, BigDecimal multiplier) {
        String sql = "UPDATE pricing SET holiday_multiplier = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, multiplier);
            stmt.setString(2, vehicleType.getValue());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating holiday multiplier: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update long term discount for a vehicle type
     * @param vehicleType vehicle type
     * @param discount new long term discount
     * @return true if successful, false otherwise
     */
    public boolean updateLongTermDiscount(Vehicle.VehicleType vehicleType, BigDecimal discount) {
        String sql = "UPDATE pricing SET long_term_discount = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, discount);
            stmt.setString(2, vehicleType.getValue());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating long term discount: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update insurance daily rate for a vehicle type
     * @param vehicleType vehicle type
     * @param rate new insurance daily rate
     * @return true if successful, false otherwise
     */
    public boolean updateInsuranceDailyRate(Vehicle.VehicleType vehicleType, BigDecimal rate) {
        String sql = "UPDATE pricing SET insurance_daily_rate = ?, updated_at = CURRENT_TIMESTAMP WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, rate);
            stmt.setString(2, vehicleType.getValue());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating insurance daily rate: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Delete pricing rule
     * @param pricingId pricing ID
     * @return true if successful, false otherwise
     */
    public boolean deletePricing(int pricingId) {
        String sql = "DELETE FROM pricing WHERE pricing_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pricingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pricing: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Check if pricing exists for vehicle type
     * @param vehicleType vehicle type
     * @return true if exists, false otherwise
     */
    public boolean pricingExists(Vehicle.VehicleType vehicleType) {
        String sql = "SELECT COUNT(*) FROM pricing WHERE vehicle_type = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleType.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking pricing existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get pricing count
     * @return total number of pricing rules
     */
    public int getPricingCount() {
        String sql = "SELECT COUNT(*) FROM pricing";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pricing count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Calculate total cost for a booking
     * @param vehicleType vehicle type
     * @param days number of days
     * @param isWeekend whether it's weekend
     * @param isHoliday whether it's holiday
     * @param includeInsurance whether to include insurance
     * @return total cost
     */
    public BigDecimal calculateTotalCost(Vehicle.VehicleType vehicleType, int days, 
                                       boolean isWeekend, boolean isHoliday, boolean includeInsurance) {
        Pricing pricing = getPricingByVehicleType(vehicleType);
        if (pricing == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal baseCost = pricing.calculateTotalRate(days, isWeekend, isHoliday, pricing.isLongTermRental(days));
        
        if (includeInsurance) {
            BigDecimal insuranceCost = pricing.calculateTotalInsuranceRate(days);
            baseCost = baseCost.add(insuranceCost);
        }
        
        return baseCost;
    }
    
    /**
     * Map ResultSet to Pricing object
     * @param rs ResultSet
     * @return Pricing object
     * @throws SQLException if database error occurs
     */
    private Pricing mapResultSetToPricing(ResultSet rs) throws SQLException {
        Pricing pricing = new Pricing();
        pricing.setPricingId(rs.getInt("pricing_id"));
        pricing.setVehicleType(Vehicle.VehicleType.fromString(rs.getString("vehicle_type")));
        pricing.setBaseDailyRate(rs.getBigDecimal("base_daily_rate"));
        pricing.setWeekendMultiplier(rs.getBigDecimal("weekend_multiplier"));
        pricing.setHolidayMultiplier(rs.getBigDecimal("holiday_multiplier"));
        pricing.setLongTermDiscount(rs.getBigDecimal("long_term_discount"));
        pricing.setInsuranceDailyRate(rs.getBigDecimal("insurance_daily_rate"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            pricing.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            pricing.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return pricing;
    }
}
