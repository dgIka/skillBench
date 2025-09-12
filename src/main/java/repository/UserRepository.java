package repository;

import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Optional;

public class UserRepository {

    private final SessionFactory sf;

    public UserRepository(SessionFactory sessionFactory) {
        this.sf = sessionFactory;
    }

    public void save(User user) {
        sf.inTransaction(session -> {
            session.persist(user);
        });
    }

    public Optional<User> findById(int id) {
            return Optional.ofNullable(sf.getCurrentSession().get(User.class, id));
    }

    public Optional<User> findByUserEmail(String email) {
        Query query = sf.getCurrentSession().createQuery("from User where email = :email", User.class).setParameter("email", email);
        return Optional.ofNullable((User) query.uniqueResult());

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

    public void delete(User user) {
        sf.inTransaction(session -> {
            session.delete(user);
        });
    }

}
