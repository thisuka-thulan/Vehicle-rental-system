import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {

    // every day late costs 1.5x the normal daily rate
    public static final double LATE_FEE_MULTIPLIER = 1.5;

    private String rentalId;
    private Vehicle vehicle;
    private Customer customer;
    private int days;
    private double totalCost;
    private LocalDate rentDate;
    private LocalDate returnDate; // stays null until the vehicle comes back
    private double lateFee;

    // brand new rental, starts today
    public Rental(String rentalId, Vehicle vehicle, Customer customer, int days) {
        this(rentalId, vehicle, customer, days, vehicle.calculateRentalCost(days),
                LocalDate.now(), null, 0);
    }

    // used when loading from the data file (or seeding demo data)
    public Rental(String rentalId, Vehicle vehicle, Customer customer, int days,
                  double totalCost, LocalDate rentDate, LocalDate returnDate, double lateFee) {
        this.rentalId = rentalId;
        this.vehicle = vehicle;
        this.customer = customer;
        this.days = days;
        this.totalCost = totalCost;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.lateFee = lateFee;
    }

    public String getRentalId() {
        return rentalId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public LocalDate getDueDate() {
        return rentDate.plusDays(days);
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    // how many days past the due date (counts up to today if still out)
    public int getLateDays() {
        LocalDate end = isReturned() ? returnDate : LocalDate.now();
        long late = ChronoUnit.DAYS.between(getDueDate(), end);
        return late > 0 ? (int) late : 0;
    }

    private double calcLateFee() {
        return getLateDays() * vehicle.getRatePerDay() * LATE_FEE_MULTIPLIER;
    }

    // fixed once returned, "so far" figure while still out
    public double getLateFee() {
        return isReturned() ? lateFee : calcLateFee();
    }

    public double getGrandTotal() {
        return totalCost + getLateFee();
    }

    public void markReturned() {
        returnDate = LocalDate.now();
        lateFee = calcLateFee();
    }

    public String getStatus() {
        if (isReturned()) return "Returned";
        if (getLateDays() > 0) return "Overdue";
        return "Active";
    }

    public String getReceiptText() {
        StringBuilder sb = new StringBuilder();
        String line = "==========================================\n";
        String thin = "------------------------------------------\n";
        sb.append(line);
        sb.append("      GLOBEDRIVE RENTALS - RECEIPT\n");
        sb.append(line);
        sb.append(String.format("Receipt No  : %s%n", rentalId));
        sb.append(String.format("Customer    : %s (%s)%n", customer.getName(), customer.getCustomerId()));
        sb.append(String.format("Vehicle     : %s %s (%s)%n", vehicle.getBrand(), vehicle.getModel(), vehicle.getVehicleId()));
        sb.append(String.format("Rate / Day  : Rs.%,.2f%n", vehicle.getRatePerDay()));
        sb.append(String.format("Rented On   : %s%n", rentDate));
        sb.append(String.format("Due Date    : %s%n", getDueDate()));
        sb.append(String.format("Returned On : %s%n", isReturned() ? returnDate.toString() : "Not returned yet"));
        sb.append(String.format("Days Booked : %d%n", days));
        sb.append(thin);
        sb.append(String.format("Rental Cost : Rs.%,.2f%n", totalCost));
        sb.append(String.format("Late Days   : %d%n", getLateDays()));
        sb.append(String.format("Late Fee    : Rs.%,.2f%s%n", getLateFee(), isReturned() ? "" : "  (so far)"));
        sb.append(thin);
        sb.append(String.format("TOTAL       : Rs.%,.2f%n", getGrandTotal()));
        sb.append(line);
        sb.append(String.format("Late fee = %.1fx daily rate per late day%n", LATE_FEE_MULTIPLIER));
        return sb.toString();
    }
}
