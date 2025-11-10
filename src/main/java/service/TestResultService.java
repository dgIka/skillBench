package service;

import dto.auth.AttemptDTO;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import model.Test;
import model.TestResult;
import model.User;
import org.hibernate.SessionFactory;
import repository.TestRepository;
import repository.TestResultRepository;
import repository.UserRepository;

import java.util.Optional;

@AllArgsConstructor
public class TestResultService {

    private SessionFactory sf;

    private TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;

    public Optional<TestResult> getTestResult(int id) {
        return testResultRepository.findById(id);
    }

    public Integer saveTestResult(TestResult testResult) {
        return testResultRepository.save(testResult);
    }

    public Integer saveTestResult(int userId, AttemptDTO attemptDTO) {
        TestResult testResult = new TestResult();

        return sf.fromTransaction(session ->{
            User u = session.byId(User.class).getReference(userId);
            Test t = session.byId(Test.class).getReference(attemptDTO.getTestId());
            testResult.setUser(u);
            testResult.setTest(t);
            testResult.setFalseAnswers(attemptDTO.getFalseCount());
            testResult.setTrueAnswers(attemptDTO.getTrueCount());
            return testResultRepository.save(testResult);
        });
    }

}
