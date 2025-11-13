package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import model.Test;
import model.User;
import org.hibernate.SessionFactory;
import repository.TestRepository;
import repository.UserRepository;
import service.AuthService;
import service.TestService;

import java.util.List;

@WebServlet(urlPatterns = "/_dev", loadOnStartup = 1)
public class DevRunnerServlet extends HttpServlet {

    private SessionFactory sf;
    private TestService testService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        sf = (SessionFactory) config.getServletContext().getAttribute("sessionFactory");
        if (sf == null) throw new ServletException("SessionFactory not found");

        testService = (TestService) config.getServletContext().getAttribute("testService");

        List<Test> all = testService.getTests();
        System.out.println(all);
        System.out.println("CHECK");

        AuthService authService = new AuthService(new UserRepository(sf));
        authService.registerAdmin("admin@admin.ru", "adminADMIN1", "admin");





    }
}
