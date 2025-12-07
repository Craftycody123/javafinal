package frontend;

import backend.dao.VehicleDAO;
import backend.models.Vehicle;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VehiclePanel extends JPanel {
    private Main mainApp;
    private VehicleDAO vehicleDAO;

    private JTable vehicleTable;
    private DefaultTableModel vehicleTableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<Vehicle.VehicleType> typeFilterCombo;
    private JComboBox<Vehicle.VehicleStatus> statusFilterCombo;
    private JTextField searchField;
    private boolean isAdminMode = false;

    public VehiclePanel(VehicleDAO vehicleDAO, Main mainApp) {
        this.vehicleDAO = vehicleDAO;
        this.mainApp = mainApp;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadVehicles();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "Make", "Model", "Year", "Color", "License Plate", "Type",
                                "Fuel", "Transmission", "Seats", "Mileage", "Status", "Daily Rate"};
        vehicleTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehicleTable = new JTable(vehicleTableModel);
        vehicleTable.setFont(new Font("Arial", Font.PLAIN, 12));
        vehicleTable.setRowHeight(25);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addButton = new JButton("Add Vehicle");
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setPreferredSize(new Dimension(100, 30));
        addButton.setBackground(new Color(34, 139, 34));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        editButton = new JButton("Edit Vehicle");
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.setPreferredSize(new Dimension(100, 30));
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);

        deleteButton = new JButton("Delete Vehicle");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setPreferredSize(new Dimension(120, 30));
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setPreferredSize(new Dimension(80, 30));
        refreshButton.setBackground(new Color(105, 105, 105));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        typeFilterCombo = new JComboBox<>(Vehicle.VehicleType.values());
        typeFilterCombo.insertItemAt(null, 0);
        typeFilterCombo.setSelectedIndex(0);

        statusFilterCombo = new JComboBox<>(Vehicle.VehicleStatus.values());
        statusFilterCombo.insertItemAt(null, 0);
        statusFilterCombo.setSelectedIndex(0);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
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
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilterCombo);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilterCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Vehicles"));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddVehicleDialog());

        editButton.addActionListener(e -> {
            int selectedRow = vehicleTable.getSelectedRow();
            if (selectedRow >= 0) {
                int vehicleId = (Integer) vehicleTableModel.getValueAt(selectedRow, 0);
                Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
                if (vehicle != null) {
                    showEditVehicleDialog(vehicle);
                }
            } else {
                mainApp.showErrorMessage("Please select a vehicle to edit");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = vehicleTable.getSelectedRow();
            if (selectedRow >= 0) {
                int vehicleId = (Integer) vehicleTableModel.getValueAt(selectedRow, 0);
                String vehicleName = vehicleTableModel.getValueAt(selectedRow, 1) + " "
                        + vehicleTableModel.getValueAt(selectedRow, 2);

                if (mainApp.showConfirmDialog("Are you sure you want to delete vehicle: " + vehicleName + "?")) {
                    if (vehicleDAO.deleteVehicle(vehicleId)) {
                        mainApp.showSuccessMessage("Vehicle deleted successfully!");
                        loadVehicles();
                    } else {
                        mainApp.showErrorMessage("Failed to delete vehicle");
                    }
                }
            } else {
                mainApp.showErrorMessage("Please select a vehicle to delete");
            }
        });

        refreshButton.addActionListener(e -> loadVehicles());
        typeFilterCombo.addActionListener(e -> loadVehicles());
        statusFilterCombo.addActionListener(e -> loadVehicles());
        searchField.addActionListener(e -> loadVehicles());
    }

    private void loadVehicles() {
        vehicleTableModel.setRowCount(0);

        try {
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();

            String searchTerm = searchField.getText().trim().toLowerCase();
            Vehicle.VehicleType typeFilter = (Vehicle.VehicleType) typeFilterCombo.getSelectedItem();
            Vehicle.VehicleStatus statusFilter = (Vehicle.VehicleStatus) statusFilterCombo.getSelectedItem();

            for (Vehicle vehicle : vehicles) {
                if (!searchTerm.isEmpty()) {
                    if (!vehicle.getMake().toLowerCase().contains(searchTerm) &&
                            !vehicle.getModel().toLowerCase().contains(searchTerm) &&
                            !vehicle.getLicensePlate().toLowerCase().contains(searchTerm)) {
                        continue;
                    }
                }

                if (typeFilter != null && vehicle.getVehicleType() != typeFilter) {
                    continue;
                }

                if (statusFilter != null && vehicle.getStatus() != statusFilter) {
                    continue;
                }

                Object[] row = {
                        vehicle.getVehicleId(),
                        vehicle.getMake(),
                        vehicle.getModel(),
                        vehicle.getYear(),
                        vehicle.getColor(),
                        vehicle.getLicensePlate(),
                        vehicle.getVehicleType().getValue(),
                        vehicle.getFuelType().getValue(),
                        vehicle.getTransmission().getValue(),
                        vehicle.getSeatingCapacity(),
                        vehicle.getMileage(),
                        vehicle.getStatus().getValue(),
                        "$" + vehicle.getDailyRate()
                };
                vehicleTableModel.addRow(row);
            }
        } catch (Exception e) {
            mainApp.showErrorMessage("Error loading vehicles: " + e.getMessage());
        }
    }

    public void setAdminMode(boolean isAdmin) {
        this.isAdminMode = isAdmin;
        addButton.setEnabled(isAdmin);
        addButton.setVisible(isAdmin);
        editButton.setEnabled(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setEnabled(isAdmin);
        deleteButton.setVisible(isAdmin);
    }

    public void refresh() {
        loadVehicles();
    }

    private void showAddVehicleDialog() {
        VehicleDialog dialog = new VehicleDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Add Vehicle", null, vehicleDAO, mainApp);
        dialog.setVisible(true);
        if (dialog.isVehicleAdded()) {
            loadVehicles();
        }
    }

    private void showEditVehicleDialog(Vehicle vehicle) {
        VehicleDialog dialog = new VehicleDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Vehicle", vehicle, vehicleDAO, mainApp);
        dialog.setVisible(true);
        if (dialog.isVehicleAdded()) {
            loadVehicles();
        }
    }

    private static class VehicleDialog extends JDialog {
        private Vehicle vehicle;
        private VehicleDAO vehicleDAO;
        private Main mainApp;
        private boolean vehicleAdded = false;

        private JTextField makeField;
        private JTextField modelField;
        private JTextField yearField;
        private JTextField colorField;
        private JTextField licensePlateField;
        private JComboBox<Vehicle.VehicleType> typeCombo;
        private JComboBox<Vehicle.FuelType> fuelCombo;
        private JComboBox<Vehicle.Transmission> transmissionCombo;
        private JTextField seatingCapacityField;
        private JTextField mileageField;
        private JComboBox<Vehicle.VehicleStatus> statusCombo;
        private JTextField dailyRateField;
        private JTextArea descriptionArea;

        public VehicleDialog(Frame parent, String title, Vehicle vehicle, VehicleDAO vehicleDAO, Main mainApp) {
            super(parent, title, true);
            this.vehicle = vehicle;
            this.vehicleDAO = vehicleDAO;
            this.mainApp = mainApp;

            initializeComponents();
            setupLayout();
            setupEventHandlers();

            if (vehicle != null) {
                loadVehicleData();
            }
        }

        private void initializeComponents() {
            makeField = new JTextField(20);
            modelField = new JTextField(20);
            yearField = new JTextField(20);
            colorField = new JTextField(20);
            licensePlateField = new JTextField(20);
            typeCombo = new JComboBox<>(Vehicle.VehicleType.values());
            fuelCombo = new JComboBox<>(Vehicle.FuelType.values());
            transmissionCombo = new JComboBox<>(Vehicle.Transmission.values());
            seatingCapacityField = new JTextField(20);
            mileageField = new JTextField(20);
            statusCombo = new JComboBox<>(Vehicle.VehicleStatus.values());
            dailyRateField = new JTextField(20);
            descriptionArea = new JTextArea(3, 20);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
        }

        private void setupLayout() {
            setTitle(getTitle());
            setSize(500, 600);
            setLocationRelativeTo(getParent());

            JPanel panel = new JPanel(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            addFormField(formPanel, gbc, "Make*:", makeField, 0);
            addFormField(formPanel, gbc, "Model*:", modelField, 1);
            addFormField(formPanel, gbc, "Year*:", yearField, 2);
            addFormField(formPanel, gbc, "Color:", colorField, 3);
            addFormField(formPanel, gbc, "License Plate*:", licensePlateField, 4);
            addFormField(formPanel, gbc, "Type*:", typeCombo, 5);
            addFormField(formPanel, gbc, "Fuel Type*:", fuelCombo, 6);
            addFormField(formPanel, gbc, "Transmission*:", transmissionCombo, 7);
            addFormField(formPanel, gbc, "Seating Capacity*:", seatingCapacityField, 8);
            addFormField(formPanel, gbc, "Mileage:", mileageField, 9);
            addFormField(formPanel, gbc, "Status*:", statusCombo, 10);
            addFormField(formPanel, gbc, "Daily Rate*:", dailyRateField, 11);

            gbc.gridx = 0;
            gbc.gridy = 12;
            formPanel.add(new JLabel("Description:"), gbc);
            gbc.gridx = 1;
            gbc.gridy = 12;
            formPanel.add(new JScrollPane(descriptionArea), gbc);

            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");

            saveButton.setPreferredSize(new Dimension(80, 30));
            saveButton.setBackground(new Color(34, 139, 34));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);

            cancelButton.setPreferredSize(new Dimension(80, 30));
            cancelButton.setBackground(new Color(105, 105, 105));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            saveButton.addActionListener(e -> saveVehicle());
            cancelButton.addActionListener(e -> dispose());

            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            setContentPane(panel);
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int row) {
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel(label), gbc);
            gbc.gridx = 1;
            panel.add(component, gbc);
        }

        private void setupEventHandlers() {
            // Additional event handlers can be added here if needed
        }

        private void loadVehicleData() {
            if (vehicle != null) {
                makeField.setText(vehicle.getMake());
                modelField.setText(vehicle.getModel());
                yearField.setText(String.valueOf(vehicle.getYear()));
                colorField.setText(vehicle.getColor());
                licensePlateField.setText(vehicle.getLicensePlate());
                typeCombo.setSelectedItem(vehicle.getVehicleType());
                fuelCombo.setSelectedItem(vehicle.getFuelType());
                transmissionCombo.setSelectedItem(vehicle.getTransmission());
                seatingCapacityField.setText(String.valueOf(vehicle.getSeatingCapacity()));
                mileageField.setText(String.valueOf(vehicle.getMileage()));
                statusCombo.setSelectedItem(vehicle.getStatus());
                dailyRateField.setText(vehicle.getDailyRate().toString());
                descriptionArea.setText(vehicle.getDescription());
            }
        }

        private void saveVehicle() {
            try {
                // Validate required fields
                if (makeField.getText().trim().isEmpty() ||
                        modelField.getText().trim().isEmpty() ||
                        yearField.getText().trim().isEmpty() ||
                        licensePlateField.getText().trim().isEmpty() ||
                        seatingCapacityField.getText().trim().isEmpty() ||
                        dailyRateField.getText().trim().isEmpty()) {
                    mainApp.showErrorMessage("Please fill in all required fields (*)");
                    return;
                }

                int year = Integer.parseInt(yearField.getText().trim());
                if (year < 1900 || year > 2030) {
                    mainApp.showErrorMessage("Invalid year");
                    return;
                }

                int seatingCapacity = Integer.parseInt(seatingCapacityField.getText().trim());
                if (seatingCapacity < 1 || seatingCapacity > 20) {
                    mainApp.showErrorMessage("Invalid seating capacity");
                    return;
                }

                double dailyRate = Double.parseDouble(dailyRateField.getText().trim());
                if (dailyRate <= 0) {
                    mainApp.showErrorMessage("Daily rate must be greater than 0");
                    return;
                }

                int mileage = 0;
                if (!mileageField.getText().trim().isEmpty()) {
                    mileage = Integer.parseInt(mileageField.getText().trim());
                    if (mileage < 0) {
                        mainApp.showErrorMessage("Mileage cannot be negative");
                        return;
                    }
                }

                if (vehicle == null || !vehicle.getLicensePlate().equals(licensePlateField.getText().trim())) {
                    if (vehicleDAO.licensePlateExists(licensePlateField.getText().trim())) {
                        mainApp.showErrorMessage("License plate already exists");
                        return;
                    }
                }

                if (vehicle == null) {
                    Vehicle newVehicle = new Vehicle(makeField.getText().trim(), modelField.getText().trim(),
                            year, colorField.getText().trim(), licensePlateField.getText().trim(),
                            (Vehicle.VehicleType) typeCombo.getSelectedItem(),
                            (Vehicle.FuelType) fuelCombo.getSelectedItem(),
                            (Vehicle.Transmission) transmissionCombo.getSelectedItem(),
                            seatingCapacity,
                            new BigDecimal(dailyRate),
                            descriptionArea.getText().trim());

                    newVehicle.setMileage(mileage);
                    newVehicle.setStatus((Vehicle.VehicleStatus) statusCombo.getSelectedItem());

                    if (vehicleDAO.createVehicle(newVehicle)) {
                        mainApp.showSuccessMessage("Vehicle added successfully!");
                        vehicleAdded = true;
                        dispose();
                    } else {
                        mainApp.showErrorMessage("Failed to add vehicle");
                    }
                } else {
                    vehicle.setMake(makeField.getText().trim());
                    vehicle.setModel(modelField.getText().trim());
                    vehicle.setYear(year);
                    vehicle.setColor(colorField.getText().trim());
                    vehicle.setLicensePlate(licensePlateField.getText().trim());
                    vehicle.setVehicleType((Vehicle.VehicleType) typeCombo.getSelectedItem());
                    vehicle.setFuelType((Vehicle.FuelType) fuelCombo.getSelectedItem());
                    vehicle.setTransmission((Vehicle.Transmission) transmissionCombo.getSelectedItem());
                    vehicle.setSeatingCapacity(seatingCapacity);
                    vehicle.setMileage(mileage);
                    vehicle.setStatus((Vehicle.VehicleStatus) statusCombo.getSelectedItem());
                    vehicle.setDailyRate(new BigDecimal(dailyRate));
                    vehicle.setDescription(descriptionArea.getText().trim());

                    if (vehicleDAO.updateVehicle(vehicle)) {
                        mainApp.showSuccessMessage("Vehicle updated successfully!");
                        vehicleAdded = true;
                        dispose();
                    } else {
                        mainApp.showErrorMessage("Failed to update vehicle");
                    }
                }
            } catch (NumberFormatException e) {
                mainApp.showErrorMessage("Invalid number format");
            } catch (Exception e) {
                mainApp.showErrorMessage("Error saving vehicle: " + e.getMessage());
            }
        }

        public boolean isVehicleAdded() {
            return vehicleAdded;
        }
    }
}
