package frontend;

import java.awt.*;
import javax.swing.*;

/**
 * Main application entry point for Vehicle Rental System
 */
public class Main extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Panels
    private LoginFrame loginFrame;
    private RegisterFrame registerFrame;
    private AdminDashboard adminDashboard;
    private UserDashboard userDashboard;
    
    public Main() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Vehicle Rental System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Initialize panels
        loginFrame = new LoginFrame(this);
        registerFrame = new RegisterFrame(this);
        adminDashboard = new AdminDashboard(this);
        userDashboard = new UserDashboard(this);
        
        // Add panels to main panel
        mainPanel.add(loginFrame, "LOGIN");
        mainPanel.add(registerFrame, "REGISTER");
        mainPanel.add(adminDashboard, "ADMIN_DASHBOARD");
        mainPanel.add(userDashboard, "USER_DASHBOARD");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // Show login panel by default
        showLoginPanel();
    }
    
    private void setupEventHandlers() {
        // Add any global event handlers here if needed
    }
    
    /**
     * Show login panel
     */
    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
        loginFrame.refresh();
    }
    
    /**
     * Show register panel
     */
    public void showRegisterPanel() {
        cardLayout.show(mainPanel, "REGISTER");
        registerFrame.refresh();
    }
    
    /**
     * Show admin dashboard
     */
    public void showAdminDashboard() {
        cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
        adminDashboard.refresh();
    }
    
    /**
     * Show user dashboard
     */
    public void showUserDashboard() {
        cardLayout.show(mainPanel, "USER_DASHBOARD");
        userDashboard.refresh();
    }
    
    /**
     * Get user dashboard instance
     */
    public UserDashboard getUserDashboard() {
        return userDashboard;
    }
    
    /**
     * Get admin dashboard instance
     */
    public AdminDashboard getAdminDashboard() {
        return adminDashboard;
    }
    
    /**
     * Logout and return to login
     */
    public void logout() {
        // Clear any user session data
        adminDashboard.clearSession();
        userDashboard.clearSession();
        
        // Show login panel
        showLoginPanel();
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Set look and feel - using cross-platform for compatibility
        try {
UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Create and show the main window
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main main = new Main();
                    main.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                        "Error starting application: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Show error message dialog
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show success message dialog
     */
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     */
    public boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Show input dialog
     */
    public String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE);
    }
}
