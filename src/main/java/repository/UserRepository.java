package repository;

import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final SessionFactory sf;

    public UserRepository(SessionFactory sessionFactory) {
        this.sf = sessionFactory;
    }

    public Integer save(User user) {
        sf.inTransaction(session -> {
            session.persist(user);
        });
        return user.getId();
    }

    public Optional<User> findById(int id) {
            return Optional.ofNullable(sf.getCurrentSession().get(User.class, id));
    }

    public Optional<User> findByUserEmail(String email) {
       return sf.fromTransaction(session ->
                session.createQuery("select u from User u where u.email = :email", User.class)
                        .setParameter("email", email).uniqueResultOptional());

    }

    public boolean existsByEmail(String email) {
        return sf.fromTransaction(session ->
            session.createQuery(
                            "select 1 from User u where u.email = :email", Integer.class
                    ).setParameter("email", email)
                    .setMaxResults(1)
                    .uniqueResultOptional()
                    .isPresent()
        );
    }

    public void update(User user) {
        sf.inTransaction(session -> {
            User user1 = session.get(User.class, user.getId());
            user1.setName(user.getName());
            user1.setEmail(user.getEmail());
            user1.setRole(user.getRole());
            user1.setPasswordHash(user.getPasswordHash());
            user1.setIsActive(user.getIsActive());
        });
    }

    public void delete(User user) {
        sf.inTransaction(session -> {
            session.delete(user);
        });
    }

    public List<User> findAll() {
        return sf.fromSession(session -> session.createQuery("from User", User.class).list());
    }

}
