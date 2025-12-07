package backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pricing model class representing pricing rules for different vehicle types
 */
public class Pricing {
    private int pricingId;
    private Vehicle.VehicleType vehicleType;
    private BigDecimal baseDailyRate;
    private BigDecimal weekendMultiplier;
    private BigDecimal holidayMultiplier;
    private BigDecimal longTermDiscount;
    private BigDecimal insuranceDailyRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Pricing() {}
    
    // Constructor for new pricing
    public Pricing(Vehicle.VehicleType vehicleType, BigDecimal baseDailyRate,
                   BigDecimal weekendMultiplier, BigDecimal holidayMultiplier,
                   BigDecimal longTermDiscount, BigDecimal insuranceDailyRate) {
        this.vehicleType = vehicleType;
        this.baseDailyRate = baseDailyRate;
        this.weekendMultiplier = weekendMultiplier;
        this.holidayMultiplier = holidayMultiplier;
        this.longTermDiscount = longTermDiscount;
        this.insuranceDailyRate = insuranceDailyRate;
    }
    
    // Full constructor
    public Pricing(int pricingId, Vehicle.VehicleType vehicleType, BigDecimal baseDailyRate,
                   BigDecimal weekendMultiplier, BigDecimal holidayMultiplier,
                   BigDecimal longTermDiscount, BigDecimal insuranceDailyRate,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.pricingId = pricingId;
        this.vehicleType = vehicleType;
        this.baseDailyRate = baseDailyRate;
        this.weekendMultiplier = weekendMultiplier;
        this.holidayMultiplier = holidayMultiplier;
        this.longTermDiscount = longTermDiscount;
        this.insuranceDailyRate = insuranceDailyRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getPricingId() {
        return pricingId;
    }
    
    public void setPricingId(int pricingId) {
        this.pricingId = pricingId;
    }
    
    public Vehicle.VehicleType getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(Vehicle.VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public BigDecimal getBaseDailyRate() {
        return baseDailyRate;
    }
    
    public void setBaseDailyRate(BigDecimal baseDailyRate) {
        this.baseDailyRate = baseDailyRate;
    }
    
    public BigDecimal getWeekendMultiplier() {
        return weekendMultiplier;
    }
    
    public void setWeekendMultiplier(BigDecimal weekendMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
    }
    
    public BigDecimal getHolidayMultiplier() {
        return holidayMultiplier;
    }
    
    public void setHolidayMultiplier(BigDecimal holidayMultiplier) {
        this.holidayMultiplier = holidayMultiplier;
    }
    
    public BigDecimal getLongTermDiscount() {
        return longTermDiscount;
    }
    
    public void setLongTermDiscount(BigDecimal longTermDiscount) {
        this.longTermDiscount = longTermDiscount;
    }
    
    public BigDecimal getInsuranceDailyRate() {
        return insuranceDailyRate;
    }
    
    public void setInsuranceDailyRate(BigDecimal insuranceDailyRate) {
        this.insuranceDailyRate = insuranceDailyRate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public BigDecimal calculateWeekendRate() {
        return baseDailyRate.multiply(weekendMultiplier);
    }
    
    public BigDecimal calculateHolidayRate() {
        return baseDailyRate.multiply(holidayMultiplier);
    }
    
    public BigDecimal calculateLongTermRate() {
        return baseDailyRate.multiply(longTermDiscount);
    }
    
    public BigDecimal calculateTotalInsuranceRate(int days) {
        return insuranceDailyRate.multiply(BigDecimal.valueOf(days));
    }
    
    public BigDecimal calculateTotalRate(int days, boolean isWeekend, boolean isHoliday, boolean isLongTerm) {
        BigDecimal dailyRate = baseDailyRate;
        
        if (isHoliday) {
            dailyRate = calculateHolidayRate();
        } else if (isWeekend) {
            dailyRate = calculateWeekendRate();
        }
        
        if (isLongTerm && days >= 7) {
            dailyRate = dailyRate.multiply(longTermDiscount);
        }
        
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }
    
    public boolean isLongTermRental(int days) {
        return days >= 7;
    }
    
    public BigDecimal getDiscountAmount(int days) {
        if (isLongTermRental(days)) {
            BigDecimal originalAmount = baseDailyRate.multiply(BigDecimal.valueOf(days));
            BigDecimal discountedAmount = calculateLongTermRate().multiply(BigDecimal.valueOf(days));
            return originalAmount.subtract(discountedAmount);
        }
        return BigDecimal.ZERO;
    }
    
    public String getVehicleTypeDisplayName() {
        if (vehicleType == null) {
            return "Unknown";
        }
        
        switch (vehicleType) {
            case SEDAN:
                return "Sedan";
            case SUV:
                return "SUV";
            case HATCHBACK:
                return "Hatchback";
            case CONVERTIBLE:
                return "Convertible";
            case TRUCK:
                return "Truck";
            case VAN:
                return "Van";
            default:
                return vehicleType.getValue();
        }
    }
    
    @Override
    public String toString() {
        return "Pricing{" +
                "pricingId=" + pricingId +
                ", vehicleType=" + vehicleType +
                ", baseDailyRate=" + baseDailyRate +
                ", weekendMultiplier=" + weekendMultiplier +
                ", holidayMultiplier=" + holidayMultiplier +
                ", longTermDiscount=" + longTermDiscount +
                ", insuranceDailyRate=" + insuranceDailyRate +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pricing pricing = (Pricing) obj;
        return pricingId == pricing.pricingId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(pricingId);
    }
}
