public class Car extends Vehicle {

    private int numberOfSeats;

    public Car(String vehicleId, String brand, String model, double ratePerDay, int numberOfSeats) {
        super(vehicleId, brand, model, ratePerDay);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    @Override
    public String getTypeName() {
        return "Car";
    }

    @Override
    public String getExtraInfo() {
        return numberOfSeats + " seats";
    }

    @Override
    public int getExtraValue() {
        return numberOfSeats;
    }

    @Override
    public void setExtraValue(int value) {
        numberOfSeats = value;
    }

    @Override
    public void displayInfo() {
        System.out.println("[CAR] " + getVehicleId() + " | " + getBrand() + " " + getModel()
                + " | Seats: " + numberOfSeats
                + " | Rate/Day: Rs." + getRatePerDay()
                + " | Available: " + (isAvailable() ? "Yes" : "No"));
    }
}
