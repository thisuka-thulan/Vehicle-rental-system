/*
 * Base class for anybody that can log into the system.
 * Admin and Customer both build on top of this so we don't
 * repeat the username/password/name handling twice.
 */
public abstract class User {

    private String name;
    private String username;
    private String password;

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public boolean checkPassword(String attempt) {
        // simple equality check for now, nothing fancy needed for a project like this
        if (attempt == null) return false;
        return password.equals(attempt);
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    // only here so DataStore can write the account to the file
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return name + " (" + username + ")";
    }
}
