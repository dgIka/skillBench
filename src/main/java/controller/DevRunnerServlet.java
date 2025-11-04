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
import service.TestService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = "/_dev", loadOnStartup = 1)
public class DevRunnerServlet extends HttpServlet {

    private SessionFactory sf;
    private TestService testService;
    private TestRepository testRepo;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        sf = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
        if (sf == null) throw new ServletException("SessionFactory not found");

        testService = (TestService) config.getServletContext().getAttribute("testService");

        testRepo = new TestRepository(sf);

        List<Test> all = testService.getTests();
        System.out.println(all);
        System.out.println("CHECK");


    }
}
