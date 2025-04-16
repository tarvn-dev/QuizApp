package cs.tarun.QuizApp.controllers;

import cs.tarun.QuizApp.config.JwtUtil;
import cs.tarun.QuizApp.dto.TokenResponse;
import cs.tarun.QuizApp.dto.UserLoginDTO;
import cs.tarun.QuizApp.dto.UserRegistrationDTO;
import cs.tarun.QuizApp.models.Admin;
import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.User;
import cs.tarun.QuizApp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = userService.authenticateUser(loginDTO);
        String username = loginDTO.getUsername();
        String role = jwtUtil.getClaimFromToken(token, claims -> claims.get("role", String.class));

        TokenResponse response = new TokenResponse(token, username, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerPlayer(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        Player player = userService.registerPlayer(registrationDTO);

        String token = jwtUtil.generateToken(player.getUsername(), "PLAYER");
        TokenResponse response = new TokenResponse(token, player.getUsername(), "PLAYER");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<TokenResponse> createAdmin(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        Admin admin = userService.createAdmin(registrationDTO);

        String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");
        TokenResponse response = new TokenResponse(token, admin.getUsername(), "ADMIN");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserRegistrationDTO updateDTO) {
        // Extract username from JWT token
        String username = extractUsernameFromToken(token);

        User updatedUser = userService.updateUserProfile(username, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Helper method to extract username from token
    private String extractUsernameFromToken(String authHeader) {
        // Assuming token is in format "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUsernameFromToken(token);
        }
        throw new RuntimeException("Invalid or missing Authorization header");
    }
}