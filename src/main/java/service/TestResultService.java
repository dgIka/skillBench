package service;

import dto.auth.AttemptDTO;
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
            User u = sf.getCurrentSession().byId(User.class).getReference(userId);
            Test t = sf.getCurrentSession().byId(Test.class).getReference(attemptDTO.getTestId());
            testResult.setUser(u);
            testResult.setTest(t);
            testResult.setFalseAnswers(attemptDTO.getFalseCount());
            testResult.setTrueAnswers(attemptDTO.getTrueCount());
            testResultRepository.save(testResult);
        });
    }

}
