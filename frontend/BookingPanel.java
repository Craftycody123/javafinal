package frontend;

import backend.dao.*;
import backend.models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing bookings (viewing, creating, editing)
 */
public class BookingPanel extends JPanel {
    private Main mainApp;
    private BookingDAO bookingDAO;
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;

    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, confirmButton, cancelButton;
    private JComboBox<Booking.BookingStatus> statusFilterCombo;
    private JTextField searchField;

    public BookingPanel(VehicleDAO vehicleDAO, UserDAO userDAO, Main mainApp) {
        this.vehicleDAO = vehicleDAO;
        this.userDAO = userDAO;
        this.mainApp = mainApp;
        this.bookingDAO = new BookingDAO(vehicleDAO);

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadBookings();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "User", "Vehicle", "Start Date", "End Date",
                "Pickup Location", "Dropoff Location", "Total Amount", "Status", "Payment"};
        bookingTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingTable.setRowHeight(25);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addButton = createStyledButton("Add Booking", new Color(34, 139, 34));
        editButton = createStyledButton("Edit Booking", new Color(70, 130, 180));
        deleteButton = createStyledButton("Delete Booking", new Color(220, 20, 60));
        refreshButton = createStyledButton("Refresh", new Color(105, 105, 105));
        confirmButton = createStyledButton("Confirm", new Color(34, 139, 34));
        cancelButton = createStyledButton("Cancel", new Color(255, 140, 0));

        statusFilterCombo = new JComboBox<>(Booking.BookingStatus.values());
        statusFilterCombo.insertItemAt(null, 0);
        statusFilterCombo.setSelectedIndex(0);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 30));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(240, 248, 255));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilterCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setPreferredSize(new Dimension(1200, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bookings"));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddBookingDialog());

        editButton.addActionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (row >= 0) {
                int bookingId = (Integer) bookingTableModel.getValueAt(row, 0);
                Booking booking = bookingDAO.getBookingById(bookingId);
                if (booking != null) showEditBookingDialog(booking);
            } else mainApp.showErrorMessage("Select a booking to edit");
        });

        deleteButton.addActionListener(e -> {
            int row = bookingTable.getSelectedRow();
            if (row >= 0) {
                int bookingId = (Integer) bookingTableModel.getValueAt(row, 0);
                if (mainApp.showConfirmDialog("Delete this booking?")) {
                    if (bookingDAO.deleteBooking(bookingId)) {
                        mainApp.showSuccessMessage("Booking deleted!");
                        loadBookings();
                    } else mainApp.showErrorMessage("Failed to delete booking");
                }
            } else mainApp.showErrorMessage("Select a booking to delete");
        });

        refreshButton.addActionListener(e -> loadBookings());

        confirmButton.addActionListener(e -> changeBookingStatus(true));

        cancelButton.addActionListener(e -> changeBookingStatus(false));

        statusFilterCombo.addActionListener(e -> loadBookings());
        searchField.addActionListener(e -> loadBookings());
    }

    private void changeBookingStatus(boolean confirm) {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            mainApp.showErrorMessage("Select a booking first");
            return;
        }

        int bookingId = (Integer) bookingTableModel.getValueAt(row, 0);
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) return;

        if (confirm && booking.getStatus() == Booking.BookingStatus.PENDING) {
            if (mainApp.showConfirmDialog("Confirm this booking?")) {
                if (bookingDAO.confirmBooking(bookingId)) {
                    mainApp.showSuccessMessage("Booking confirmed!");
                    loadBookings();
                } else mainApp.showErrorMessage("Failed to confirm booking");
            }
        } else if (!confirm && (booking.getStatus() == Booking.BookingStatus.PENDING ||
                                booking.getStatus() == Booking.BookingStatus.CONFIRMED)) {
            if (mainApp.showConfirmDialog("Cancel this booking?")) {
                if (bookingDAO.cancelBooking(bookingId)) {
                    mainApp.showSuccessMessage("Booking cancelled!");
                    loadBookings();
                } else mainApp.showErrorMessage("Failed to cancel booking");
            }
        } else {
            mainApp.showErrorMessage("Action not allowed for this booking status");
        }
    }

    private void loadBookings() {
        bookingTableModel.setRowCount(0);
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            String searchTerm = searchField.getText().trim().toLowerCase();
            Booking.BookingStatus statusFilter = (Booking.BookingStatus) statusFilterCombo.getSelectedItem();

            for (Booking b : bookings) {
                User u = userDAO.getUserById(b.getUserId());
                Vehicle v = vehicleDAO.getVehicleById(b.getVehicleId());

                String userName = u != null ? u.getFullName() : "Unknown";
                String vehicleName = v != null ? v.getFullName() : "Unknown";

                if (!searchTerm.isEmpty()) {
                    if (!userName.toLowerCase().contains(searchTerm)
                            && !vehicleName.toLowerCase().contains(searchTerm)
                            && !b.getPickupLocation().toLowerCase().contains(searchTerm)
                            && !b.getDropoffLocation().toLowerCase().contains(searchTerm)) continue;
                }

                if (statusFilter != null && b.getStatus() != statusFilter) continue;

                Object[] row = {
                        b.getBookingId(),
                        userName,
                        vehicleName,
                        b.getStartDate(),
                        b.getEndDate(),
                        b.getPickupLocation(),
                        b.getDropoffLocation(),
                        "$" + b.getTotalAmount(),
                        b.getStatus().getValue(),
                        b.getPaymentStatus().getValue()
                };
                bookingTableModel.addRow(row);
            }
        } catch (Exception e) {
            mainApp.showErrorMessage("Error loading bookings: " + e.getMessage());
        }
    }

    private void showAddBookingDialog() {
        BookingDialog dialog = new BookingDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add Booking", null, bookingDAO, vehicleDAO, userDAO, mainApp);
        dialog.setVisible(true);
        if (dialog.isBookingAdded()) loadBookings();
    }

    private void showEditBookingDialog(Booking booking) {
        BookingDialog dialog = new BookingDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Booking", booking, bookingDAO, vehicleDAO, userDAO, mainApp);
        dialog.setVisible(true);
        if (dialog.isBookingAdded()) loadBookings();
    }

    public void refresh() {
        loadBookings();
    }
}
