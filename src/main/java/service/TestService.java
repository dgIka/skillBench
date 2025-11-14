package service;

import dto.test.AnswerDto;
import dto.test.QuestionDto;
import dto.test.TestDto;
import lombok.AllArgsConstructor;
import model.Answer;
import model.Question;
import model.Test;
import org.hibernate.SessionFactory;
import repository.AnswerRepository;
import repository.QuestionRepository;
import repository.TestRepository;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class TestService {

    private SessionFactory sf;

    private TestRepository testRepository;

    private QuestionRepository questionRepository;

    private AnswerRepository answerRepository;

    public int createTest(TestDto test, List<QuestionDto> questions) {
        Test testEntity = new Test();
        testEntity.setName(test.getName());
        testEntity.setTheme(test.getTheme());

        List<Question> questionList = new ArrayList<>();
        testEntity.setQuestions(questionList);

        for (QuestionDto question : questions) {
            List<Answer> answerList = new ArrayList<>();

            Question questionEntity = new Question();

            questionEntity.setAnswers(answerList);

            questionEntity.setMainTest(testEntity);
            questionEntity.setText(question.getText());

            for (AnswerDto a : question.getAnswers()) {
                Answer answer = new Answer();
                answer.setMainQuestion(questionEntity);
                answer.setText(a.getText());
                questionEntity.getAnswers().add(answer);
            }

            questionEntity.getAnswers().get(question.getCorrectIndex()).setCorrect(true);
            testEntity.getQuestions().add(questionEntity);
        }
        return testRepository.save(testEntity);
    }

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
