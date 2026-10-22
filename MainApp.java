import java.time.LocalDate;
import javax.swing.SwingUtilities;

public class MainApp {

    public static void main(String[] args) {
        RentalSystem system = new RentalSystem();

        // load whatever was saved last time, only fill in demo data on the very first run
        boolean hadSavedData = system.load();
        if (!hadSavedData) {
            addDemoData(system);
        }

        SwingUtilities.invokeLater(() -> new LoginFrame(system).setVisible(true));
    }

    private static void addDemoData(RentalSystem system) {
        system.addVehicle(new Car("C001", "Toyota", "Corolla", 5000, 5));
        system.addVehicle(new Car("C002", "Honda", "Civc", 5500, 5));
        system.addVehicle(new Car("C003", "Suzuki", "Ato", 3500, 4));
        system.addVehicle(new Bike("B001", "Honda", "CB125", 1500, 125));
        system.addVehicle(new Bike("B002", "Yamaha", "FZ", 1800, 150));

        Customer kasun = new Customer("CUS001", "kasun", "pass123", "Kasun Perera", "0771234567");
        system.addCustomer(kasun);
        system.addCustomer(new Customer("CUS002", "nimali", "pass456", "Nimali Silva", "0719876543"));

        // one rental that is already overdue, so the late fee can be seen straight away
        Vehicle civic = system.findVehicleById("C002");
        civic.setAvailable(false);
        system.getRentals().add(new Rental("R001", civic, kasun, 3,
                civic.calculateRentalCost(3), LocalDate.now().minusDays(7), null, 0));
        system.save();
    }
}
