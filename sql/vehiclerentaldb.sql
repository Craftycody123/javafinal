-- Vehicle Rental Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS vehiclerentaldb;
USE vehiclerentaldb;

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    license_number VARCHAR(50),
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Admins table
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(50) DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(30),
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type ENUM('sedan', 'suv', 'hatchback', 'convertible', 'truck', 'van') NOT NULL,
    fuel_type ENUM('gasoline', 'diesel', 'electric', 'hybrid') NOT NULL,
    transmission ENUM('manual', 'automatic') NOT NULL,
    seating_capacity INT NOT NULL,
    mileage INT DEFAULT 0,
    status ENUM('available', 'rented', 'maintenance', 'out_of_service') DEFAULT 'available',
    daily_rate DECIMAL(10,2) NOT NULL,
    image_path VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    pickup_location VARCHAR(100) NOT NULL,
    dropoff_location VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'confirmed', 'active', 'completed', 'cancelled') DEFAULT 'pending',
    payment_status ENUM('pending', 'paid', 'refunded') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE
);

-- Pricing table
CREATE TABLE pricing (
    pricing_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_type ENUM('sedan', 'suv', 'hatchback', 'convertible', 'truck', 'van') NOT NULL,
    base_daily_rate DECIMAL(10,2) NOT NULL,
    weekend_multiplier DECIMAL(3,2) DEFAULT 1.2,
    holiday_multiplier DECIMAL(3,2) DEFAULT 1.5,
    long_term_discount DECIMAL(3,2) DEFAULT 0.9,
    insurance_daily_rate DECIMAL(10,2) DEFAULT 15.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Rental History table
CREATE TABLE rental_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    user_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    actual_return_date DATE,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('completed', 'cancelled', 'no_show') NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE
);

-- Insert default admin
INSERT INTO admins (username, email, password_hash, first_name, last_name, role) 
VALUES ('admin', 'admin@vehiclerental.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System', 'Administrator', 'super_admin');

-- Insert sample vehicles
INSERT INTO vehicles (make, model, year, color, license_plate, vehicle_type, fuel_type, transmission, seating_capacity, daily_rate, description) VALUES
('Toyota', 'Camry', 2022, 'Silver', 'ABC123', 'sedan', 'gasoline', 'automatic', 5, 45.00, 'Comfortable sedan perfect for city driving'),
('Honda', 'CR-V', 2023, 'White', 'DEF456', 'suv', 'gasoline', 'automatic', 5, 55.00, 'Spacious SUV ideal for family trips'),
('Ford', 'Focus', 2021, 'Blue', 'GHI789', 'hatchback', 'gasoline', 'manual', 5, 35.00, 'Economical hatchback with great fuel efficiency'),
('BMW', '3 Series', 2023, 'Black', 'JKL012', 'sedan', 'gasoline', 'automatic', 5, 75.00, 'Luxury sedan with premium features'),
('Chevrolet', 'Suburban', 2022, 'Red', 'MNO345', 'suv', 'gasoline', 'automatic', 8, 85.00, 'Large SUV perfect for group travel');

-- Insert pricing data
INSERT INTO pricing (vehicle_type, base_daily_rate, weekend_multiplier, holiday_multiplier, long_term_discount, insurance_daily_rate) VALUES
('sedan', 45.00, 1.2, 1.5, 0.9, 15.00),
('suv', 55.00, 1.2, 1.5, 0.9, 18.00),
('hatchback', 35.00, 1.2, 1.5, 0.9, 12.00),
('convertible', 65.00, 1.3, 1.6, 0.9, 20.00),
('truck', 70.00, 1.2, 1.5, 0.9, 22.00),
('van', 60.00, 1.2, 1.5, 0.9, 19.00);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_vehicles_type ON vehicles(vehicle_type);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_vehicle_id ON bookings(vehicle_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(start_date, end_date);
