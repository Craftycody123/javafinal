package backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vehicle model class representing a vehicle in the rental system
 */
public class Vehicle {
    private int vehicleId;
    private String make;
    private String model;
    private int year;
    private String color;
    private String licensePlate;
    private VehicleType vehicleType;
    private FuelType fuelType;
    private Transmission transmission;
    private int seatingCapacity;
    private int mileage;
    private VehicleStatus status;
    private BigDecimal dailyRate;
    private String imagePath;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Enums for vehicle properties
    public enum VehicleType {
        SEDAN("sedan"),
        SUV("suv"),
        HATCHBACK("hatchback"),
        CONVERTIBLE("convertible"),
        TRUCK("truck"),
        VAN("van");
        
        private final String value;
        
        VehicleType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static VehicleType fromString(String value) {
            for (VehicleType type : VehicleType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid vehicle type: " + value);
        }
    }
    
    public enum FuelType {
        GASOLINE("gasoline"),
        DIESEL("diesel"),
        ELECTRIC("electric"),
        HYBRID("hybrid");
        
        private final String value;
        
        FuelType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static FuelType fromString(String value) {
            for (FuelType type : FuelType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid fuel type: " + value);
        }
    }
    
    public enum Transmission {
        MANUAL("manual"),
        AUTOMATIC("automatic");
        
        private final String value;
        
        Transmission(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Transmission fromString(String value) {
            for (Transmission type : Transmission.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid transmission type: " + value);
        }
    }
    
    public enum VehicleStatus {
        AVAILABLE("available"),
        RENTED("rented"),
        MAINTENANCE("maintenance"),
        OUT_OF_SERVICE("out_of_service");
        
        private final String value;
        
        VehicleStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static VehicleStatus fromString(String value) {
            for (VehicleStatus status : VehicleStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid vehicle status: " + value);
        }
    }
    
    // Default constructor
    public Vehicle() {}
    
    // Constructor for new vehicle
    public Vehicle(String make, String model, int year, String color, String licensePlate,
                   VehicleType vehicleType, FuelType fuelType, Transmission transmission,
                   int seatingCapacity, BigDecimal dailyRate, String description) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seatingCapacity = seatingCapacity;
        this.dailyRate = dailyRate;
        this.description = description;
        this.status = VehicleStatus.AVAILABLE;
        this.mileage = 0;
    }
    
    // Full constructor
    public Vehicle(int vehicleId, String make, String model, int year, String color,
                   String licensePlate, VehicleType vehicleType, FuelType fuelType,
                   Transmission transmission, int seatingCapacity, int mileage,
                   VehicleStatus status, BigDecimal dailyRate, String imagePath,
                   String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seatingCapacity = seatingCapacity;
        this.mileage = mileage;
        this.status = status;
        this.dailyRate = dailyRate;
        this.imagePath = imagePath;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public FuelType getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }
    
    public Transmission getTransmission() {
        return transmission;
    }
    
    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }
    
    public int getSeatingCapacity() {
        return seatingCapacity;
    }
    
    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }
    
    public int getMileage() {
        return mileage;
    }
    
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
    
    public VehicleStatus getStatus() {
        return status;
    }
    
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
    
    public BigDecimal getDailyRate() {
        return dailyRate;
    }
    
    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    public String getFullName() {
        return year + " " + make + " " + model;
    }
    
    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }
    
    public boolean isRented() {
        return status == VehicleStatus.RENTED;
    }
    
    public boolean isInMaintenance() {
        return status == VehicleStatus.MAINTENANCE;
    }
    
    public boolean isOutOfService() {
        return status == VehicleStatus.OUT_OF_SERVICE;
    }
    
    public int getAge() {
        return LocalDateTime.now().getYear() - year;
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", vehicleType=" + vehicleType +
                ", fuelType=" + fuelType +
                ", transmission=" + transmission +
                ", seatingCapacity=" + seatingCapacity +
                ", mileage=" + mileage +
                ", status=" + status +
                ", dailyRate=" + dailyRate +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return vehicleId == vehicle.vehicleId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(vehicleId);
    }
}
