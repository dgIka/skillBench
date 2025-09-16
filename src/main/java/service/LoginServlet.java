package service;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import security.LoginRateLimiter;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.authService = (AuthService) config.getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        String clientKey = email.toLowerCase();
        if (!LoginRateLimiter.isAllowed(clientKey)) {
            req.setAttribute("error", "To much attempts, please try later");
            req.getRequestDispatcher("/WEB-INF/views/login.html").forward(req, resp);
            return;
        }

        try {
            User user = authService.login(email, password);
            req.getSession().setAttribute("USER_ID", user.getId());
            resp.sendRedirect("/WEB-INF/views/home.html"); // временно !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.html").forward(req, resp);
        }
    }
}
