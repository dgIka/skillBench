package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Role;
import model.User;
// ↓ добавь при использовании репозитория/сервиса
import repository.UserRepository; // <-- если пользуешься репозиторием

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AuthorizationFilter implements Filter {

    // ↓ если хочешь подтягивать роль из БД по uid
    private UserRepository userRepository; // <-- репозиторий (или UserService)

    @Override
    public void init(FilterConfig filterConfig) {
        // ↓ достань репозиторий/сервис из ServletContext, если нужно
        // this.userRepository = (UserRepository) filterConfig.getServletContext().getAttribute("userRepository");
        // или: this.userService = (UserService) ...
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("uid") == null) {
            // не авторизован → на логин с возвратом
            String next = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
            resp.sendRedirect(req.getContextPath() + "/login?next=" + resp.encodeURL(next));
            return;
        }

        // быстрый путь: если в сессии уже лежит роль — используем её
        Object roleAttr = session.getAttribute("role"); // положи в сессию при логине: session.setAttribute("role", user.getRole());
        Role role = null;
        if (roleAttr instanceof Role r) {
            role = r;
        } else {
            // медленный путь: достать из БД по uid (если НЕ кладёшь роль в сессию)
            Integer uid = (Integer) session.getAttribute("uid");
            if (uid != null && userRepository != null) { // <-- нужен репозиторий/сервис
                User u = userRepository.findById(uid).orElse(null);
                role = (u != null ? u.getRole() : null);
            }
        }

        if (role != Role.ADMIN) {
            // нет прав → 403 (или редирект на /home — как хочешь)
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin area only");
            return;
        }

        // всё ок, пропускаем дальше
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}