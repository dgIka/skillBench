package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import model.Answer;
import model.Question;
import model.Test;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import repository.AnswerRepository;
import repository.QuestionRepository;
import repository.TestRepository;

import java.util.Collections;

@WebServlet(urlPatterns = "/_dev", loadOnStartup = 1)
public class DevRunnerServlet extends HttpServlet {

    private SessionFactory sf;
    private TestRepository testRepo;
    private QuestionRepository questionRepo;
    private AnswerRepository answerRepo;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        sf = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
        if (sf == null) throw new ServletException("SessionFactory not found");

        testRepo = new TestRepository(sf);
        questionRepo = new QuestionRepository(sf);
        answerRepo = new AnswerRepository(sf);

        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            Answer answer = new Answer();
            answer.setText("Test answer");
            Question question = new Question();
            question.setText("Test question");
            question.setAnswers(Collections.singletonList(answer));
            Test test = new Test();
            test.setName("Test name2");
            test.setTheme("Test theme");
            test.setQuestions(Collections.singletonList(question));
            testRepo.save(test);
            tx.commit();
        } catch (Exception e) {
            throw new ServletException("Dev runner failed", e);
        }
    }
}
