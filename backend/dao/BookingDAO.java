package backend.dao;

import backend.db.DBConnection;
import backend.models.Booking;
import backend.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private VehicleDAO vehicleDAO;

    public BookingDAO(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    // Add a new booking
    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, vehicle_id, start_date, end_date, pickup_location, dropoff_location, total_amount, status, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getVehicleId());
            stmt.setDate(3, Date.valueOf(booking.getStartDate()));
            stmt.setDate(4, Date.valueOf(booking.getEndDate()));
            stmt.setString(5, booking.getPickupLocation());
            stmt.setString(6, booking.getDropoffLocation());
            stmt.setBigDecimal(7, booking.getTotalAmount());
            stmt.setString(8, booking.getStatus().name().toLowerCase());
            stmt.setString(9, booking.getPaymentStatus().name().toLowerCase());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setBookingId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing booking
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET user_id=?, vehicle_id=?, start_date=?, end_date=?, pickup_location=?, dropoff_location=?, total_amount=?, status=?, payment_status=? " +
                "WHERE booking_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getVehicleId());
            stmt.setDate(3, Date.valueOf(booking.getStartDate()));
            stmt.setDate(4, Date.valueOf(booking.getEndDate()));
            stmt.setString(5, booking.getPickupLocation());
            stmt.setString(6, booking.getDropoffLocation());
            stmt.setBigDecimal(7, booking.getTotalAmount());
            stmt.setString(8, booking.getStatus().name().toLowerCase());
            stmt.setString(9, booking.getPaymentStatus().name().toLowerCase());
            stmt.setInt(10, booking.getBookingId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a booking
    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get booking by ID
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBooking(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Confirm booking
    public boolean confirmBooking(int bookingId) {
        String sql = "UPDATE bookings SET status='confirmed' WHERE booking_id=? AND status='pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cancel booking
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status='cancelled' WHERE booking_id=? AND (status='pending' OR status='confirmed')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Map a ResultSet row to Booking object (handle enums correctly)
    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("user_id"),
                rs.getInt("vehicle_id"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("pickup_location"),
                rs.getString("dropoff_location"),
                rs.getBigDecimal("total_amount"),
                Booking.BookingStatus.valueOf(rs.getString("status").toUpperCase()),
                Booking.PaymentStatus.valueOf(rs.getString("payment_status").toUpperCase()),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
