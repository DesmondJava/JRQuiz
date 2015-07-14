package com.jrquiz.controller;

import com.jrquiz.entity.Tag;
import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.exceptions.NoSuitableQuestionsException;
import com.jrquiz.service.TagService;
import com.jrquiz.service.QuizService;
import com.jrquiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;


@Controller
public class QuizGeneratorController extends TemplateController{

    @Autowired
    TagService tagService;

    @Autowired
    QuizService quizService;

    @Autowired
    UserService userService;

    private User user;

    @RequestMapping("/quiz")
    public String showAllTags(ModelMap model) {

        addHeaderAttributes(model);
        model.addAttribute("allTags", tagService.getAllTags()); //TODO  Убрать из списка теги без вопросов
        model.addAttribute("generateQuizForm", new GenerateQuizForm());
        return "quiz/quizGenerator";

    }

    @RequestMapping(value = "/quiz", method = RequestMethod.POST)
    public String generateQuiz(@ModelAttribute @Valid GenerateQuizForm generateQuizForm,
                               BindingResult bindingResult, ModelMap model, Principal principal) {
        if (user == null) {
            user = userService.getUser(principal);
        }
        model.addAttribute("allTags", tagService.getAllTags());
        if (bindingResult.hasErrors()) {
            return "quiz/quizGenerator";
        }

        Quiz generatedQuiz = new Quiz();
        Set<Tag> tags = new HashSet<>();
        for (String tagString : generateQuizForm.getCheckedTags()) {
            Long id = Long.parseLong(tagString);
            tags.add(tagService.getTag(id));
        }

        try {
            generatedQuiz = quizService.createQuiz(user, tags, 10, (byte) 0, (byte) 5);
        } catch (NoSuitableQuestionsException e) {
            bindingResult.addError(new FieldError("generateQuizForm", "checkedTags", "No suitable questions"));
            return "quiz/quizGenerator";
        }
        return "redirect:runquiz/" + generatedQuiz.getId();
    }

    //класс для работы с формой
    public static class GenerateQuizForm {
        @Size(min = 1, message = "Select at least one tag")
        public String[] checkedTags;

        public GenerateQuizForm() {
        }

        public String[] getCheckedTags() {
            return checkedTags;
        }

        public void setCheckedTags(String[] checkedTags) {
            this.checkedTags = checkedTags;
        }
    }
}
