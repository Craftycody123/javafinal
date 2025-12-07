package backend.models;

import java.time.LocalDateTime;

/**
 * Admin model class representing an administrator in the vehicle rental system
 */
public class Admin {
    private int adminId;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Admin() {}
    
    // Constructor for new admin creation
    public Admin(String username, String email, String passwordHash, String firstName, 
                 String lastName, String role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    // Full constructor
    public Admin(int adminId, String username, String email, String passwordHash, 
                 String firstName, String lastName, String role, LocalDateTime createdAt, 
                 LocalDateTime updatedAt) {
        this.adminId = adminId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isSuperAdmin() {
        return "super_admin".equalsIgnoreCase(role);
    }
    
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role) || isSuperAdmin();
    }
    
    public boolean hasPermission(String permission) {
        if (isSuperAdmin()) {
            return true;
        }
        
        switch (permission.toLowerCase()) {
            case "manage_users":
            case "manage_vehicles":
            case "manage_bookings":
            case "view_reports":
                return isAdmin();
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return adminId == admin.adminId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(adminId);
    }
}
