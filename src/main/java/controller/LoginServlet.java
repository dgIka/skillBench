package controller;

import dto.auth.LoginDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import security.LoginRateLimiter;
import service.AuthService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;
    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;
    private Validator validator;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.authService = (AuthService) config.getServletContext().getAttribute("authService");
        this.templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        this.app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
        this.validator = (Validator) config.getServletContext().getAttribute("validator");

        if (authService == null || templateEngine == null || app == null) {
            throw new ServletException("LoginServlet init failed: missing dependencies in ServletContext");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("login", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email == null ? null : email.trim().toLowerCase(Locale.ROOT));
        loginDto.setPassword(password);

        Set<ConstraintViolation<LoginDto>> violations = validator.validate(loginDto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            violations.forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
            WebContext ctx = new WebContext(app.buildExchange(req, resp), req.getLocale());
            ctx.setVariable("dto", loginDto);
            ctx.setVariable("errors", errors);
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("login", ctx, resp.getWriter());
            return;
        }

        String clientKey = loginDto.getEmail();
        if (!LoginRateLimiter.isAllowed(clientKey)) { //проверка на брутфорс
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("error", "To many attempts, please try later");
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("login", context, resp.getWriter());
            return;
        }

        try {
            User user = authService.login(email, password);
            req.getSession().setAttribute("uid", user.getId());
            req.getSession().setAttribute("user", user);
            String next = req.getParameter("next");
            if (next != null && !next.isBlank()) {
                resp.sendRedirect(next);
            } else {
                resp.sendRedirect("/home");
            }
        } catch (IllegalArgumentException e) {
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("error", e.getMessage());
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("login", context, resp.getWriter());
        }
    }
}
