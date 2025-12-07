package frontend;

import backend.dao.AdminDAO;
import backend.dao.UserDAO;
import backend.models.Admin;
import backend.models.User;
import backend.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login frame for user and admin authentication
 */
public class LoginFrame extends JPanel {
    private Main mainApp;
    
    // Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JRadioButton userRadioButton;
    private JRadioButton adminRadioButton;
    private ButtonGroup userTypeGroup;
    
    // DAOs
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    
    public LoginFrame(Main mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Username field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        // Register button
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(100, 35));
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        
        // User type radio buttons
        userRadioButton = new JRadioButton("User", true);
        adminRadioButton = new JRadioButton("Admin", false);
        userTypeGroup = new ButtonGroup();
        userTypeGroup.add(userRadioButton);
        userTypeGroup.add(adminRadioButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Vehicle Rental System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(105, 105, 105));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(subtitleLabel, gbc);
        
        // User type selection
        JPanel userTypePanel = new JPanel(new FlowLayout());
        userTypePanel.setBackground(new Color(240, 248, 255));
        userTypePanel.add(userRadioButton);
        userTypePanel.add(adminRadioButton);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(userTypePanel, gbc);
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showRegisterPanel();
            }
        });
        
        // Enter key login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (username.isEmpty()) {
            mainApp.showErrorMessage("Please enter username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            mainApp.showErrorMessage("Please enter password");
            passwordField.requestFocus();
            return;
        }
        
        if (!ValidationUtils.isValidUsername(username)) {
            mainApp.showErrorMessage("Invalid username format");
            usernameField.requestFocus();
            return;
        }
        
        try {
            if (userRadioButton.isSelected()) {
                // User login
                User user = userDAO.authenticateUser(username, password);
                if (user != null) {
                    mainApp.showSuccessMessage("Login successful! Welcome, " + user.getFirstName());
                    mainApp.getUserDashboard().setCurrentUser(user);
                    mainApp.showUserDashboard();
                    clearFields();
                } else {
                    mainApp.showErrorMessage("Invalid username or password");
                    passwordField.setText("");
                    passwordField.requestFocus();
                }
            } else {
                // Admin login
                Admin admin = adminDAO.authenticateAdmin(username, password);
                if (admin != null) {
                    mainApp.showSuccessMessage("Login successful! Welcome, " + admin.getFirstName());
                    mainApp.getAdminDashboard().setCurrentAdmin(admin);
                    mainApp.showAdminDashboard();
                    clearFields();
                } else {
                    mainApp.showErrorMessage("Invalid username or password");
                    passwordField.setText("");
                    passwordField.requestFocus();
                }
            }
        } catch (Exception e) {
            mainApp.showErrorMessage("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        userRadioButton.setSelected(true);
    }
    
    /**
     * Refresh the login frame
     */
    public void refresh() {
        clearFields();
        usernameField.requestFocus();
    }
}
