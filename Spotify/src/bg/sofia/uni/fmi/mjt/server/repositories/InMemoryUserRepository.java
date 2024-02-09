package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.profile.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private static final String USERS_FILE = "users.txt";
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public User createUser(String email, String password) throws IllegalArgumentException {
        verifyUserDoesNotExist(email); //TODO do better
        User newUser = new User(password, email);
        usersByEmail.put(email, newUser);
        saveUserToFile(newUser);
        return newUser;
    }

    private void verifyUserDoesNotExist(String email) {
        if (usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
    }

    @Override
    public void saveUserToFile(User newUser) {
        try (FileWriter writer = new FileWriter(USERS_FILE, true)) {
            writer.write(newUser.getEmail() + "," + newUser.getPassword() + System.lineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save user to file", e);
        }
    }

    @Override
    public void deleteUser(User user) {
        usersByEmail.remove(user.getEmail());
        deleteUserFromFile(user);
    }

    @Override
    public void deleteUserFromFile(User user) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(user.getEmail())) {
                    lines.add(line);
                }
            }
            reader.close();
            for (String updated : lines) {
                writer.write(updated);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting user from file of users: " + user.getEmail(), e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return usersByEmail.get(email);
    }

    @Override
    public boolean authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        return user != null && user.authenticate(password);
    }

}
