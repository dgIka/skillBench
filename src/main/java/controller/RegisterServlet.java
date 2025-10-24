package controller;

import dto.auth.LoginDto;
import dto.auth.RegisterDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import service.AuthService;

import jakarta.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
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
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
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

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(email);
        registerDto.setPassword(password);
        registerDto.setPassword2(password2);
        registerDto.setName(name);

        if (!password.equals(password2)) {
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("error", "Passwords must be the same");
            resp.setContentType("text/html;charset=UTF-8");
            templateEngine.process("register", context, resp.getWriter());
            return;
        }

        Set<ConstraintViolation<RegisterDto>> violations = validator.validate(registerDto);
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            violations.forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
            WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
            context.setVariable("dto", registerDto);
            context.setVariable("errors", errors);
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
