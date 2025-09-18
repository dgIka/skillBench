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
import service.AuthService;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
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
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
//        req.getRequestDispatcher("/WEB-INF/views/register.html").forward(req, resp);
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("register", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");
        String name = req.getParameter("name");

        if (email == null || password == null || password2 == null || !password.equals(password2)) {
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("error", "Invalid email or password");
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("register", context, resp.getWriter());
            return;
        }

        try {
            authService.register(email, password, name);
            resp.sendRedirect("/login?registered=1");
        } catch (IllegalArgumentException e) {
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("error", e.getMessage());
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("register", context, resp.getWriter());
        }
    }
}
