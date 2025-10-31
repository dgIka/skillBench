package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/tests")
public class TestsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) request.getServletContext().getAttribute("templateEngine");
        JakartaServletWebApplication app = (JakartaServletWebApplication) request.getServletContext().getAttribute("jakartaApp");
        IWebExchange exchange = app.buildExchange(request, response);
        WebContext context = new WebContext(exchange, request.getLocale());

        response.setContentType("text/html;charset=utf-8");
        templateEngine.process("tests", context, response.getWriter());
    }
}
