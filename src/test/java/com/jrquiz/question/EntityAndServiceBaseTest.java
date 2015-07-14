package com.jrquiz.question;

import com.jrquiz.config.DataConfigForTest;
import com.jrquiz.service.*;
import com.jrquiz.service.sample.SampleDataGenerator;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataConfigForTest.class)
@WebAppConfiguration
@Ignore
public class EntityAndServiceBaseTest {
    @Resource
    protected EntityManagerFactory emf;

    protected EntityManager em;

    @Resource
    protected QuestionService questionService;

    @Resource
    protected TagService tagService;

    @Resource
    protected UserService userService;

    @Resource
    protected QuizService quizService;

    @Resource
    protected AnswerService answerService;
    @Resource
    protected UserAnswerService userAnswerService;
    @Resource SampleDataGenerator sampleDataGenerator;

    public void generateSampleData()
    {
        sampleDataGenerator.generateSampleData();
    }

}
