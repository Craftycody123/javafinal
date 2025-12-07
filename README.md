# Vehicle Rental System

A comprehensive Java-based vehicle rental management system with a modern GUI interface.

## Features

### User Features
- User registration and authentication
- Browse available vehicles
- Make vehicle bookings
- View and manage personal bookings
- Update profile information
- Change password

### Admin Features
- Admin authentication
- Manage vehicles (add, edit, delete, view)
- Manage bookings (view, confirm, cancel)
- Manage users
- Configure pricing rules
- View reports and statistics

### System Features
- Secure password hashing
- Input validation
- Database connectivity
- Modern GUI interface
- Comprehensive error handling

## Prerequisites

- Java 8 or higher
- MySQL 5.7 or higher
- MySQL Connector/J (JDBC driver)

## Setup Instructions

### 1. Database Setup

1. Install MySQL on your system
2. Create a new database:
   ```sql
   CREATE DATABASE vehiclerentaldb;
   ```
3. Run the SQL script to create tables and insert sample data:
   ```bash
   mysql -u root -p vehiclerentaldb < sql/vehiclerentaldb.sql
   ```

### 2. Database Configuration

Update the database connection settings in `backend/db/DBConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/vehiclerentaldb";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_mysql_password";
```

### 3. Dependencies

Download and add the MySQL Connector/J JAR file to your classpath:
- Download from: https://dev.mysql.com/downloads/connector/j/
- Add the JAR file to your project's classpath

### 4. Compilation and Execution

#### Option 1: Using Command Line

1. Compile all Java files:
   ```bash
   javac -cp "path/to/mysql-connector-java.jar" backend/db/*.java backend/models/*.java backend/dao/*.java backend/util/*.java frontend/*.java
   ```

2. Run the application:
   ```bash
   java -cp ".:path/to/mysql-connector-java.jar" frontend.Main
   ```

#### Option 2: Using IDE

1. Create a new Java project in your IDE
2. Add the MySQL Connector/J JAR to your project's build path
3. Copy all source files to the appropriate directories
4. Run the `Main.java` file

## Default Login Credentials

### Admin Account
- Username: `admin`
- Password: `admin123`
- Email: `admin@vehiclerental.com`

### Sample User Account
You can register a new user account through the registration form.

## Project Structure

```
VehicleRentalApp/
├── frontend/                 # GUI components
│   ├── Main.java            # Main application entry point
│   ├── LoginFrame.java      # User/Admin login
│   ├── RegisterFrame.java   # User registration
│   ├── AdminDashboard.java  # Admin management interface
│   ├── UserDashboard.java   # User interface
│   ├── BookingPanel.java    # Booking management
│   └── VehiclePanel.java    # Vehicle management
├── backend/
│   ├── db/
│   │   └── DBConnection.java # Database connection
│   ├── models/              # Data models
│   │   ├── User.java
│   │   ├── Admin.java
│   │   ├── Vehicle.java
│   │   ├── Booking.java
│   │   ├── Pricing.java
│   │   └── RentalHistory.java
│   ├── dao/                 # Data Access Objects
│   │   ├── UserDAO.java
│   │   ├── AdminDAO.java
│   │   ├── VehicleDAO.java
│   │   ├── BookingDAO.java
│   │   ├── PricingDAO.java
│   │   └── RentalHistoryDAO.java
│   └── util/                # Utility classes
│       ├── PasswordUtils.java
│       └── ValidationUtils.java
└── sql/
    └── vehiclerentaldb.sql  # Database schema
```

## Database Schema

The system uses the following main tables:
- `users` - Customer information
- `admins` - Administrator accounts
- `vehicles` - Vehicle inventory
- `bookings` - Rental bookings
- `pricing` - Pricing rules
- `rental_history` - Completed rentals

## Usage

1. **Start the Application**: Run the `Main.java` file
2. **Login**: Use admin credentials or register a new user
3. **Admin Functions**: 
   - Manage vehicles and bookings
   - View reports and statistics
   - Configure pricing
4. **User Functions**:
   - Browse available vehicles
   - Make bookings
   - Manage personal bookings
   - Update profile

## Security Features

- Password hashing with salt
- Input validation and sanitization
- SQL injection prevention
- User authentication and authorization

## Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Verify MySQL is running
   - Check database credentials in `DBConnection.java`
   - Ensure the database exists

2. **Class Not Found Error**:
   - Add MySQL Connector/J to classpath
   - Verify all source files are compiled

3. **GUI Not Displaying**:
   - Ensure Java Swing is available
   - Check for any compilation errors

### Support

For issues or questions, please check:
1. Database connectivity
2. Java version compatibility
3. MySQL server status
4. Classpath configuration

## License

This project is for educational purposes. Feel free to modify and extend as needed.

## Future Enhancements

- Payment integration
- Email notifications
- Advanced reporting
- Mobile app interface
- API development
- Multi-language support
