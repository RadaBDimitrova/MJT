package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.exceptions.UserAlreadyExistsException;
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

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;

public class InMemoryUserRepository implements UserRepository {
    private static final String USERS_FILE = "src/users/users.txt";
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        loadAllUsersFromFile();
    }

    private synchronized void loadAllUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                User user = new User(tokens[0], Integer.parseInt(tokens[1]));
                usersByEmail.put(tokens[0], user);
            }
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Could not load users", e);
        }
    }

    @Override
    public synchronized User createUser(String email, String password) throws UserAlreadyExistsException {
        try {
            loadAllUsersFromFile();
            verifyUserDoesNotExist(email);
        } catch (IllegalArgumentException e) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        User newUser = new User(email, password.hashCode());
        usersByEmail.put(email, newUser);
        saveUserToFile(newUser);
        return newUser;
    }

    protected synchronized void verifyUserDoesNotExist(String email) {
        if (usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
    }

    @Override
    public synchronized void saveUserToFile(User newUser) {
        try (FileWriter writer = new FileWriter(USERS_FILE, true)) {
            writer.write(newUser.email() + "," + newUser.password() + System.lineSeparator());
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Could not save user to file", e);
        }
    }

    @Override
    public synchronized void deleteUser(User user) {
        usersByEmail.remove(user.email());
        deleteUserFromFile(user);
    }

    @Override
    public synchronized void deleteUserFromFile(User user) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(user.email())) {
                    lines.add(line);
                }
            }
            reader.close();
            for (String updated : lines) {
                writer.write(updated);
                writer.newLine();
            }
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Error deleting user from file of users: " + user.email(), e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return usersByEmail.get(email);
    }

    @Override
    public synchronized boolean authenticateUser(String email, String password) {
        loadAllUsersFromFile();
        User user = getUserByEmail(email);
        System.out.println(user);
        return user != null && user.password().equals(password.hashCode());
    }

}
