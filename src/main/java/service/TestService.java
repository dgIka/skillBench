package service;

import lombok.AllArgsConstructor;
import model.Answer;
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

    public List<Test> getTestsByTheme(String theme) {
        return testRepository.getByTheme(theme);
    }

    public Test getTestById(int id) {
        return testRepository.getById(id);
    }

    public Test getWithQuestions(int id) {
        return testRepository.getWithQuestionsById(id);
    }

    public boolean isCorrectAnswer(Test test, int index, int answerId) {
        List<Answer> answers = test.getQuestions().get(index).getAnswers();
        return answers.stream().filter(answer -> answer.getId().equals(answerId))
                .findFirst()
                .orElseThrow()
                .isCorrect();
    }

    public int getQuestionsCount(Test test) {
        return test.getQuestions().size();
    }


}
