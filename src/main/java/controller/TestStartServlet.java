package controller;

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
        System.out.println(test.getQuestions().get(0));
        context.setVariable("question", test.getQuestions().get(0));

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("start", context, resp.getWriter());


    }
}
