package backend.dao;

import backend.db.DBConnection;
import backend.models.Admin;
import backend.util.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Admin operations
 */
public class AdminDAO {
    
    /**
     * Create a new admin
     * @param admin Admin object to create
     * @return true if successful, false otherwise
     */
    public boolean createAdmin(Admin admin) {
        String sql = "INSERT INTO admins (username, email, password_hash, first_name, last_name, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getPasswordHash());
            stmt.setString(4, admin.getFirstName());
            stmt.setString(5, admin.getLastName());
            stmt.setString(6, admin.getRole());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        admin.setAdminId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating admin: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get admin by ID
     * @param adminId admin ID
     * @return Admin object or null if not found
     */
    public Admin getAdminById(int adminId) {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get admin by username
     * @param username username
     * @return Admin object or null if not found
     */
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin by username: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get admin by email
     * @param email email address
     * @return Admin object or null if not found
     */
    public Admin getAdminByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin by email: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Authenticate admin with username and password
     * @param username username
     * @param password plain text password
     * @return Admin object if authentication successful, null otherwise
     */
    public Admin authenticateAdmin(String username, String password) {
        Admin admin = getAdminByUsername(username);
        if (admin != null && PasswordUtils.verifyPassword(password, admin.getPasswordHash())) {
            return admin;
        }
        return null;
    }
    
    /**
     * Update admin information
     * @param admin Admin object with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateAdmin(Admin admin) {
        String sql = "UPDATE admins SET username = ?, email = ?, first_name = ?, last_name = ?, " +
                    "role = ?, updated_at = CURRENT_TIMESTAMP WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, admin.getFirstName());
            stmt.setString(4, admin.getLastName());
            stmt.setString(5, admin.getRole());
            stmt.setInt(6, admin.getAdminId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating admin: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Update admin password
     * @param adminId admin ID
     * @param newPasswordHash new password hash
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(int adminId, String newPasswordHash) {
        String sql = "UPDATE admins SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, adminId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating admin password: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Delete admin
     * @param adminId admin ID
     * @return true if successful, false otherwise
     */
    public boolean deleteAdmin(int adminId) {
        String sql = "DELETE FROM admins WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adminId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting admin: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get all admins
     * @return List of all admins
     */
    public List<Admin> getAllAdmins() {
        String sql = "SELECT * FROM admins ORDER BY created_at DESC";
        List<Admin> admins = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all admins: " + e.getMessage());
        }
        return admins;
    }
    
    /**
     * Get admins by role
     * @param role admin role
     * @return List of admins with specified role
     */
    public List<Admin> getAdminsByRole(String role) {
        String sql = "SELECT * FROM admins WHERE role = ? ORDER BY created_at DESC";
        List<Admin> admins = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    admins.add(mapResultSetToAdmin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admins by role: " + e.getMessage());
        }
        return admins;
    }
    
    /**
     * Check if admin username exists
     * @param username username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM admins WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin username existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Check if admin email exists
     * @param email email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM admins WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin email existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get admin count
     * @return total number of admins
     */
    public int getAdminCount() {
        String sql = "SELECT COUNT(*) FROM admins";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Map ResultSet to Admin object
     * @param rs ResultSet
     * @return Admin object
     * @throws SQLException if database error occurs
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setUsername(rs.getString("username"));
        admin.setEmail(rs.getString("email"));
        admin.setPasswordHash(rs.getString("password_hash"));
        admin.setFirstName(rs.getString("first_name"));
        admin.setLastName(rs.getString("last_name"));
        admin.setRole(rs.getString("role"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            admin.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            admin.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return admin;
    }
}
