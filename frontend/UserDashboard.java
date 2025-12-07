package frontend;

import backend.dao.*;
import backend.models.User;
import java.awt.*;
import javax.swing.*;

/**
 * User Dashboard Panel
 */
public class UserDashboard extends JPanel {
    private Main mainApp;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private JButton logoutButton;

    private VehicleDAO vehicleDAO;
    private BookingDAO bookingDAO;
    private PricingDAO pricingDAO;
    private UserDAO userDAO;

    private VehiclePanel vehiclePanel;
    private BookingPanel bookingPanel;
    private JPanel myBookingsPanel;
    private JPanel profilePanel;

    public UserDashboard(Main mainApp) {
        this.mainApp = mainApp;

        initializeDAOs();       // Create DAOs
        initializeComponents(); // Initialize UI components
        setupLayout();          // Layout
        setupEventHandlers();   // Events
    }

    private void initializeDAOs() {
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        bookingDAO = new BookingDAO(vehicleDAO); // ✅ Must pass vehicleDAO
        pricingDAO = new PricingDAO();
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, User");
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

        // Initialize panels with class fields
        vehiclePanel = new VehiclePanel(vehicleDAO, mainApp);
        vehiclePanel.setAdminMode(false); // User cannot add vehicles

        bookingPanel = new BookingPanel(vehicleDAO, userDAO, mainApp); // Use class fields

        myBookingsPanel = createMyBookingsPanel();
        profilePanel = createProfilePanel();

        tabbedPane.addTab("Browse Vehicles", vehiclePanel);
        tabbedPane.addTab("Make Booking", bookingPanel);
        tabbedPane.addTab("My Bookings", myBookingsPanel);
        tabbedPane.addTab("My Profile", profilePanel);
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

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.add(new JLabel("My Bookings - Feature Under Construction", SwingConstants.CENTER),
                  BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.add(new JLabel("My Profile - Feature Under Construction", SwingConstants.CENTER),
                  BorderLayout.CENTER);
        return panel;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName());
    }

    public void clearSession() {
        this.currentUser = null;
    }

    public void refresh() {
        if (vehiclePanel != null) vehiclePanel.refresh();
        if (bookingPanel != null) bookingPanel.refresh();
    }
}
