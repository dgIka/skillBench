package repository;

import model.Question;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class QuestionRepository {

    private final SessionFactory sf;

    public QuestionRepository(SessionFactory sf) {
        this.sf = sf;
    }

    public Integer save(Question question) {
        sf.inTransaction(session -> {
            session.persist(question);
        });
        return question.getId();
    }

    public Optional<Question> findById(Integer id) {
        return Optional.ofNullable(sf.getCurrentSession().get(Question.class, id));
    }

    public void delete(Question question) {
        sf.getCurrentSession().delete(question);
    }

    public void update(Question question) {
        sf.inTransaction(session -> {
            Question questionToUpdate = session.get(Question.class, question.getId());
            questionToUpdate.setText(question.getText());
        });

    }

}
