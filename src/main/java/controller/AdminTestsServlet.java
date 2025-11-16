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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/tests")
public class AdminTestsServlet extends HttpServlet {

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
        WebContext ctx = new WebContext(app.buildExchange(req, resp), req.getLocale());

        Map<String, List<Test>> grouped = testService != null ? testService.getTestsGroupedByTheme() : null;
        Map<Integer, Long> attempts = testService != null ? testService.getAttemptsCountMap() : null;


        if (grouped == null) grouped = Collections.emptyMap();
        if (attempts == null) attempts = Collections.emptyMap();

        ctx.setVariable("grouped",  testService.getTestsGroupedByTheme());
        ctx.setVariable("attempts", testService.getAttemptsCountMap());

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("admin_tests", ctx, resp.getWriter());
    }
}
