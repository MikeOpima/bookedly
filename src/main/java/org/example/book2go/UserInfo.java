package org.example.book2go;

//import java.util.List;
import java.util.Objects;

public class UserInfo {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String password;
    private String role;
    private boolean enabled;  // boolean so we can deactivate an account without deleting it

    public UserInfo() {
    }

    public UserInfo(String userId, String userName, String userEmail, String userPhone, String password, String role, boolean enabled) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return enabled == userInfo.enabled && Objects.equals(userId, userInfo.userId) && Objects.equals(userName, userInfo.userName) && Objects.equals(userEmail, userInfo.userEmail) && Objects.equals(userPhone, userInfo.userPhone) && Objects.equals(password, userInfo.password) && Objects.equals(role, userInfo.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, userEmail, userPhone, password, role, enabled);
    }
}
