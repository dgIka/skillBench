package repository;

import model.Test;
import org.hibernate.SessionFactory;

import java.util.List;

public class TestRepository {

    private final SessionFactory sf;

    public TestRepository(SessionFactory sf) {
        this.sf = sf;
    }

    public Integer save(Test test) {
        sf.inTransaction(session -> {
            session.persist(test);
        });
    return test.getId();
    }

    public void delete(Test test) {
        sf.getCurrentSession().delete(test);
    }

    public void update(Test test) {
        sf.inTransaction(session -> {
            Test testToUpdate = session.get(Test.class, test.getId());
            testToUpdate.setName(test.getName());
            testToUpdate.setTheme(test.getTheme());
        });
    }

    public List<Test> getAll() {
        return sf.fromTransaction(session -> session.createQuery("select t from Test t " +
                "left join fetch t.questions", Test.class).list());
    }
}
