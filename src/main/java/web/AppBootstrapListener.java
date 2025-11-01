package web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import model.Answer;
import model.Question;
import model.Test;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import repository.UserRepository;
import service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class AppBootstrapListener implements ServletContextListener {
    private ValidatorFactory validatorFactory;
    private SessionFactory sessionFactory;
    private HikariDataSource dataSource;
    private Liquibase liquibase;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        //Liquibase
        Properties properties = new Properties();
        try(InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("hibernate.properties")) {
            if(in == null) {throw new IllegalArgumentException("hibernate.properties not found");}
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Hikari
        HikariConfig hc = new HikariConfig();
        hc.setDriverClassName(properties.getProperty("hibernate.hikari.driverClassName"));
        hc.setJdbcUrl(properties.getProperty("hibernate.hikari.jdbcUrl"));
        hc.setUsername(properties.getProperty("hibernate.hikari.username"));
        hc.setPassword(properties.getProperty("hibernate.hikari.password"));
        hc.setMaximumPoolSize(Integer.parseInt(properties.getProperty("hibernate.hikari.maximumPoolSize", "10")));
        hc.setMinimumIdle(Integer.parseInt(properties.getProperty("hibernate.hikari.minimumIdle", "2")));
        hc.setIdleTimeout(Long.parseLong(properties.getProperty("hibernate.hikari.idleTimeout", "600000")));
        hc.setMaxLifetime(Long.parseLong(properties.getProperty("hibernate.hikari.maxLifetime", "1800000")));
        hc.setConnectionTimeout(Long.parseLong(properties.getProperty("hibernate.hikari.connectionTimeout", "30000")));

        dataSource = new HikariDataSource(hc);

        String masterChangelog = "db/changelog/db.changelog-master.yaml";

        try (var conn = dataSource.getConnection()) {
            liquibase = new Liquibase(masterChangelog
            ,new ClassLoaderResourceAccessor()
            , new JdbcConnection(conn));
            liquibase.update(new Contexts(), new LabelExpression());
            sce.getServletContext().setAttribute("liquibase", liquibase);

        } catch (Exception e) {
            throw new RuntimeException("Liquibase init failed", e);
        }

        //Hibernate Validator
        validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        servletContext.setAttribute("validatorFactory", validatorFactory);
        servletContext.setAttribute("validator", validator);

        //thymeleaf
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        servletContext.setAttribute("templateEngine", engine);
        servletContext.setAttribute("jakartaApp", application);
        try {
            Configuration cfg = new Configuration();
            cfg.addAnnotatedClass(User.class);
            cfg.addAnnotatedClass(Test.class);
            cfg.addAnnotatedClass(Question.class);
            cfg.addAnnotatedClass(Answer.class);

            // cfg.addAnnotatedClass(User.class);
            //SessionFactory sf = (SessionFactory) getServletContext().getAttribute("sessionFactory"); это для сервлета

            sessionFactory = cfg.buildSessionFactory();
            servletContext.setAttribute("sessionFactory", sessionFactory);
            UserRepository userRepository = new UserRepository(sessionFactory);
            AuthService authService = new AuthService(userRepository);
            servletContext.setAttribute("userRepository", userRepository);
            servletContext.setAttribute("authService", authService);

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
        if (dataSource != null) {
            try { dataSource.close(); } catch (Exception ignore) {}
        }

        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("Hibernate SessionFactory closed");
        }

        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }
}
