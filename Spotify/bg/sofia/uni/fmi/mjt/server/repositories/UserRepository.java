package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.server.profile.User;

public interface UserRepository {
    User createUser(String email, String password) throws UserAlreadyExistsException;

    void deleteUser(User user);

    User getUserByEmail(String email);

    boolean authenticateUser(String email, String password);

    void saveUserToFile(User newUser);

    void deleteUserFromFile(User user);

}

