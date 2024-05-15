package tech.mrkdagods.docs_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mrkdagods.docs_backend.models.Session;
import tech.mrkdagods.docs_backend.models.User;
import tech.mrkdagods.docs_backend.services.AuthService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Map<String, String> body) {
        final var result = authService.createUser(body.get("username"),
                body.get("email"),
                body.get("password"));
        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 401
    }

    @PostMapping("/login")
    public ResponseEntity<Session> login(@RequestBody Map<String, String> body) {
        final var session = authService.loginUser(body.get("username"), body.get("password"));
        return new ResponseEntity<>(session, session != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        authService.logoutUser(body.get("sessionId"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/fetch")
    public ResponseEntity<User> fetch(@RequestBody Map<String, String> body) {
        final var user = authService.fetchUser(body.get("userId"));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<User>> search(@RequestBody Map<String, String> body) {
        final var users = authService.searchUsers(body.get("query"));
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
