package repository;

import model.Answer;
import org.hibernate.SessionFactory;

public class AnswerRepository {

    private final SessionFactory sf;

    public AnswerRepository(SessionFactory sf) {
        this.sf = sf;
    }

    public Integer save(Answer answer) {
        sf.inTransaction(session -> {session.persist(answer);});
        return answer.getId();
    }


    public void delete(Answer answer) {
        sf.inTransaction(session -> {
            session.delete(answer);
        });
    }
}
