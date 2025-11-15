package controller;

import dto.test.AnswerDto;
import dto.test.QuestionDto;
import dto.test.TestDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import service.TestService;

import java.io.IOException;
import java.util.*;

@WebServlet("/admin/newtest")
public class NewTestServlet extends HttpServlet {

    private TemplateEngine templateEngine;
    private JakartaServletWebApplication app;
    private Validator validator;
    private TestService testService;

    @Override
    public void init(ServletConfig config) {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
        app = (JakartaServletWebApplication) config.getServletContext().getAttribute("jakartaApp");
        validator = (Validator) config.getServletContext().getAttribute("validator");
        testService = (TestService) config.getServletContext().getAttribute("testService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());

        context.setVariable("themes", testService.getAllThemes());

        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process("newtest", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new WebContext(app.buildExchange(req, resp), req.getLocale());
        resp.setContentType("text/html;charset=UTF-8");


        String name = trim(req.getParameter("name"));
        String themeExisting = trim(req.getParameter("themeExisting"));
        String themeNew = trim(req.getParameter("themeNew"));
        String theme = (themeNew != null && !themeNew.isBlank()) ? themeNew : themeExisting;

        TestDto testDto = new TestDto();
        testDto.setName(name);
        testDto.setTheme(theme);

        List<QuestionDto> questions = new ArrayList<>();
        for (int i = 0; ; i++) {
            String qText = req.getParameter("question[" + i + "].text");
            if (qText == null) break;

            QuestionDto qdto = new QuestionDto();
            qdto.setText(trim(qText));

            List<AnswerDto> answers = new ArrayList<>(4);
            for (int j = 0; j < 4; j++) {
                String aText = req.getParameter("question[" + i + "].answers[" + j + "].text");
                AnswerDto adto = new AnswerDto();
                adto.setText(trim(aText));
                answers.add(adto);
            }
            qdto.setAnswers(answers);

            String idxStr = req.getParameter("question[" + i + "].correctIndex");
            int correctIndex = -1;
            try {
                correctIndex = Integer.parseInt(idxStr);
            } catch (Exception ignored) { }
            qdto.setCorrectIndex(correctIndex);

            questions.add(qdto);
        }

        String action = req.getParameter("action");
        if ("addQuestion".equals(action)) {
            QuestionDto blank = new QuestionDto();
            blank.setText("");
            List<AnswerDto> blankAnswers = new ArrayList<>(4);
            for (int j = 0; j < 4; j++) {
                AnswerDto a = new AnswerDto();
                a.setText("");
                blankAnswers.add(a);
            }
            blank.setAnswers(blankAnswers);
            blank.setCorrectIndex(-1);
            questions.add(blank);

            context.setVariable("themes", testService.getAllThemes());
            context.setVariable("name", name);
            context.setVariable("themeExisting", themeExisting);
            context.setVariable("themeNew", themeNew);
            context.setVariable("questions", questions);
            templateEngine.process("newtest", context, resp.getWriter());
            return;
        }

        Map<String, String> errors = new LinkedHashMap<>();

        mapViolations(errors, validator.validate(testDto), "");

        for (int i = 0; i < questions.size(); i++) {
            QuestionDto q = questions.get(i);
            mapViolations(errors, validator.validate(q), "question[" + i + "].");

            List<AnswerDto> ans = q.getAnswers();
            if (ans != null) {
                for (int j = 0; j < ans.size(); j++) {
                    mapViolations(errors, validator.validate(ans.get(j)), "question[" + i + "].answers[" + j + "].");
                }
            }

            if (ans == null || ans.size() != 4) {
                errors.put("question[" + i + "].answers", "Должно быть ровно 4 ответа");
            }
            if (q.getCorrectIndex() < 0 || q.getCorrectIndex() > 3) {
                errors.put("question[" + i + "].correctIndex", "Выбери один правильный ответ");
            }
        }
        if (questions.isEmpty()) {
            errors.put("questions", "Нужно добавить хотя бы один вопрос");
        }

        if (!errors.isEmpty()) {
            context.setVariable("themes", testService.getAllThemes());
            context.setVariable("errors", errors);
            context.setVariable("name", name);
            context.setVariable("themeExisting", themeExisting);
            context.setVariable("themeNew", themeNew);
            context.setVariable("questions", questions);

            templateEngine.process("newtest", context, resp.getWriter());
            return;
        }

        int testId = testService.createTest(testDto, questions);

        resp.sendRedirect(req.getContextPath() + "/admin");
    }


    private static String trim(String s) {
        return (s == null) ? null : s.trim();
    }

    private static <T> void mapViolations(Map<String, String> errors,
                                          Set<ConstraintViolation<T>> violations,
                                          String prefix) {
        if (violations == null || violations.isEmpty()) return;
        String pfx = (prefix == null) ? "" : prefix;
        for (ConstraintViolation<T> v : violations) {
            String path = (v.getPropertyPath() == null) ? "" : v.getPropertyPath().toString();
            errors.put(pfx + path, v.getMessage());
        }
    }

    private static <T> void mapViolations(Map<String, String> errors,
                                          Set<ConstraintViolation<T>> violations) {
        mapViolations(errors, violations, "");
    }



}
