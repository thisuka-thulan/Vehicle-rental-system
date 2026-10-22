import java.util.ArrayList;

public class RentalSystem {

    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Rental> rentals = new ArrayList<>();
    private Admin admin;

    public RentalSystem() {
        // only one admin account needed for this project
        admin = new Admin("AD001", "admin", "admin123", "System Admin");
    }

    // ---------- saving / loading ----------

    public void save() {
        DataStore.save(this);
    }

    public boolean load() {
        return DataStore.load(this);
    }

    // ---------- vehicles ----------

    // false if a vehicle with the same ID is already there
    public boolean addVehicle(Vehicle v) {
        if (findVehicleById(v.getVehicleId()) != null) {
            return false;
        }
        vehicles.add(v);
        save();
        return true;
    }

    public void updateVehicle(Vehicle v, String brand, String model, double rate, int extra) {
        v.setBrand(brand);
        v.setModel(model);
        v.setRatePerDay(rate);
        v.setExtraValue(extra);
        save();
    }

    // returns null when it worked, otherwise the reason it didn't
    public String removeVehicle(String id) {
        Vehicle v = findVehicleById(id);
        if (v == null) {
            return "Vehicle not found.";
        }
        if (!v.isAvailable()) {
            return "This vehicle is currently rented out.";
        }
        if (hasRentalHistory(id)) {
            return "This vehicle has rental history, so it can't be deleted.";
        }
        vehicles.remove(v);
        save();
        return null;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public Vehicle findVehicleById(String id) {
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equalsIgnoreCase(id)) {
                return v;
            }
        }
        return null;
    }

    // type = "All" / "Car" / "Bike", maxRate <= 0 means no price limit
    public ArrayList<Vehicle> searchVehicles(String keyword, String type, double maxRate, boolean onlyAvailable) {
        ArrayList<Vehicle> result = new ArrayList<>();
        String key = (keyword == null) ? "" : keyword.trim().toLowerCase();

        for (Vehicle v : vehicles) {
            if (onlyAvailable && !v.isAvailable()) continue;
            if (type != null && !type.equals("All") && !v.getTypeName().equalsIgnoreCase(type)) continue;
            if (maxRate > 0 && v.getRatePerDay() > maxRate) continue;
            if (!key.isEmpty()) {
                String text = (v.getVehicleId() + " " + v.getBrand() + " " + v.getModel()).toLowerCase();
                if (!text.contains(key)) continue;
            }
            result.add(v);
        }
        return result;
    }

    // ---------- customers ----------

    public void addCustomer(Customer c) {
        customers.add(c);
        save();
    }

    public Customer registerCustomer(String name, String phone, String username, String password) {
        if (findCustomerByUsername(username) != null) {
            return null; // already taken
        }
        Customer c = new Customer(generateNextCustomerId(), username, password, name, phone);
        customers.add(c);
        save();
        return c;
    }

    private String generateNextCustomerId() {
        return "CUS" + String.format("%03d", customers.size() + 1);
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    public Customer findCustomerByUsername(String username) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            if (c.getUsername().equalsIgnoreCase(username)) {
                return c;
            }
        }
        return null;
    }

    // ---------- login ----------

    public boolean authenticateAdmin(String username, String password) {
        return admin.getUsername().equalsIgnoreCase(username) && admin.checkPassword(password);
    }

    public Customer authenticateCustomer(String username, String password) {
        Customer c = findCustomerByUsername(username);
        if (c != null && c.checkPassword(password)) {
            return c;
        }
        return null;
    }

    // ---------- rentals ----------

    // gives back the new Rental, or null if the vehicle can't be rented
    public Rental rentVehicle(String vehicleId, Customer customer, int days) {
        Vehicle v = findVehicleById(vehicleId);
        if (v == null || !v.isAvailable() || days <= 0) {
            return null;
        }
        v.setAvailable(false);
        Rental rental = new Rental(nextRentalId(), v, customer, days);
        rentals.add(rental);
        save();
        return rental;
    }

    public boolean returnRental(Rental rental) {
        if (rental == null || rental.isReturned()) {
            return false;
        }
        rental.markReturned();
        rental.getVehicle().setAvailable(true);
        save();
        return true;
    }

    private String nextRentalId() {
        return "R" + String.format("%03d", rentals.size() + 1);
    }

    public ArrayList<Rental> getRentals() {
        return rentals;
    }

    public ArrayList<Rental> getRentalsByCustomer(Customer customer) {
        ArrayList<Rental> matches = new ArrayList<>();
        for (Rental r : rentals) {
            if (r.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                matches.add(r);
            }
        }
        return matches;
    }

    public boolean hasRentalHistory(String vehicleId) {
        for (Rental r : rentals) {
            if (r.getVehicle().getVehicleId().equalsIgnoreCase(vehicleId)) {
                return true;
            }
        }
        return false;
    }
}
