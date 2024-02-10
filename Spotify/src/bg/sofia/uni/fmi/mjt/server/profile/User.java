package bg.sofia.uni.fmi.mjt.server.profile;

public class User {
    private String email;
    private Integer password;

    public User(String email, Integer password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPassword() {
        return password;
    }

}
