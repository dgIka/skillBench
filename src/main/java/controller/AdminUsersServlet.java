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
import repository.UserRepository;
import service.AdminService;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;
    private AdminService adminService;
    private UserRepository userRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
        adminService = (AdminService) config.getServletContext().getAttribute("adminService");
        userRepository = (UserRepository) config.getServletContext().getAttribute("userRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        List<User> all = userRepository.findAll();
        context.setVariable("users", all);


        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("admin_users", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var session = req.getSession(false);
        Object role = (session != null) ? session.getAttribute("role") : null;
        if (role == null || !"ADMIN".equals(role.toString())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        String idStr  = req.getParameter("id");

        if (action == null || idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        try {
            int userId = Integer.parseInt(idStr);
            if ("block".equals(action)) {
                adminService.changeActive(userId, false);
            } else if ("unblock".equals(action)) {
                adminService.changeActive(userId, true);
            }
        } catch (Exception ignored) {
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
