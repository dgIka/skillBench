package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import service.TestResultService;
import service.TestService;

import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;

    @Override
    public void init(ServletConfig config) {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("admin", context, resp.getWriter());
    }
}
