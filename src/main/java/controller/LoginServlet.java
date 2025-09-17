package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import security.LoginRateLimiter;
import service.AuthService;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;
    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.authService = (AuthService) config.getServletContext().getAttribute("authService");
        this.templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        this.app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");

        if (authService == null || templateEngine == null || app == null) {
            throw new ServletException("LoginServlet init failed: missing dependencies in ServletContext");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
//        req.getRequestDispatcher("/WEB-INF/templates/login.html").forward(req, resp);
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("login", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        String clientKey = email.toLowerCase();
        if (!LoginRateLimiter.isAllowed(clientKey)) {
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            req.setAttribute("error", "To many attempts, please try later");
//            req.getRequestDispatcher("/WEB-INF/templates/login.html").forward(req, resp);
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("login", context, resp.getWriter());
            return;
        }

        try {
            User user = authService.login(email, password);
            req.getSession().setAttribute("USER_ID", user.getId());
            resp.sendRedirect("home"); // временно !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        } catch (IllegalArgumentException e) {
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("error", e.getMessage());
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("login", context, resp.getWriter());
        }
    }
}
