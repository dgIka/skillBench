package repository;

import model.TestResult;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class TestResultRepository {

    private final SessionFactory sf;


    public TestResultRepository(SessionFactory sf) {
        this.sf = sf;
    }

    public Integer save(TestResult r) {
        sf.inTransaction(session -> {
            session.persist(r);
        });
        return r.getId();
    }

    public Optional<TestResult> findById(Integer id) {
        return Optional.ofNullable(sf.getCurrentSession().get(TestResult.class, id));
    }

    public void delete(TestResult r) {
        sf.inTransaction(session -> {session.remove(r);});
    }

    public List<TestResult> findAll() {
        return sf.fromTransaction(session -> session.createSelectionQuery("from TestResult", TestResult.class).getResultList());
    }

    public List<TestResult> selectAllByUserId(int userId) {
        return sf.fromTransaction(session -> session.createSelectionQuery("from TestResult tr " +
                "where tr.user.id = :userId", TestResult.class).setParameter("userId", userId).getResultList());
    }

    public List<TestResult> selectAllByTestId(int testId) {
        return sf.fromTransaction(session -> session.createSelectionQuery("from TestResult tr " +
                "where tr.test.id = :testId", TestResult.class).setParameter("testId", testId).getResultList());
    }

    public List<Object[]> countByTestId() {
        return sf.fromTransaction(s ->
                s.createQuery("select tr.test.id, count(tr) from TestResult tr group by tr.test.id")
                        .getResultList()
        );
    }

}
