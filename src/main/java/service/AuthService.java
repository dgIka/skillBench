package service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import model.User;
import repository.UserRepository;
import security.PasswordUtil;

import java.util.Objects;


@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User register(String email, String password) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(password);

        userRepository.findByUserEmail(email).ifPresent(user -> {
            throw new IllegalArgumentException("User already exists");
        });

        String hash = PasswordUtil.hash(password);
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hash);
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByUserEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User or password are incorrect"));

        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Wrong email or password");
        }

        if (PasswordUtil.needsRehash(password)) {
            user.setPasswordHash(PasswordUtil.hash(password));
            userRepository.update(user);
        }
        return user;
    }

}
