package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Answer;
import model.Question;
import model.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import service.TestService;

import java.io.IOException;

@WebServlet("/tests/start")
public class TestStartServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;
    private TestService testService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
        testService = (TestService) config.getServletContext().getAttribute("testService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        String testId = req.getParameter("id");
        if (testId == null) {
            resp.sendRedirect(req.getContextPath() + "/tests");
        }

        Test test = testService.getWithQuestions(Integer.parseInt(testId));
        context.setVariable("test", test);
        context.setVariable("totalQuestions", test.getQuestions().size());

        String index = req.getParameter("questionIndex");
        if (index != null) {
            context.setVariable("question", test.getQuestions().get(Integer.parseInt(index)));
        } else {
            context.setVariable("question", test.getQuestions().get(0));
            int questionIndex = 0;
            context.setVariable("questionIndex", questionIndex);
        }

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("start", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        String testId = req.getParameter("testId");
        if (testId == null) {
            resp.sendRedirect(req.getContextPath() + "/tests");
            return;
        }

        Test test = testService.getWithQuestions(Integer.parseInt(testId));
        context.setVariable("test", test);
        String index = req.getParameter("questionIndex");
        if (index == null) {
            context.setVariable("question", test.getQuestions().get(0));
            int questionIndex = 0;
            context.setVariable("questionIndex", questionIndex);
        } else {
            context.setVariable("question", test.getQuestions().get(Integer.parseInt(index)));
            context.setVariable("questionIndex", Integer.parseInt(index) + 1);
        }

        String answerId = req.getParameter("answerId");
        System.out.println("answerId = " + answerId);
        if (answerId != null && index != null) {
            try {
                boolean isCorrect = testService.isCorrectAnswer(test, Integer.parseInt(index), Integer.parseInt(answerId));
                context.setVariable("isCorrect", isCorrect);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tests");
                return;
            }

        } else {
            context.setVariable("isCorrect", null);
        }

        context.setVariable("hasNext", test.getQuestions().size() > (Integer.parseInt(index) + 1));
        context.setVariable("totalQuestions", test.getQuestions().size());


        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("start", context, resp.getWriter());
    }
}
