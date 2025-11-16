package service;


import lombok.AllArgsConstructor;
import model.User;
import org.hibernate.SessionFactory;
import repository.UserRepository;

@AllArgsConstructor
public class AdminService {

    private UserRepository userRepository;
    private SessionFactory sessionFactory;

    public void changeRole(int userId, String role) {}

    public void changeActive(int userId, boolean active) {
        sessionFactory.inTransaction(session -> {
            User u = session.get(User.class, userId);
            if (u == null) throw new IllegalArgumentException("User not found: " + userId);
            u.setIsActive(active);
        });
    }

    public void getAllUsers() {}
}
