package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TestResult;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import service.TestResultService;
import service.TestService;

import java.io.IOException;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;
    private TestService testService;
    private TestResultService testResultService;

    @Override
    public void init(ServletConfig config) {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
        testService = (TestService) config.getServletContext().getAttribute("testService");
        testResultService = (TestResultService) config.getServletContext().getAttribute("testResultService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        String userId = req.getSession(false).getAttribute("uid").toString();

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<TestResult> results = testResultService.getAllByUser(Integer.parseInt(userId));

        context.setVariable("results", results);

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("history", context, resp.getWriter());

    }
}
