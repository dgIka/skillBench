package controller;

import dto.auth.AttemptDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import service.TestResultService;
import service.TestService;

import java.io.IOException;

@WebServlet("/tests/result")
public class TestResultServlet extends HttpServlet {

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
            return;
        }

        AttemptDTO attemptDTO = (AttemptDTO) req.getSession(false).getAttribute(attemptKey(testId));


        Test test = testService.getWithQuestions(attemptDTO.getTestId());

        context.setVariable("test", test);
        context.setVariable("trueAnswers", attemptDTO.getTrueCount());
        context.setVariable("falseAnswers", attemptDTO.getFalseCount());
        context.setVariable("totalQuestions", test.getQuestions().size());
        context.setVariable("choices", attemptDTO.getChoices());

        attemptDTO.getChoices().forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });

        req.getSession(false).removeAttribute(attemptKey(testId));

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("result", context, resp.getWriter());


    }

    private String attemptKey(String testId) {
        return "attempt:test:" + testId;
    }
}
