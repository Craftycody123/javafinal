package frontend;

import backend.dao.AdminDAO;
import backend.dao.UserDAO;
import backend.models.Admin;
import backend.models.User;
import backend.util.PasswordUtils;
import backend.util.ValidationUtils;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class RegisterFrame extends JPanel {
    private Main mainApp;

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField licenseNumberField;
    private JTextField dateOfBirthField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton backButton;

    private UserDAO userDAO;
    private AdminDAO adminDAO;

    public RegisterFrame(Main mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAO();
        this.adminDAO = new AdminDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        phoneField = new JTextField(20);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        licenseNumberField = new JTextField(20);
        dateOfBirthField = new JTextField(20);

        roleComboBox = new JComboBox<>(new String[]{"User", "Admin"});

        Font fieldFont = new Font("Arial", Font.PLAIN, 12);
        usernameField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        firstNameField.setFont(fieldFont);
        lastNameField.setFont(fieldFont);
        phoneField.setFont(fieldFont);
        addressArea.setFont(fieldFont);
        licenseNumberField.setFont(fieldFont);
        dateOfBirthField.setFont(fieldFont);
        roleComboBox.setFont(fieldFont);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(100, 35));
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.setBackground(new Color(105, 105, 105));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Add form labels and fields (username, email, password, confirm password, first name, last name, phone, address, license number, date of birth)
        addField(mainPanel, gbc, 1, "Username*:", usernameField);
        addField(mainPanel, gbc, 2, "Email*:", emailField);
        addField(mainPanel, gbc, 3, "Password*:", passwordField);
        addField(mainPanel, gbc, 4, "Confirm Password*:", confirmPasswordField);
        addField(mainPanel, gbc, 5, "First Name*:", firstNameField);
        addField(mainPanel, gbc, 6, "Last Name*:", lastNameField);
        addField(mainPanel, gbc, 7, "Phone:", phoneField);
        addTextAreaField(mainPanel, gbc, 8, "Address:", addressArea);
        addField(mainPanel, gbc, 9, "License Number*:", licenseNumberField);
        addField(mainPanel, gbc, 10, "Date of Birth* (YYYY-MM-DD):", dateOfBirthField);

        // Role combo box
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Register as:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 11;
        mainPanel.add(roleComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int y, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.gridy = y;
        panel.add(field, gbc);
    }

    private void addTextAreaField(JPanel panel, GridBagConstraints gbc, int y, String labelText, JTextArea textArea) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, gbc);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(e -> performRegistration());
        backButton.addActionListener(e -> mainApp.showLoginPanel());
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();
        String licenseNumber = licenseNumberField.getText().trim();
        String dateOfBirthStr = dateOfBirthField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        // Validate required fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            firstName.isEmpty() || lastName.isEmpty() || licenseNumber.isEmpty() || dateOfBirthStr.isEmpty()) {
            mainApp.showErrorMessage("Please fill in all required fields (*)");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mainApp.showErrorMessage("Passwords do not match");
            passwordField.setText("");
            confirmPasswordField.setText("");
            passwordField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidUsername(username)) {
            mainApp.showErrorMessage("Invalid username format. Username must be 3-20 characters long and contain only letters, numbers, and underscores.");
            usernameField.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            mainApp.showErrorMessage("Invalid email format");
            emailField.requestFocus();
            return;
        }

        try {
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String passwordHash = PasswordUtils.createCombinedHash(password);

            if ("Admin".equalsIgnoreCase(role)) {
                if (adminDAO.usernameExists(username)) {
                    mainApp.showErrorMessage("Admin username already exists. Please choose a different username.");
                    usernameField.requestFocus();
                    return;
                }
                if (adminDAO.emailExists(email)) {
                    mainApp.showErrorMessage("Admin email already exists. Please use a different email address.");
                    emailField.requestFocus();
                    return;
                }

                Admin admin = new Admin(username, email, passwordHash, firstName, lastName, role);
                if (adminDAO.createAdmin(admin)) {
                    mainApp.showSuccessMessage("Admin registration successful! You can now login.");
                    mainApp.showLoginPanel();
                } else {
                    mainApp.showErrorMessage("Admin registration failed. Please try again.");
                }
            } else {
                if (userDAO.usernameExists(username)) {
                    mainApp.showErrorMessage("Username already exists. Please choose a different username.");
                    usernameField.requestFocus();
                    return;
                }
                if (userDAO.emailExists(email)) {
                    mainApp.showErrorMessage("Email already exists. Please use a different email address.");
                    emailField.requestFocus();
                    return;
                }

                User user = new User(username, email, passwordHash, firstName, lastName,
                        phone, address, licenseNumber, dateOfBirth);
                if (userDAO.createUser(user)) {
                    mainApp.showSuccessMessage("Registration successful! You can now login.");
                    mainApp.showLoginPanel();
                } else {
                    mainApp.showErrorMessage("Registration failed. Please try again.");
                }
            }
        } catch (DateTimeParseException e) {
            mainApp.showErrorMessage("Invalid date format. Please use YYYY-MM-DD.");
            dateOfBirthField.requestFocus();
        } catch (Exception e) {
            mainApp.showErrorMessage("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refresh() {
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        licenseNumberField.setText("");
        dateOfBirthField.setText("");
        roleComboBox.setSelectedIndex(0);
        usernameField.requestFocus();
    }
}
