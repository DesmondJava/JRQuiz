package com.jrquiz.question;

import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import org.junit.Test;

import javax.persistence.Query;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class UserAnswerServiceTest extends EntityAndServiceBaseTest {

    @Test
    public void searchesInRepositoryTest(){
    
    //TODO fix test for deploy with empty DB
    
        /*em = emf.createEntityManager();

        Query userQuery = em.createQuery("SELECT u FROM User u ORDER BY u.id");
        List<User> users = userQuery.getResultList();
        assertTrue("No data in table User found", users.size() > 0);
        User user = users.get(0);
        Query testQuery = em.createQuery("SELECT t FROM Quiz t ORDER BY t.id");
        List<Quiz> tests = testQuery.getResultList();
        assertTrue("No data in table Questions found", tests.size() > 0);
        Quiz test = tests.get(0);

        userAnswerService.findByUser(user);
        userAnswerService.findByUserAndAnswerIsNullOrderByQuestionIndex(user);
        userAnswerService.findByUserAndQuiz(user, test);
        userAnswerService.findByUserAndQuizAndAnswerIsNullOrderByQuestionIndex(user, test);*/
    }
}
