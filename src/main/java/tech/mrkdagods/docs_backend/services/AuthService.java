package tech.mrkdagods.docs_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.mrkdagods.docs_backend.models.Session;
import tech.mrkdagods.docs_backend.models.User;
import tech.mrkdagods.docs_backend.repositories.SessionRepository;
import tech.mrkdagods.docs_backend.repositories.UserRepository;
import tech.mrkdagods.docs_backend.utils.AuthUtils;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public AuthService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    /// Creates a new user
    public boolean createUser(String username, String email, String password) {
        // unique username & email
        if (userRepository.findByUsername(username) != null ||
                userRepository.findByEmail(email) != null) {
            return false;
        }

        // validate
        if (!AuthUtils.validateUsername(username) ||
                !AuthUtils.validateEmail(email) ||
                !AuthUtils.validatePassword(password)) {
            return false;
        }

        User user = new User(UUID.randomUUID().toString(), username, email, AuthUtils.calculateHash(password));
        userRepository.save(user);

        return true;
    }

    /// Logs in the user, and returns the newly created session
    public Session loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        final var foundUser = user != null && user.getPasswordHash().equals(AuthUtils.calculateHash(password));
        if (!foundUser) {
            return null;
        }

        // create new session, delete old ones too?
        var oldSessions = sessionRepository.findAllByUserId(user.getId());
        if (!oldSessions.isEmpty()) {
            sessionRepository.deleteAll(oldSessions);
        }

        // create new session
        var newSession = new Session(AuthUtils.generateSessionString(), user.getId());
        sessionRepository.save(newSession);
        return newSession;
    }

    /// Deletes a session if exists
    public void logoutUser(String sessionId) {
        var session = sessionRepository.findById(sessionId);
        if (session.isPresent()) {
            sessionRepository.deleteById(sessionId);
        }
    }

    /// Retrieves a user by id
    public User fetchUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> searchUsers(String query) {
        return userRepository
                .findAll()
                .stream()
                .filter(x -> x.getUsername().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
