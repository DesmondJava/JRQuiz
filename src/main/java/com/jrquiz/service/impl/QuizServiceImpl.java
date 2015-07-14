package com.jrquiz.service.impl;

import com.jrquiz.entity.*;
import com.jrquiz.entity.enums.QuestionType;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.exceptions.AccessDeniedException;
import com.jrquiz.exceptions.NoSuitableQuestionsException;
import com.jrquiz.exceptions.RemoveQuizException;
import com.jrquiz.repository.QuizRepository;
import com.jrquiz.service.AnswerService;
import com.jrquiz.service.TagService;
import com.jrquiz.service.QuizService;
import com.jrquiz.service.UserAnswerService;
import com.jrquiz.service.quizvalidation.QuestionValidator;
import com.jrquiz.support.quiz.AnswersForQuiz;
import com.jrquiz.support.quiz.QuestionsForQuiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Resource
    private TagService tagService;

    @Resource
    private UserAnswerService userAnswerService;

    @Resource
    private AnswerService answerService;

    @Autowired
    private QuestionValidator questionValidator;

    @Override
    @Cacheable(value = "quizzes")
    public Quiz getQuiz(Long quizID) {
        return quizRepository.findOne(quizID);
    }

    @Override
    @Cacheable(value = "quizQuestions")
    public Set<Question> getQuestions(Long quizID) {
        return quizRepository.findOne(quizID).getQuestions();
    }

    @Override
    @CachePut(value = "quizzes", key = "#quiz.id")
    public Quiz addQuiz(Quiz quiz) {
        return quizRepository.saveAndFlush(quiz);
    }

    @Override
    public Set<Quiz> findQuizzesByTag(Tag tag) {
        return tag.getQuizzes();
    }

    @Override
    public Set<Quiz> findQuizzesByUser(User user) {
        return quizRepository.findByUser(user);
    }


    public Set<Quiz> findUncompletedQuizzesByUser(User user) {
        return quizRepository.findByUserAndScoreLessThan(user, 0);
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    @Caching(put = {
            @CachePut(value = "quizzes", key = "#quiz.id")},
            evict = {@CacheEvict(value = "quizQuestions", key = "#quiz.id")})
    public Quiz updateQuiz(Quiz quiz) {
        return quizRepository.saveAndFlush(quiz);
    }

    @Override
    @Caching(
            evict = {@CacheEvict(value = "quizQuestions", key = "#quizID"),
                @CacheEvict(value = "quizzes", key = "#quizID")})
    public void removeQuiz(Long quizID) throws RemoveQuizException {
        Quiz quizForRemove = quizRepository.findOne(quizID);
        removeQuiz(quizForRemove);

    }

    @Override
    @Caching(
            evict = {@CacheEvict(value = "quizQuestions", key = "#quiz.id"),
                    @CacheEvict(value = "quizzes", key = "#quiz.id")})
    public void removeQuiz(Quiz quiz) throws RemoveQuizException {

        if(quiz.getScore() < 0) {
            userAnswerService.removeUserAnswerForCurrentQuiz(quiz);
            Quiz quizForRemove = quizRepository.findOne(quiz.getId());
            quizRepository.delete(quizForRemove.getId());
        }
        else {
            throw new RemoveQuizException();
        }
    }

    @Override
    public int countFinishedTests(User user) {
        return quizRepository.countByUserAndScoreGreaterThan(user, 0);
    }


    /*Метод ответсвенный за генерацию тестов, в качестве аргументов он получает теги по которым мы ищем вопросы,
    количество вопросов, сложность по который отсеиваются вопросы.*/
    public Quiz createQuiz(User user, Set<Tag> tags, int quantity, byte minComplexity, byte maxComplexity) throws NoSuitableQuestionsException {
        List<Question> questions = new ArrayList<>(tagService.getAllQuestionsByTags(tags));
        Set<Question> answeredQuestions = userAnswerService.getAllAnsweredQuestion(user);

        Iterator<Question> iterator = questions.iterator();

        while (iterator.hasNext()) {
            Question question = iterator.next();
            if (question.getComplexity() < minComplexity
                    || question.getComplexity() > maxComplexity
                    || !question.isActive()
                    || answeredQuestions.contains(question)) {
                iterator.remove();
            }
        }
        Collections.shuffle(questions);

        if (questions.isEmpty())
            throw new NoSuitableQuestionsException("No Suitable question");

        if (quantity > questions.size())
            quantity = questions.size();

        Set<Question> questionForQuiz = new HashSet<>(questions.subList(0, quantity));

        Quiz newQuiz = new Quiz();
        newQuiz.setQuestions(questionForQuiz);
        newQuiz.setTags(tags);
        newQuiz.setUser(user);
        addQuiz(newQuiz);

        return newQuiz;
    }

    public void submitAnswer(QuestionsForQuiz questionsForQuiz, Quiz quiz, User user ,Long... answerIds) {

        Question question = questionsForQuiz.getQuestion();
        ArrayList<UserAnswer> userAnswers = new ArrayList<>();
        ArrayList<Answer> answers = new ArrayList<>();
        for (Long answerId : answerIds) {
            UserAnswer userAnswer = new UserAnswer();
            Answer answer = answerService.getAnswer(answerId);
            answers.add(answer);
            userAnswer.setQuestion(question);
            userAnswer.setAnswer(answer);
            userAnswer.setQuiz(quiz);
            userAnswer.setUser(user);
            userAnswers.add(userAnswer);
        }
        int scoreForAnswer = 0;
        try {
            scoreForAnswer = questionValidator.validateQuestion(question, answers.toArray(new Answer[answers.size()])).getPercentage();
            questionsForQuiz.setScore(scoreForAnswer);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO разобраться с ексепшеном
        }

        for (UserAnswer userAnswer : userAnswers) {
            userAnswer.setScore(scoreForAnswer);
            userAnswerService.addUserAnswer(userAnswer);
        }
    }

    public List<QuestionsForQuiz>initializeQuiz(Quiz quiz, User user) throws AccessDeniedException {
        ArrayList<QuestionsForQuiz> questionsForCurrentQuiz = new ArrayList<>();
            if (!quiz.getUser().equals(user))
                throw new AccessDeniedException("You have not access to this Test");
            if (quiz.getScore() > 0)
                throw new AccessDeniedException("This test was already finished");

            for (Question question : quiz.getQuestions()) {
                QuestionsForQuiz questionForQuiz = new QuestionsForQuiz();
                questionForQuiz.setQuestion(question);
                List<UserAnswer> answersForCurrentQuestion = userAnswerService.findByUserAndQuestionAndQuiz(user, question, quiz);

                if (answersForCurrentQuestion != null && !answersForCurrentQuestion.isEmpty()) {
                    questionForQuiz.setIsAnswered(true);
                    questionForQuiz.setAnswers(answersForCurrentQuestion);
                }
                questionsForCurrentQuiz.add(questionForQuiz);
            }

        return questionsForCurrentQuiz;
    }

    public AnswersForQuiz prepareAnswersForQuiz(QuestionsForQuiz questionsForQuiz, User user, Quiz quiz) {
        AnswersForQuiz answerForQuiz = new AnswersForQuiz();
        answerForQuiz.setQuestionType(questionsForQuiz.getQuestion().getQuestionType());
        answerForQuiz.setAnswers(questionsForQuiz.getQuestion().getAnswers());

        List<UserAnswer> answersForCurrentQuestion = userAnswerService.findByUserAndQuestionAndQuiz(user, questionsForQuiz.getQuestion(), quiz);

        if (answerForQuiz.getQuestionType() == QuestionType.MULTIANSWER) {
            Long[] savedAnswers = new Long[answersForCurrentQuestion.size()];

            int i = 0;
            for (UserAnswer userAnswer : answersForCurrentQuestion) {
                savedAnswers[i++] = userAnswer.getAnswer().getId();
            }
            answerForQuiz.setCheckBoxValues(savedAnswers);
        } else if (answerForQuiz.getQuestionType() == QuestionType.SINGLEANSWER) {
            for (UserAnswer userAnswer : answersForCurrentQuestion) {
                Long savedAnswer;
                savedAnswer = userAnswer.getAnswer().getId();
                answerForQuiz.setRadioAnswerValue(savedAnswer);
            }
        }
        return answerForQuiz;
    }

}
