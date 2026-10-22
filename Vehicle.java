/*
 * Every rentable vehicle in the system builds on this.
 * Fields stay private, outside code goes through getters/setters.
 */
public abstract class Vehicle {

    private String vehicleId;
    private String brand;
    private String model;
    private double ratePerDay;
    private boolean available;

    public Vehicle(String vehicleId, String brand, String model, double ratePerDay) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.ratePerDay = ratePerDay;
        available = true;
    }

    public abstract void displayInfo();

    // "Car" or "Bike" - used by the tables, filters and the data file
    public abstract String getTypeName();

    // short text for the table, e.g. "5 seats" or "125cc"
    public abstract String getExtraInfo();

    // the one number that is specific to the type (seats / engine cc)
    public abstract int getExtraValue();
    public abstract void setExtraValue(int value);

    // default cost calc - subclasses can override if they price differently (see Bike)
    public double calculateRentalCost(int days) {
        return ratePerDay * days;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(double ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
