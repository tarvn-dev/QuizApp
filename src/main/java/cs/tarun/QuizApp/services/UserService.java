package cs.tarun.QuizApp.services;

import cs.tarun.QuizApp.config.JwtUtil;
import cs.tarun.QuizApp.db.AdminRepository;
import cs.tarun.QuizApp.db.PlayerRepository;
import cs.tarun.QuizApp.db.UserRepository;
import cs.tarun.QuizApp.dto.UserLoginDTO;
import cs.tarun.QuizApp.dto.UserRegistrationDTO;
import cs.tarun.QuizApp.models.Admin;
import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String authenticateUser(UserLoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                return jwtUtil.generateToken(user.getUsername(), user.getRole().toString());
            }
        }

        throw new RuntimeException("Invalid username or password");
    }

    @Transactional
    public Player registerPlayer(UserRegistrationDTO registrationDTO) {
        // Check if username or email is already taken
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new player
        Player player = new Player(
                registrationDTO.getUsername(),
                passwordEncoder.encode(registrationDTO.getPassword()),
                registrationDTO.getEmail(),
                registrationDTO.getFullName()
        );

        return playerRepository.save(player);
    }

    @Transactional
    public Admin createAdmin(UserRegistrationDTO registrationDTO) {
        // Check if username or email is already taken
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new admin
        Admin admin = new Admin(
                registrationDTO.getUsername(),
                passwordEncoder.encode(registrationDTO.getPassword()),
                registrationDTO.getEmail(),
                registrationDTO.getFullName()
        );

        return adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        // Get the authenticated username from Security Context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUserProfile(String username, UserRegistrationDTO updateDTO) {
        User user = getUserByUsername(username);

        // Update fields
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }

        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        return userRepository.save(user);
    }
}