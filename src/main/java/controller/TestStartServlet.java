package controller;

import dto.auth.AttemptDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Answer;
import model.Question;
import model.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import repository.TestRepository;
import repository.TestResultRepository;
import service.TestResultService;
import service.TestService;

import java.io.IOException;

@WebServlet("/tests/start")
public class TestStartServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;
    private TestService testService;
    private TestResultService testResultService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
        testService = (TestService) config.getServletContext().getAttribute("testService");
        testResultService = (TestResultService) config.getServletContext().getAttribute("testResultService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        String testId = req.getParameter("id");
        if (testId == null) {
            resp.sendRedirect(req.getContextPath() + "/tests");
            return;
        }

        Test test = testService.getWithQuestions(Integer.parseInt(testId));
        context.setVariable("test", test);
        context.setVariable("totalQuestions", test.getQuestions().size());


        String index = req.getParameter("q");
        if (index != null) {
            context.setVariable("question", test.getQuestions().get(Integer.parseInt(index)));
            context.setVariable("questionIndex", Integer.parseInt(index));
        } else {
            context.setVariable("question", test.getQuestions().get(0));
            int questionIndex = 0;
            context.setVariable("questionIndex", questionIndex);
        }

        context.setVariable("displayIndex", (index != null ? Integer.parseInt(index) + 1 : 1));
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("start", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        HttpSession session = req.getSession();
        String testId = req.getParameter("testId");
        String index = req.getParameter("questionIndex");
        int questionIndex = 0;

        if (index != null) {
            questionIndex = Integer.parseInt(index);
        }

        if (testId == null || index == null) {
            System.out.println("this is work - testId == null");
            resp.sendRedirect(req.getContextPath() + "/tests");
            return;
        }

        String answerId = req.getParameter("answerId");

        if (answerId == null) {
            resp.sendRedirect(req.getContextPath() + "/tests/start?id=" + testId + "&q=" + index);
            return;
        }

        AttemptDTO attemptDTO = (AttemptDTO) session.getAttribute(attemptKey(testId));
        if (attemptDTO == null) {
            attemptDTO = new AttemptDTO(Integer.parseInt(testId));
            session.setAttribute(attemptKey(testId), attemptDTO);
        }

        Test test = testService.getWithQuestions(Integer.parseInt(testId));
        context.setVariable("test", test);
        context.setVariable("question", test.getQuestions().get(questionIndex));
        context.setVariable("questionIndex", questionIndex + 1);

        try {
            boolean isCorrect = testService.isCorrectAnswer(test, questionIndex, Integer.parseInt(answerId));
            context.setVariable("isCorrect", isCorrect);
            if (isCorrect) {
                attemptDTO.setTrueCount(attemptDTO.getTrueCount() + 1);
            } else {
                attemptDTO.setFalseCount(attemptDTO.getFalseCount() + 1);
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/tests");
            return;
        }

        if (questionIndex < test.getQuestions().size()) {
            context.setVariable("displayIndex", questionIndex + 1);
        } else {
            context.setVariable("displayIndex", test.getQuestions().size());
        }

        boolean hasNext = test.getQuestions().size() > (questionIndex + 1);
        context.setVariable("hasNext", hasNext);
        if (hasNext) {
            context.setVariable("nextIndex", questionIndex + 1);
        }
        context.setVariable("totalQuestions", test.getQuestions().size());

        attemptDTO.getChoices().put(test.getQuestions().get(questionIndex).getId(), Integer.parseInt(answerId));

        if (!hasNext) {
            var uid = session.getAttribute("uid");
            attemptDTO.setCompleted(true);
            testResultService.saveTestResult((int) uid, attemptDTO);
            resp.sendRedirect(req.getContextPath() + "/tests/result?id=" + testId);
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("start", context, resp.getWriter());
    }

    private String attemptKey(String testId) {
        return "attempt:test:" + testId;
    }
}
