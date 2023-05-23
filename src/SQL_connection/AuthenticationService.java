package SQL_connection;

import java.io.Serializable;

public class AuthenticationService implements Serializable {

    private String password;
    private String salt;
    private boolean passwordExists;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isPasswordExists() {
        return passwordExists;
    }

    public void setPasswordExists(boolean passwordExists) {
        this.passwordExists = passwordExists;
    }

}
