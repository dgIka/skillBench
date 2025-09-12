package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Role;
import model.User;
import repository.UserRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserRepository userRepository = (UserRepository) getServletContext().getAttribute("userRepository");

        response.setContentType("text/plain; charset=UTF-8");
        System.out.println("Test servlet is called!");
        Optional<User> userOptional = userRepository.findByUserEmail("test@mail.com");
        System.out.println("Searching result: " + userOptional);
        PrintWriter out = response.getWriter();
        if (userOptional.isPresent()) {
            User u = userOptional.get();
            out.println(u.getId() + " " + u.getEmail());
        } else {
            out.println("not found");
        }
    }
}
