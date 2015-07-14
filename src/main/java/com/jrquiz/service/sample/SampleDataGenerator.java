package com.jrquiz.service.sample;

import java.sql.Timestamp;
import java.util.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.jrquiz.entity.*;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.service.*;
import org.springframework.stereotype.Service;

import com.jrquiz.entity.enums.QuestionType;

@Service
public class SampleDataGenerator {
	@Inject
	protected EntityManagerFactory emf;
	protected EntityManager em;

	@Inject
	protected QuestionService questionService;

	@Inject
	protected TagService tagService;

	@Inject
	protected UserService userService;

	@Inject
	protected QuizService quizService;

	@Inject
	protected AnswerService answerService;

    @Inject
    protected UserAnswerService userAnswerService;

	public void generateSampleData() {
		em = emf.createEntityManager();

		// create 5 users
		int sizeOfUsers = 5;
		String[] userNames = { "Valera", "Andrey", "Zhora", "Dima", "Petya" };
		User[] users = new User[sizeOfUsers];
		for (int i = 0; i < sizeOfUsers; i++) {
			User user = new User();
			user.setUserName(userNames[i]);
			user.setEmail(userNames[i]);
			user.setPassword("");
			users[i] = userService.addUser(user);
		}

		// create 5 tags
		int sizeOfTags = 6;
		String[] tagName = { "Base", "Arrays", "Condition", "Serialization", "Variables", "Inheritance" };
		HashSet<Tag> tags = new HashSet<>();
		for (int i = 0; i < sizeOfTags; i++) {
			Tag tag = new Tag();
			tag.setTagName(tagName[i]);
			tagService.addTag(tag);
			tags.add(tagService.addTag(tag));
		}

		// create Questions
		// int sizeOfQuestions = 5;
		QuestionType[] questionTypes = QuestionType.values();
		// String questionTitle = "Question Title #";
		// String questionText = "Question Text #";
		// String answerText = "answer #";

		int sizeOfQuestions = 16;
		String[] questionTitle = { "Выберите верное понятие программы на Java", "Вывод привет как ты \"Hello World\"", "Выберите верное", "Подберите соответсвие понятию класс", "Подберите соответствие", "Получение последнего элемента массива",
				"Индексация массива", "Определите значение переменной в массиве", "Дайте верное понятие массива", "Выберите верное условие", "Выберите верное условие", "Запрет сериализации", "Подберите соответсвующее", "Определите результат",
				"Подберите соответствующее", "Определите верные варианты наследования" };
		String[] questionText = { "Из чего состоит программа на Java?", "Какой из вариантов ответов выведет на экран \"Hello World\"?", "Какой пакет импортируется по умолчанию в любую java программу?", "Класс это:",
				"Выберите соответствующий тип данных для 'i'", "По какому индексу можно получить последний элемент из массива arr?", "Первый индекс массива это:", "Дан массив : int [ ] ar = {1,2,3,4,5}; Чему равно значение ar[4]?",
				"Массив хранит в себе:", "Какое из нижеперечисленных условий будет истинным для \"х\" меньше 3, а \"y\" больше или равному 4?",
				"Какое из нижеперечисленных условий будет истинным для \"x\" больше 8 или меньше 1 и \"y\" меньше или равно 12", "Как можно запретить сериализацию переменной класса?", "Выберите соответствуйющий тип данных для : isCorrect",
				"Резальтатом следующего кода будет : int a = 3.5;", "Выберите соответствующий тип данных для числа 5.5", "A и E - классы. B и D - интерфейсы. С- абстрактный класс. Выберите верное" };
		String[] answerText = { "Программа состоит из набора методов с расширением java и одного файла", "Прогрмма состоит только из классов и методов которые хранятся в оперативной памяти",
				"Программа состоит из набора файлов с расширением java и в каждом файле написан код одного класса", "Программа состоит из набора классов с расширением java и в каждом файле написан код одного метода",
				"Sysytem.out.println(\"Hello World\");", "system.out.println(\"Hello World\");", "System.out.println(\"Hello World\");", "system.Out.println(\"Hello World\");", "java.awt.font", "java.awt", "java.lang", "java.io",
				"Выполняемая часть кода", "Абстрактное определение для объекта", "Объект", "Переменная", "int", "Integer", "char", "String", "0", "1", "arr.length - 1", "arr.length", "Размер самого массива", "1", "0", "Задается самим программистом",
				"3", "2", "5", "4", "Одинаковые значения одного типа", "Одинакоые значения разных типов", "Разные значения одного типа", "Разные значения разных типов", "if ((x < 3) && (y > 4))", "if (x < 3 y >= 4)", "if ((x < 3) || (y > = 4))",
				"if ((x < 3) || (y < = 4))", "if ((x > 8) || ((x < 1) && (y <= 12)))", "if ((x > 8) || ((x < 1) && (y < 12)))", "if (((x > 8) || (x < 1)) && (y <= 12))", "if ((x < 8) || (x < 1) && (y <= 12))", "Модификатором private",
				"Модификатором volatile", "Модификатором transient", "Этого сделать нельзя", "String", "Integer", "Boolean", "Double", "3.5", "4", "3", "Ошибка выполнения программы", "String", "Integer", "Double", "Float",
				"class F implements B,C{ }", "class F extends A,E{ }", "class F implements B{ }", "class F extends E{ }" };

		for (int i = 0; i < sizeOfQuestions; i++) {

			Question question = new Question();
			QuestionType questionType = questionTypes[0];
			if (i == 14 || i == 15)
				questionType = questionTypes[1];

			HashSet<Answer> answers = new HashSet<>();
			if (questionType.equals(QuestionType.MULTIANSWER)) {
				for (int j = 0; j < 4; j++) {
					Answer answer = new Answer();
					answer.setAnswerText(answerText[i * 4 + j]);
					Boolean isCorrect = ((j == 2) || (j == 3));
					answer.setIsCorrect(isCorrect);
					answer.setQuestion(question);
					answers.add(answer);
				}
			} else if (questionType.equals(QuestionType.SINGLEANSWER)) {
				for (int j = 0; j < 4; j++) {
					Answer answer = new Answer();
					answer.setAnswerText(answerText[i * 4 + j]);
					Boolean isCorrect = (j == 2);
					answer.setIsCorrect(isCorrect);
					answer.setQuestion(question);
					answers.add(answer);
				}
			} else {
				Answer answer = new Answer();
				answer.setAnswerText("There are no answers for question" + i);
				answer.setIsCorrect(true);
				answer.setQuestion(question);
				answers.add(answer);
			}

			question.setQuestionType(questionType);
			question.setTitle(questionTitle[i]);
			question.setText(questionText[i]);
			question.setCreatedBy(users[i % sizeOfUsers]);
			question.setAnswers(answers);
			question.setTags(tags);
            question.setComplexity(((byte)(Math.random()*6)));
			questionService.addQuestion(question);
		}

		// create Test
		Quiz quiz = new Quiz();
		quiz.setMode("mock");
		quiz.setUser(getUser());
		quiz.setCreated(new Timestamp(new Date().getTime()));
		quiz.setTags(tags);
		quiz.setQuestions(new HashSet<>());
		quiz.getQuestions().addAll(questionService.getAllQuestions());
		quizService.addQuiz(quiz);

	}

    public void generateUncompletedTest(){
        UserAnswer ua = new UserAnswer();


        User user = userService.getUser(1l);

        Question question4 = questionService.getQuestion(4l);
        Set<Question> questions = new HashSet<>();
        questions.add(question4);

        Tag tag11 = tagService.getTag(11l);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag11);


        Quiz quiz106 = new Quiz();
        quiz106.setUser(user);
        quiz106.setMode("mock");
        quiz106.setCreated(new Timestamp(new Date().getTime()));
        quiz106.setTags(tags);
        quiz106.setQuestions(questions);
        quizService.addQuiz(quiz106);

        ua.setUser(user);
        ua.setQuiz(quiz106);
        ua.setQuestion(question4);
        userAnswerService.addUserAnswer(ua);
    }
    private User getUser() //Временная заглушка
    {
        User  user = userService.getUser(1L);
        return user;
    }

}
