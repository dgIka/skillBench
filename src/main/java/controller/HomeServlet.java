package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

//шаблон для остальных сервлетов
@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TemplateEngine templateEngine = (TemplateEngine) request.getServletContext().getAttribute("templateEngine");//в каждом сервлете
        JakartaServletWebApplication app = (JakartaServletWebApplication) request.getServletContext().getAttribute("jakartaApp");//в каждом сервлете

        IWebExchange exchange = app.buildExchange(request, response);//в каждом сервлете

        WebContext context = new WebContext(exchange, request.getLocale()); //если передаем данные, создается всегда, использовать не обязательно, в каждом сервлете
        context.setVariable("message", "Привет, "); //сама передача данных

        response.setContentType("text/html;charset=utf-8"); //кодировка передаваемого шаблона, в каждом сервлете
        templateEngine.process("home", context, response.getWriter()); //финальный для передачи странички
    }
}
