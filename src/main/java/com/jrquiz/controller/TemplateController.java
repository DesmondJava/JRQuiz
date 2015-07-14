package com.jrquiz.controller;

import org.springframework.ui.ModelMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class TemplateController {
    void addHeaderAttributes(ModelMap model)
    {
        final Map<String, String> ADMIN_URLS = new LinkedHashMap<>();
        ADMIN_URLS.put("home.generalAdministrating", "/admin");
        ADMIN_URLS.put("home.usersBtnHeader", UsersController.PATH);

        final Map<String, String> MODERATION_URLS = new LinkedHashMap<>();
        MODERATION_URLS.put("home.questionsBtnHeader", QuestionController.PATH);
        MODERATION_URLS.put("home.addQuestionBtnHeader", QuestionController.PATH + "/add");
        MODERATION_URLS.put("home.tagsBtnHeader", TagController.PATH);

        model.addAttribute("homeURL", "/");
        model.addAttribute("testsURL", "/user");
        model.addAttribute("adminURL", "/admin");
        model.addAttribute("adminURLs", ADMIN_URLS);
        model.addAttribute("moderationURLs", MODERATION_URLS);
        model.addAttribute("statisticsURL", "/statistics");
        model.addAttribute("infoURL", "/info");
    }
}
