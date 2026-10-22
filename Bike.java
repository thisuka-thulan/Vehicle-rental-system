public class Bike extends Vehicle {

    private int engineCC;

    public Bike(String vehicleId, String brand, String model, double ratePerDay, int engineCC) {
        super(vehicleId, brand, model, ratePerDay);
        this.engineCC = engineCC;
    }

    public int getEngineCC() {
        return engineCC;
    }

    @Override
    public String getTypeName() {
        return "Bike";
    }

    @Override
    public String getExtraInfo() {
        return engineCC + "cc";
    }

    @Override
    public int getExtraValue() {
        return engineCC;
    }

    @Override
    public void setExtraValue(int value) {
        engineCC = value;
    }

    @Override
    public void displayInfo() {
        System.out.println("[BIKE] " + getVehicleId() + " | " + getBrand() + " " + getModel()
                + " | Engine: " + engineCC + "cc"
                + " | Rate/Day: Rs." + getRatePerDay()
                + " | Available: " + (isAvailable() ? "Yes" : "No"));
    }

    // bikes get a small discount if someone books a week or more
    @Override
    public double calculateRentalCost(int days) {
        double cost = getRatePerDay() * days;
        if (days >= 7) {
            cost -= cost * 0.10;
        }
        return cost;
    }
}
