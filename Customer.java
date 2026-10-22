public class Customer extends User {

    private String customerId;
    private String phone;

    public Customer(String customerId, String username, String password, String name, String phone) {
        super(username, password, name);
        this.customerId = customerId;
        this.phone = phone;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPhone() {
        return phone;
    }

    public void printDetails() {
        System.out.println(customerId + " -> " + getName() + ", phone: " + phone);
    }
}
