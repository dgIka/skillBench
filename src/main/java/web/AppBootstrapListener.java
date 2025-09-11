package web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@WebListener
public class AppBootstrapListener implements ServletContextListener {

    private SessionFactory sessionFactory;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        servletContext.setAttribute("templateEngine", engine);
        servletContext.setAttribute("jakartaApp", application);
        try {
            Configuration cfg = new Configuration();

            // cfg.addAnnotatedClass(User.class);
            //SessionFactory sf = (SessionFactory) getServletContext().getAttribute("sessionFactory"); это для сервлета

            sessionFactory = cfg.buildSessionFactory();
            servletContext.setAttribute("sessionFactory", sessionFactory);

            System.out.println("Hibernate SessionFactory initialized");

            try (Session session = sessionFactory.openSession()) {
                session.createNativeQuery("SELECT 1").getSingleResult();
                System.out.println("DB OK");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации Hibernate", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("Hibernate SessionFactory closed");
        }
    }
}
