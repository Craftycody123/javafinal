package frontend;

import backend.dao.*;
import backend.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class BookingDialog extends JDialog {
    private Booking booking;
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;
    private Main mainApp;
    private boolean bookingAdded = false;

    private JComboBox<User> userCombo;
    private JTextField startDatePicker, endDatePicker;
    private JTextField pickupLocationField, dropoffLocationField, totalAmountField;
    private JComboBox<Booking.BookingStatus> statusCombo;
    private JComboBox<Booking.PaymentStatus> paymentStatusCombo;

    private JTable vehicleTable;
    private DefaultTableModel vehicleTableModel;
    private Vehicle selectedVehicle;

    public BookingDialog(Frame parent, String title, Booking booking, BookingDAO bookingDAO,
                         VehicleDAO vehicleDAO, UserDAO userDAO, Main mainApp) {
        super(parent, title, true);
        this.booking = booking;
        this.bookingDAO = bookingDAO;
        this.vehicleDAO = vehicleDAO;
        this.userDAO = userDAO;
        this.mainApp = mainApp;

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        loadVehiclesIntoTable(); // Load vehicles always for table population

        if (booking != null) {
            loadBookingData();
            selectVehicleInTable(booking.getVehicleId());
        }
    }

    private void initializeComponents() {
        userCombo = new JComboBox<>();
        loadUsers();

        // Initialize date pickers - replaced with manual entry JTextFields
        startDatePicker = createDatePicker();
        endDatePicker = createDatePicker();
        
        // Set default dates (today and tomorrow)
        LocalDate today = LocalDate.now();
        setDatePickerDate(startDatePicker, today);
        setDatePickerDate(endDatePicker, today.plusDays(1));

        pickupLocationField = new JTextField(15);
        dropoffLocationField = new JTextField(15);
        totalAmountField = new JTextField(15);
        totalAmountField.setEditable(false);
        totalAmountField.setBackground(Color.LIGHT_GRAY);

        statusCombo = new JComboBox<>(Booking.BookingStatus.values());
        paymentStatusCombo = new JComboBox<>(Booking.PaymentStatus.values());

        String[] vehicleColumns = {"ID", "Make", "Model", "Year", "License Plate"};
        vehicleTableModel = new DefaultTableModel(vehicleColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        vehicleTable = new JTable(vehicleTableModel);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JTextField createDatePicker() {
        JTextField dateField = new JTextField(10);
        dateField.setToolTipText("Enter date in YYYY-MM-DD format");
        return dateField;
    }

    private void setDatePickerDate(JTextField picker, LocalDate date) {
        if (date == null || picker == null) return;
        picker.setText(date.toString());
    }

    private LocalDate getDateFromPicker(JTextField picker) {
        if (picker == null) return null;
        
        String text = picker.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(text);
        } catch (Exception e) {
            System.err.println("Error parsing date from text: " + e.getMessage());
            return null;
        }
    }

    private void loadUsers() {
        userCombo.removeAllItems();
        try {
            List<User> users = userDAO.getAllUsers();
            System.out.println("DEBUG: Loading users - found " + users.size() + " users");
            for (User  u : users) {
                userCombo.addItem(u);
                System.out.println("  - User: " + u.getUsername() + " (ID: " + u.getUserId() + ")");
            }
            if (users.isEmpty()) {
                System.out.println("WARNING: No users found in database!");
            }
        } catch (Exception e) {
            System.err.println("ERROR loading users: " + e.getMessage());
            e.printStackTrace();
            mainApp.showErrorMessage("Error loading users: " + e.getMessage());
        }
    }

    private void loadVehiclesIntoTable() {
        vehicleTableModel.setRowCount(0);
        try {
            List<Vehicle> vehicles = vehicleDAO.getAvailableVehicles();
            System.out.println("DEBUG: Loading vehicles - found " + vehicles.size() + " vehicles");
            for (Vehicle v : vehicles) {
                Object[] row = {v.getVehicleId(), v.getMake(), v.getModel(), v.getYear(), v.getLicensePlate()};
                vehicleTableModel.addRow(row);
                System.out.println("  - Vehicle: " + v.getMake() + " " + v.getModel() + " (ID: " + v.getVehicleId() + ")");
            }
            if (vehicles.isEmpty()) {
                System.out.println("WARNING: No available vehicles found!");
            }
        } catch (Exception e) {
            System.err.println("ERROR loading vehicles: " + e.getMessage());
            e.printStackTrace();
            mainApp.showErrorMessage("Error loading vehicles: " + e.getMessage());
        }
    }

    private void selectVehicleInTable(int vehicleId) {
        System.out.println("DEBUG: Selecting vehicle ID: " + vehicleId);
        for (int i = 0; i < vehicleTableModel.getRowCount(); i++) {
            if ((Integer) vehicleTableModel.getValueAt(i, 0) == vehicleId) {
                vehicleTable.setRowSelectionInterval(i, i);
                selectedVehicle = vehicleDAO.getVehicleById(vehicleId);
                System.out.println("DEBUG: Vehicle selected from table: " + 
                    (selectedVehicle != null ? selectedVehicle.getMake() + " " + selectedVehicle.getModel() : "NULL"));
                break;
            }
        }
    }

    private void setupLayout() {
        setSize(750, 700);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout());

        JScrollPane vehicleScroll = new JScrollPane(vehicleTable);
        vehicleScroll.setBorder(BorderFactory.createTitledBorder("Select Vehicle"));
        vehicleScroll.setPreferredSize(new Dimension(700, 150));
        mainPanel.add(vehicleScroll, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formPanel, gbc, "User  *:", userCombo, 0);
        addFormField(formPanel, gbc, "Start Date*:", startDatePicker, 1);
        addFormField(formPanel, gbc, "End Date*:", endDatePicker, 2);
        addFormField(formPanel, gbc, "Pickup Location*:", pickupLocationField, 3);
        addFormField(formPanel, gbc, "Dropoff Location*:", dropoffLocationField, 4);
        
        // Add calculate button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton calculateBtn = new JButton("Calculate Total Amount");
        calculateBtn.setBackground(new Color(30, 144, 255));
        calculateBtn.setForeground(Color.WHITE);
        calculateBtn.setFocusPainted(false);
        calculateBtn.addActionListener(e -> calculateTotalAmount());
        formPanel.add(calculateBtn, gbc);
        gbc.gridwidth = 1;
        
        addFormField(formPanel, gbc, "Total Amount*:", totalAmountField, 6);
        addFormField(formPanel, gbc, "Status*:", statusCombo, 7);
        addFormField(formPanel, gbc, "Payment Status*:", paymentStatusCombo, 8);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setBackground(new Color(34,139,34)); 
        saveBtn.setForeground(Color.WHITE); 
        saveBtn.setFocusPainted(false);
        cancelBtn.setBackground(Color.GRAY); 
        cancelBtn.setForeground(Color.WHITE); 
        cancelBtn.setFocusPainted(false);

        btnPanel.add(saveBtn); 
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveBooking());
        cancelBtn.addActionListener(e -> dispose());

        setContentPane(mainPanel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent comp, int row){
        gbc.gridx=0; gbc.gridy=row; gbc.gridwidth=1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx=1; 
        panel.add(comp, gbc);
    }

    private void calculateTotalAmount() {
        try {
            if (selectedVehicle == null) {
                mainApp.showErrorMessage("Please select a vehicle first!");
                return;
            }

            LocalDate startDate = getDateFromPicker(startDatePicker);
            LocalDate endDate = getDateFromPicker(endDatePicker);

            if (startDate == null || endDate == null) {
                mainApp.showErrorMessage("Please enter valid start and end dates in YYYY-MM-DD format!");
                return;
            }

            if (endDate.isBefore(startDate)) {
                mainApp.showErrorMessage("End date must be after start date!");
                return;
            }

            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            if (days == 0) days = 1; // Minimum 1 day

            BigDecimal dailyRate = selectedVehicle.getDailyRate();
            BigDecimal totalAmount = dailyRate.multiply(BigDecimal.valueOf(days));
            totalAmountField.setText(totalAmount.toString());

            System.out.println("DEBUG: Calculated amount - Days: " + days + 
                ", Daily Rate: " + dailyRate + ", Total: " + totalAmount);

        } catch (Exception e) {
            System.err.println("ERROR calculating amount: " + e.getMessage());
            e.printStackTrace();
            mainApp.showErrorMessage("Error calculating amount: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        // Add debug listener for user selection
        userCombo.addActionListener(e -> {
            User selected = (User ) userCombo.getSelectedItem();
            System.out.println("DEBUG: User combo selection changed to: " + 
                (selected != null ? selected.getUsername() + " (ID: " + selected.getUserId() + ")" : "NULL"));
        });

        // Vehicle table selection with debug output
        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = vehicleTable.getSelectedRow();
                System.out.println("DEBUG: Vehicle table row selected: " + row);
                
                if (row >= 0) {
                    int vehicleId = (Integer) vehicleTableModel.getValueAt(row, 0);
                    System.out.println("DEBUG: Getting vehicle with ID: " + vehicleId);
                    
                    selectedVehicle = vehicleDAO.getVehicleById(vehicleId);
                    
                    if (selectedVehicle != null) {
                        System.out.println("DEBUG: Vehicle loaded successfully - " + 
                            selectedVehicle.getMake() + " " + selectedVehicle.getModel() + 
                            " (ID: " + selectedVehicle.getVehicleId() + ")");
                    } else {
                        System.err.println("ERROR: vehicleDAO.getVehicleById(" + vehicleId + ") returned NULL!");
                    }
                } else {
                    selectedVehicle = null;
                    System.out.println("DEBUG: No vehicle row selected");
                }
            }
        });

        // Auto-calculate when dates change (fires on Enter key press)
        if (startDatePicker != null) {
            startDatePicker.addActionListener(e -> {
                if (selectedVehicle != null && totalAmountField.getText().trim().isEmpty()) {
                    calculateTotalAmount();
                }
            });
        }

        if (endDatePicker != null) {
            endDatePicker.addActionListener(e -> {
                if (selectedVehicle != null && totalAmountField.getText().trim().isEmpty()) {
                    calculateTotalAmount();
                }
            });
        }
    }

    private void loadBookingData() {
        if (booking == null) return;

        for (int i = 0; i < userCombo.getItemCount(); i++) {
            User u = userCombo.getItemAt(i);
            if (u.getUserId() == booking.getUserId()) { 
                userCombo.setSelectedIndex(i); 
                break; 
            }
        }

        setDatePickerDate(startDatePicker, booking.getStartDate());
        setDatePickerDate(endDatePicker, booking.getEndDate());
        pickupLocationField.setText(booking.getPickupLocation());
        dropoffLocationField.setText(booking.getDropoffLocation());
        totalAmountField.setText(booking.getTotalAmount().toString());
        statusCombo.setSelectedItem(booking.getStatus());
        paymentStatusCombo.setSelectedItem(booking.getPaymentStatus());
    }

    private void saveBooking() {
        System.out.println("\n========== SAVE BOOKING DEBUG ==========");
        System.out.println("Selected User: " + userCombo.getSelectedItem());
        System.out.println("Selected Vehicle: " + selectedVehicle);
        
        LocalDate start = getDateFromPicker(startDatePicker);
        LocalDate end = getDateFromPicker(endDatePicker);
        
        System.out.println("Start Date: " + start);
        System.out.println("End Date: " + end);
        System.out.println("Pickup Location: '" + pickupLocationField.getText() + "'");
        System.out.println("Dropoff Location: '" + dropoffLocationField.getText() + "'");
        System.out.println("Total Amount: '" + totalAmountField.getText() + "'");
        System.out.println("========================================\n");

        try {
            // Detailed validation with specific error messages
            if (userCombo.getSelectedItem() == null) {
                System.err.println("ERROR: No user selected!");
                mainApp.showErrorMessage("Please select a user!");
                return;
            }

            if (selectedVehicle == null) {
                System.err.println("ERROR: No vehicle selected!");
                mainApp.showErrorMessage("Please select a vehicle from the table!");
                return;
            }

            if (start == null) {
                System.err.println("ERROR: Start date is null!");
                mainApp.showErrorMessage("Please enter a valid start date in YYYY-MM-DD format!");
                return;
            }

            if (end == null) {
                System.err.println("ERROR: End date is null!");
                mainApp.showErrorMessage("Please enter a valid end date in YYYY-MM-DD format!");
                return;
            }

            if (pickupLocationField.getText().trim().isEmpty()) {
                System.err.println("ERROR: Pickup location is empty!");
                mainApp.showErrorMessage("Please enter a pickup location!");
                return;
            }

            if (dropoffLocationField.getText().trim().isEmpty()) {
                System.err.println("ERROR: Dropoff location is empty!");
                mainApp.showErrorMessage("Please enter a dropoff location!");
                return;
            }

            if (totalAmountField.getText().trim().isEmpty()) {
                System.err.println("ERROR: Total amount is empty!");
                mainApp.showErrorMessage("Please calculate or enter a total amount!");
                return;
            }

            // All validations passed, proceed with saving
            System.out.println("All validations passed, proceeding to save...");

            User user = (User ) userCombo.getSelectedItem();
            String pickup = pickupLocationField.getText().trim();
            String dropoff = dropoffLocationField.getText().trim();
            BigDecimal amount = new BigDecimal(totalAmountField.getText().trim());
            Booking.BookingStatus status = (Booking.BookingStatus) statusCombo.getSelectedItem();
            Booking.PaymentStatus payment = (Booking.PaymentStatus) paymentStatusCombo.getSelectedItem();

            if (booking == null) { // New booking
                System.out.println("Creating new booking...");
                Booking newBooking = new Booking(user.getUserId(), selectedVehicle.getVehicleId(),
                        start, end, pickup, dropoff, amount, status, payment);
                
                System.out.println("Calling bookingDAO.addBooking()...");
                if (bookingDAO.addBooking(newBooking)) {
                    System.out.println("SUCCESS: Booking added!");
                    mainApp.showSuccessMessage("Booking added successfully!");
                    bookingAdded = true;
                    dispose();
                } else {
                    System.err.println("ERROR: bookingDAO.addBooking() returned false");
                    mainApp.showErrorMessage("Failed to add booking");
                }
            } else { // Edit existing booking
                System.out.println("Updating existing booking (ID: " + booking.getBookingId() + ")...");
                booking.setUserId(user.getUserId());
                booking.setVehicleId(selectedVehicle.getVehicleId());
                booking.setStartDate(start);
                booking.setEndDate(end);
                booking.setPickupLocation(pickup);
                booking.setDropoffLocation(dropoff);
                booking.setTotalAmount(amount);
                booking.setStatus(status);
                booking.setPaymentStatus(payment);
                
                System.out.println("Calling bookingDAO.updateBooking()...");
                if (bookingDAO.updateBooking(booking)) {
                    System.out.println("SUCCESS: Booking updated!");
                    mainApp.showSuccessMessage("Booking updated successfully!");
                    bookingAdded = true;
                    dispose();
                } else {
                    System.err.println("ERROR: bookingDAO.updateBooking() returned false");
                    mainApp.showErrorMessage("Failed to update booking");
                }
            }

        } catch (Exception ex) {
            System.err.println("EXCEPTION during saveBooking: " + ex.getMessage());
            ex.printStackTrace();
            mainApp.showErrorMessage("Error saving booking: " + ex.getMessage());
        }
    }

    public boolean isBookingAdded() { 
        return bookingAdded; 
    }
}
