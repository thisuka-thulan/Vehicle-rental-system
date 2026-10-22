public class Admin extends User {

    private String adminId;

    public Admin(String adminId, String username, String password, String name) {
        super(username, password, name);
        this.adminId = adminId;
    }

    public String getAdminId() {
        return adminId;
    }
}
