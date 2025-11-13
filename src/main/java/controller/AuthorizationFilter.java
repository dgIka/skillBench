package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Role;
import model.User;
// ↓ добавь при использовании репозитория/сервиса
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import repository.UserRepository; // <-- если пользуешься репозиторием
import service.AuthService;
import service.TestResultService;
import service.TestService;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AuthorizationFilter implements Filter {


    private AuthService authService;
    private TemplateEngine templateEngine;

    @Override
    public void init(FilterConfig config) {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        authService = (AuthService) config.getServletContext().getAttribute("authService");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("uid") == null) {
            String next = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
            resp.sendRedirect(req.getContextPath() + "/login?next=" + resp.encodeURL(next));
            return;
        }

        Object roleAttr = session.getAttribute("role");
        Role role = null;
        if (roleAttr instanceof Role r) {
            role = r;
        } else {
            Integer uid = (Integer) session.getAttribute("uid");
            if (uid != null) {
                User u = authService.getUserById(uid);
            }
        }

        if (role != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin area only");
            return;
        }

        chain.doFilter(request, response);
    }
}