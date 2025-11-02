package service;

import lombok.AllArgsConstructor;
import model.Test;
import org.hibernate.SessionFactory;
import repository.AnswerRepository;
import repository.QuestionRepository;
import repository.TestRepository;

import java.util.List;



@AllArgsConstructor
public class TestService {

    private SessionFactory sf;

    private TestRepository testRepository;

    private QuestionRepository questionRepository;

    private AnswerRepository answerRepository;


    public List<Test> getTests() {
        return testRepository.getAll();
    }

    public List<String> getAllThemes() {
        return sf.fromTransaction(session -> (
            session.createQuery("select distinct t.theme from Test t").list()
        ));
    }


}
