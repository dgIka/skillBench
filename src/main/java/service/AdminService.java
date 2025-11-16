package service;


import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import repository.UserRepository;

@AllArgsConstructor
public class AdminService {

    private UserRepository userRepository;
    private SessionFactory sessionFactory;

    public void changeRole(int userId, String role) {}

    public void changeActive(int userId, boolean active) {
        sessionFactory.inTransaction(session -> {
            userRepository.findById(userId).setIsActive(active);
        });
    }

    public void getAllUsers() {}
}
