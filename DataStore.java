import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

/*
 * Reads and writes everything to plain text files so the data
 * is still there after the app is closed. One line = one record,
 * fields separated by "|".
 */
public class DataStore {

    private static final String DATA_DIR = "data";
    private static final String RECEIPT_DIR = "receipts";

    private static final Path VEHICLE_FILE = Paths.get(DATA_DIR, "vehicles.txt");
    private static final Path CUSTOMER_FILE = Paths.get(DATA_DIR, "customers.txt");
    private static final Path RENTAL_FILE = Paths.get(DATA_DIR, "rentals.txt");

    // make sure a stray "|" or newline can't break the file format
    private static String clean(String s) {
        return s.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }

    public static void save(RentalSystem system) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));

            ArrayList<String> lines = new ArrayList<>();
            for (Vehicle v : system.getVehicles()) {
                lines.add(v.getTypeName() + "|" + clean(v.getVehicleId()) + "|" + clean(v.getBrand())
                        + "|" + clean(v.getModel()) + "|" + v.getRatePerDay() + "|" + v.getExtraValue());
            }
            Files.write(VEHICLE_FILE, lines);

            lines = new ArrayList<>();
            for (Customer c : system.getCustomers()) {
                lines.add(clean(c.getCustomerId()) + "|" + clean(c.getUsername()) + "|" + clean(c.getPassword())
                        + "|" + clean(c.getName()) + "|" + clean(c.getPhone()));
            }
            Files.write(CUSTOMER_FILE, lines);

            lines = new ArrayList<>();
            for (Rental r : system.getRentals()) {
                lines.add(r.getRentalId() + "|" + r.getVehicle().getVehicleId() + "|"
                        + r.getCustomer().getCustomerId() + "|" + r.getDays() + "|" + r.getTotalCost()
                        + "|" + r.getRentDate() + "|" + (r.isReturned() ? r.getReturnDate().toString() : "-")
                        + "|" + (r.isReturned() ? r.getLateFee() : 0));
            }
            Files.write(RENTAL_FILE, lines);
        } catch (IOException e) {
            System.err.println("Could not save data: " + e.getMessage());
        }
    }

    // returns false if there is no saved data yet (first run)
    public static boolean load(RentalSystem system) {
        if (!Files.exists(VEHICLE_FILE)) {
            return false;
        }
        try {
            for (String line : Files.readAllLines(VEHICLE_FILE)) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 6) continue;
                double rate = Double.parseDouble(p[4]);
                int extra = Integer.parseInt(p[5]);
                Vehicle v;
                if (p[0].equals("Car")) {
                    v = new Car(p[1], p[2], p[3], rate, extra);
                } else {
                    v = new Bike(p[1], p[2], p[3], rate, extra);
                }
                system.getVehicles().add(v);
            }

            if (Files.exists(CUSTOMER_FILE)) {
                for (String line : Files.readAllLines(CUSTOMER_FILE)) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split("\\|", -1);
                    if (p.length < 5) continue;
                    system.getCustomers().add(new Customer(p[0], p[1], p[2], p[3], p[4]));
                }
            }

            if (Files.exists(RENTAL_FILE)) {
                for (String line : Files.readAllLines(RENTAL_FILE)) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split("\\|", -1);
                    if (p.length < 8) continue;
                    Vehicle v = system.findVehicleById(p[1]);
                    Customer c = system.findCustomerById(p[2]);
                    if (v == null || c == null) continue; // record points at something that no longer exists
                    LocalDate returned = p[6].equals("-") ? null : LocalDate.parse(p[6]);
                    Rental r = new Rental(p[0], v, c, Integer.parseInt(p[3]), Double.parseDouble(p[4]),
                            LocalDate.parse(p[5]), returned, Double.parseDouble(p[7]));
                    system.getRentals().add(r);
                    if (!r.isReturned()) {
                        v.setAvailable(false);
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Problem while loading data: " + e.getMessage());
        }
        return true;
    }

    // writes the receipt to receipts/receipt_R001.txt and gives back the full path
    public static String saveReceipt(Rental r) throws IOException {
        Files.createDirectories(Paths.get(RECEIPT_DIR));
        Path file = Paths.get(RECEIPT_DIR, "receipt_" + r.getRentalId() + ".txt");
        Files.write(file, r.getReceiptText().getBytes(StandardCharsets.UTF_8));
        return file.toAbsolutePath().toString();
    }
}
