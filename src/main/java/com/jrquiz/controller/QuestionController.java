package com.jrquiz.controller;

import com.jrquiz.PageWrapper;
import com.jrquiz.controller.choices.ChosenTags;
import com.jrquiz.entity.Answer;
import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import com.jrquiz.entity.enums.QuestionType;
import com.jrquiz.service.AnswerService;
import com.jrquiz.service.QuestionService;
import com.jrquiz.service.TagService;
import com.jrquiz.service.UserService;
import com.jrquiz.service.sample.SampleDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping(value = QuestionController.PATH)
public class QuestionController extends TemplateController{

    protected static final String PATH = "/moderation/questions";
    private final int[] COMPLEXITY = new int[]{1, 2, 3 , 4 , 5};
    @Resource
    protected UserService userService;
    @Resource
    protected SampleDataGenerator sampleDataGenerator;
    @Autowired
    QuestionService questionService;
    @Autowired
    TagService tagService;
    @Autowired
    AnswerService answerService;

    @RequestMapping(method = RequestMethod.GET)
    public String chooseQuestion(ModelMap model, Pageable pageable) {
        PageWrapper<Question> page = new PageWrapper<>(questionService.getAllPublishedQuestionsByCreatedDateDesc(pageable), PATH);
        ArrayList<String> allTagsNames = new ArrayList<>();
        for(Tag tag: tagService.getAllTags()){
            allTagsNames.add(tag.getTagName());
        }
        addHeaderAttributes(model);
        model.addAttribute("forTag", null);
        model.addAttribute("allTagsNames", allTagsNames);
        model.addAttribute("page", page);
        model.addAttribute("allQuestions", page.getContent());
        return PATH + "/requestquestion";
    }

    @RequestMapping(value = "/sort", method = RequestMethod.GET)
    public String sortTable(@RequestParam(value = "sortBy", required = false) String sortBy, ModelMap model, Pageable pageable){
        ArrayList<String> allTagsNames = new ArrayList<>();
        for (Tag tag : tagService.getAllTags()) {
            allTagsNames.add(tag.getTagName());
        }
        PageWrapper<Question> page;
        String sortByColumn = sortBy.split("_")[0];
        byte sortByOrder = Byte.parseByte(sortBy.split("_")[1]);

        switch (sortByColumn) {
            case "id":
                if (sortByOrder == 0) {
                    page = new PageWrapper<>(questionService.getAllQuestionsByIdAsc(pageable), PATH + "/sort?sortBy=id_0");
                } else
                    page = new PageWrapper<>(questionService.getAllQuestionsByIdDesc(pageable), PATH + "/sort?sortBy=id_1");
                break;
            case "title":
                if (sortByOrder == 0) {
                    page = new PageWrapper<>(questionService.getAllQuestionsByTitleAsc(pageable), PATH + "/sort?sortBy=title_0");
                } else
                    page = new PageWrapper<>(questionService.getAllQuestionsByTitleDesc(pageable), PATH + "/sort?sortBy=title_1");
                break;
            case "createdBy":
                if (sortByOrder == 0) {
                    page = new PageWrapper<>(questionService.getAllQuestionsByCreatedByAsc(pageable), PATH + "/sort?sortBy=createdBy_0");
                } else
                    page = new PageWrapper<>(questionService.getAllQuestionsByCreatedByDesc(pageable), PATH + "/sort?sortBy=createdBy_1");
                break;
            case "date":
                if (sortByOrder == 0) {
                    page = new PageWrapper<>(questionService.getAllPublishedQuestionsByCreatedDateAsc(pageable), PATH + "/sort?sortBy=date_0");
                } else
                    page = new PageWrapper<>(questionService.getAllPublishedQuestionsByCreatedDateDesc(pageable), PATH + "/sort?sortBy=date_1");
                break;
            case "complexity":
                if (sortByOrder == 0) {
                    page = new PageWrapper<>(questionService.getAllQuestionsByComplexityAsc(pageable), PATH + "/sort?sortBy=complexity_0");
                } else
                    page = new PageWrapper<>(questionService.getAllQuestionsByComplexityDesc(pageable), PATH + "/sort?sortBy=complexity_1" );
                break;
            default: page = new PageWrapper<>(questionService.getAllPublishedQuestionsByCreatedDateAsc(pageable), PATH);
        }
        addHeaderAttributes(model);
        model.addAttribute("forTag", null);
        model.addAttribute("allTagsNames", allTagsNames);
        model.addAttribute("page", page);
        model.addAttribute("allQuestions", page.getContent());
        return PATH + "/requestquestion";
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public String findQuestion(@RequestParam String searchquestion, ModelMap model) {
        addHeaderAttributes(model);
        ArrayList<String> allTagsNames = new ArrayList<>();
        for(Tag tag: tagService.getAllTags()){
            allTagsNames.add(tag.getTagName());
        }
        model.addAttribute("allTagsNames", allTagsNames);
        model.addAttribute("forTag", null);
        model.addAttribute("page", null);
        model.addAttribute("allQuestions", questionService.findQuestions(searchquestion));
        return PATH + "/requestquestion";
    }

    @RequestMapping(value = "/tag", method = RequestMethod.GET)
    public String detailInfo(@RequestParam(value = "id") Tag tag, ModelMap model) {
        addHeaderAttributes(model);
        model.addAttribute("forTag", tag);
        model.addAttribute("page", null);
        model.addAttribute("allQuestions", tag.getQuestions());
        return PATH + "/requestquestion";
    }

    @RequestMapping(value = "/tagSearch",method = RequestMethod.POST)
    public String findTag(@RequestParam String searchtag, ModelMap model) {
        ArrayList<String> allTagsNames = new ArrayList<>();
        for(Tag tag: tagService.getAllTags()){
            allTagsNames.add(tag.getTagName());
        }
        Tag tag = tagService.getTagByName(searchtag);
        if (tag == null){
            model.addAttribute("allQuestions", null);
        } else
            model.addAttribute("allQuestions", tag.getQuestions());
            model.addAttribute("forTag", null);
            model.addAttribute("allTagsNames", allTagsNames);
            model.addAttribute("page", null);
            addHeaderAttributes(model);
        return PATH + "/requestquestion";
    }

    @Transactional
    @RequestMapping(value="/addtags", method=RequestMethod.POST)
    public String addTags(@ModelAttribute(value="tagnames") ChosenTags chosenTags,
                          @RequestParam (value = "id") Long questionId){            //выводим запрашиваемый тестовый вопрос

        if (chosenTags.getCheckedItems() == null)
            return "redirect:" + PATH + "/update?id=" + questionId;

        Question question = questionService.getQuestion(questionId);
        Set<Tag> tags = question.getTags();

        for (String s : chosenTags.getCheckedItems())                               //переделаю под java 8 позже.
            tags.add(tagService.getTagByName(s));

        question.setTags(tags);
        questionService.updateQuestion(question);

        return "redirect:" + PATH + "/update?id=" + questionId;
    }

    @Transactional
    @RequestMapping(value="/deletetags", method=RequestMethod.POST)
    public String deleteTags(@ModelAttribute(value="tagnames") ChosenTags chosenTags,
                             @RequestParam (value = "id") Long questionId){ //выводим запрашиваемый тестовый вопрос
        if (chosenTags.getCheckedItems() == null)
            return "redirect:" + PATH + "/update?id=" + questionId;

        Question question = questionService.getQuestion(questionId);
        Set<Tag> tags = question.getTags();

        Iterator qTags = tags.iterator();

        while (qTags.hasNext()){
            String tagName = ((Tag)qTags.next()).getTagName();
            for (String s : chosenTags.getCheckedItems())
                if (tagName.equals(s))
                    qTags.remove();
        }

        question.setTags(tags);
        questionService.updateQuestion(question);

        return "redirect:" + PATH + "/update?id=" + questionId;
    }

    @RequestMapping(value = "/disable")
    public String deleteQuestion(@RequestParam(value = "id") Long questionId){

        Question question = questionService.getQuestion(questionId);
        if (question.isActive() == null) question.setActive(true);
        question.setActive(!question.isActive());
        questionService.updateQuestion(question);

        return "redirect:" + PATH;
    }

    @RequestMapping(value = "/add")
    public String addQuestionForm (ModelMap model){
        addHeaderAttributes(model);
        model.addAttribute("question", new Question());
        model.addAttribute("complexity", COMPLEXITY);
        return PATH + "/addQuestion";
    }

    @RequestMapping(value = "/addquestion", method = RequestMethod.POST)
    public String addQuestion(@ModelAttribute(value = "question") Question question,
                                @RequestParam (value="type", defaultValue = "1") short type){
        switch (type) {
            case 2: question.setQuestionType(QuestionType.SINGLEANSWER);
                    break;
            case 3: question.setQuestionType(QuestionType.TYPEANSWER);
                    break;
            default: question.setQuestionType(QuestionType.MULTIANSWER);
        }
        question.setCreatedBy(userService.getUser(1l));
        questionService.addQuestion(question);
        Long questionId = question.getId();

        return "redirect:" + PATH + "/update?id=" + questionId;
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String updateQuestionForm(@RequestParam(value="id") Long questionId, ModelMap model){

        Question question;
        try {
            question = questionService.getQuestion(questionId);
            question.getId();
        }catch (Exception e){
            return "error/general";
        }
        List<Answer> answers = new ArrayList<Answer>(questionService.getAnswers(question.getId()));

        Boolean isCorrectExists = false;
        if (question.getQuestionType().equals(QuestionType.SINGLEANSWER)||
                question.getQuestionType().equals(QuestionType.TYPEANSWER)){
            for (Answer answer : answers)
                if (answer.getIsCorrect())
                    isCorrectExists = true;
        }

        ChosenTags chosenTags = new ChosenTags();
        List<Tag> allTags = tagService.getAllTags();

        for (Tag qTag : question.getTags()) {
            Iterator iterator = allTags.iterator();
            while (iterator.hasNext())
                if (qTag.getTagName().equals(((Tag) iterator.next()).getTagName()))
                    iterator.remove();
        }

        addHeaderAttributes(model);
        model.addAttribute("question", question);
        model.addAttribute("complexity", COMPLEXITY);               //сложность - величины постоянные
        model.addAttribute("answers", answers);
        model.addAttribute("newanswer", new Answer());              //для создания нового ответа
        model.addAttribute("iscorrectexists", isCorrectExists);     //нужно для сингл вопроса
        model.addAttribute("allTags", allTags);                     //все тэги, за исключением существующих
        model.addAttribute("tagnames", chosenTags);                 //теги которые мы выберем
        return PATH + "/updateQuestion";
    }

    @RequestMapping(value = "/updatequestion", method = RequestMethod.POST)
    public String updateQuestion(@ModelAttribute(value = "question") Question question,
                                 @RequestParam (value = "type", defaultValue = "1") short type,
                                 @RequestParam (value = "id") Long questionId){
        Question updatedQuestion = questionService.getQuestion(questionId);
        updatedQuestion.setTitle(question.getTitle());
        updatedQuestion.setText(question.getText());
        updatedQuestion.setComplexity(question.getComplexity());
        updatedQuestion.setTimeToSolve(question.getTimeToSolve());
        updatedQuestion.setModifiedDate(new Date());
        switch (type) {
            case 2: updatedQuestion.setQuestionType(QuestionType.SINGLEANSWER);
                break;
            case 3: updatedQuestion.setQuestionType(QuestionType.TYPEANSWER);
                break;
            default: updatedQuestion.setQuestionType(QuestionType.MULTIANSWER);
        }
        questionService.updateQuestion(updatedQuestion);

        return "redirect:" + PATH + "/update?id=" + questionId;
    }

    @RequestMapping(value = "/addanswer", method = RequestMethod.POST)
    public String addAnswer(@ModelAttribute(value = "newanswer") Answer answer, //Ни в коем случае не менять qid на id - происходит конфликт
                            @RequestParam(value = "qid") Question question) {   //айди вопроса и айди нового ответа
        questionService.addAnswer(answer, question);
        return "redirect:" + PATH + "/update?id=" +  question.getId();
    }

    @RequestMapping(value = "deleteanswer", method = RequestMethod.GET)
    public String deleteAnswer(@RequestParam(value = "id") Answer answer) {

        Long questionId = answer.getQuestion().getId();
        questionService.removeAnswer(answer);
        return "redirect:" + PATH + "/update?id=" + questionId;
    }

    @RequestMapping(value = "/generatesampledata")
    public String generateSampleData (Model model){
        sampleDataGenerator.generateSampleData();
        return "redirect:/hello";
    }
}