package backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * RentalHistory model class representing completed rental transactions
 */
public class RentalHistory {
    private int historyId;
    private int bookingId;
    private int userId;
    private int vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualReturnDate;
    private BigDecimal totalAmount;
    private RentalStatus status;
    private String notes;
    private LocalDateTime createdAt;
    
    // Enum for rental status
    public enum RentalStatus {
        COMPLETED("completed"),
        CANCELLED("cancelled"),
        NO_SHOW("no_show");
        
        private final String value;
        
        RentalStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static RentalStatus fromString(String value) {
            for (RentalStatus status : RentalStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid rental status: " + value);
        }
    }
    
    // Default constructor
    public RentalHistory() {}
    
    // Constructor for new rental history entry
    public RentalHistory(int bookingId, int userId, int vehicleId, LocalDate startDate,
                        LocalDate endDate, BigDecimal totalAmount, RentalStatus status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }
    
    // Full constructor
    public RentalHistory(int historyId, int bookingId, int userId, int vehicleId,
                        LocalDate startDate, LocalDate endDate, LocalDate actualReturnDate,
                        BigDecimal totalAmount, RentalStatus status, String notes,
                        LocalDateTime createdAt) {
        this.historyId = historyId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actualReturnDate = actualReturnDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getHistoryId() {
        return historyId;
    }
    
    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }
    
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public RentalStatus getStatus() {
        return status;
    }
    
    public void setStatus(RentalStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Utility methods
    public long getPlannedDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return startDate.until(endDate).getDays();
    }
    
    public long getActualDurationInDays() {
        if (startDate == null || actualReturnDate == null) {
            return 0;
        }
        return startDate.until(actualReturnDate).getDays();
    }
    
    public boolean isCompleted() {
        return status == RentalStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == RentalStatus.CANCELLED;
    }
    
    public boolean isNoShow() {
        return status == RentalStatus.NO_SHOW;
    }
    
    public boolean wasReturnedLate() {
        if (actualReturnDate == null || endDate == null) {
            return false;
        }
        return actualReturnDate.isAfter(endDate);
    }
    
    public boolean wasReturnedEarly() {
        if (actualReturnDate == null || endDate == null) {
            return false;
        }
        return actualReturnDate.isBefore(endDate);
    }
    
    public boolean wasReturnedOnTime() {
        if (actualReturnDate == null || endDate == null) {
            return false;
        }
        return actualReturnDate.isEqual(endDate);
    }
    
    public long getDaysLate() {
        if (!wasReturnedLate()) {
            return 0;
        }
        return endDate.until(actualReturnDate).getDays();
    }
    
    public long getDaysEarly() {
        if (!wasReturnedEarly()) {
            return 0;
        }
        return actualReturnDate.until(endDate).getDays();
    }
    
    public boolean isRecent() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }
    
    public String getStatusDisplayName() {
        if (status == null) {
            return "Unknown";
        }
        
        switch (status) {
            case COMPLETED:
                return "Completed";
            case CANCELLED:
                return "Cancelled";
            case NO_SHOW:
                return "No Show";
            default:
                return status.getValue();
        }
    }
    
    public String getReturnStatus() {
        if (actualReturnDate == null) {
            return "Not Returned";
        }
        
        if (wasReturnedOnTime()) {
            return "On Time";
        } else if (wasReturnedLate()) {
            return "Late (" + getDaysLate() + " days)";
        } else {
            return "Early (" + getDaysEarly() + " days)";
        }
    }
    
    @Override
    public String toString() {
        return "RentalHistory{" +
                "historyId=" + historyId +
                ", bookingId=" + bookingId +
                ", userId=" + userId +
                ", vehicleId=" + vehicleId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", actualReturnDate=" + actualReturnDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RentalHistory that = (RentalHistory) obj;
        return historyId == that.historyId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(historyId);
    }
}
