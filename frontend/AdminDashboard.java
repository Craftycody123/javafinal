package frontend;

import backend.dao.*;
import backend.models.Admin;
import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JPanel {
    private Main mainApp;
    private Admin currentAdmin;
    private JTabbedPane tabbedPane;

    private JLabel welcomeLabel;
    private JButton logoutButton;

    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    private BookingDAO bookingDAO;
    private PricingDAO pricingDAO;
    private RentalHistoryDAO rentalHistoryDAO;

    private VehiclePanel vehiclePanel;
    private BookingPanel bookingPanel;
    private JPanel userManagementPanel;
    private JPanel pricingPanel;
    private JPanel reportsPanel;

    public AdminDashboard(Main mainApp) {
        this.mainApp = mainApp;

        initializeDAOs();         // Create DAO instances
        initializeComponents();   // Initialize UI Components
        setupLayout();            // Setup Layout
        setupEventHandlers();     // Setup Listeners
    }

    private void initializeDAOs() {
        userDAO = new UserDAO();
        vehicleDAO = new VehicleDAO();

        // BookingDAO must be created with vehicleDAO instance (constructor changed)
        bookingDAO = new BookingDAO(vehicleDAO);

        pricingDAO = new PricingDAO();
        rentalHistoryDAO = new RentalHistoryDAO();
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, Admin");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(25, 25, 112));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Pass DAOs correctly to panels
        vehiclePanel = new VehiclePanel(vehicleDAO, mainApp);
        vehiclePanel.setAdminMode(true);

        bookingPanel = new BookingPanel(vehicleDAO, userDAO, mainApp);  // Pass vehicleDAO & userDAO & mainApp
        userManagementPanel = createUserManagementPanel();
        pricingPanel = createPricingPanel();
        reportsPanel = createReportsPanel();

        tabbedPane.addTab("Vehicles", vehiclePanel);
        tabbedPane.addTab("Bookings", bookingPanel);
        tabbedPane.addTab("Users", userManagementPanel);
        tabbedPane.addTab("Pricing", pricingPanel);
        tabbedPane.addTab("Reports", reportsPanel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        logoutButton.addActionListener(e -> mainApp.logout());
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("User Management Panel - Under Construction"), BorderLayout.CENTER);
        panel.setBackground(new Color(240, 248, 255));
        return panel;
    }

    private JPanel createPricingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Pricing Management Panel - Under Construction"), BorderLayout.CENTER);
        panel.setBackground(new Color(240, 248, 255));
        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Reports Panel - Under Construction"), BorderLayout.CENTER);
        panel.setBackground(new Color(240, 248, 255));
        return panel;
    }

    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        welcomeLabel.setText("Welcome, " + admin.getFirstName() + " " + admin.getLastName());
    }

    public void clearSession() {
        this.currentAdmin = null;
    }

    public void refresh() {
        if (vehiclePanel != null) vehiclePanel.refresh();
        if (bookingPanel != null) bookingPanel.refresh();
    }
}
