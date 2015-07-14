package com.jrquiz.controller;


import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import com.jrquiz.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.spec.PSSParameterSpec;
import java.util.Set;

@Controller

@RequestMapping(value = TagController.PATH)
public class TagController extends TemplateController{

    @Autowired
    TagService tagService;

    protected final static String PATH = "/moderation/tags";


    @RequestMapping(method = RequestMethod.GET)
    public String showTags(ModelMap model) {
        addHeaderAttributes(model);
        model.addAttribute("tag", new Tag());
        model.addAttribute("allTags", tagService.getAllTags());
        return PATH + "/tags";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addTag(@Valid Tag tag, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return PATH + "/tags";
        }
        tagService.addTag(tag);
        return "redirect:" + PATH;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String eTag(@RequestParam(value = "tid") Tag tag,
                       @RequestParam(value = "tagName") String name){
        tag.setTagName(name);
        tagService.updateTag(tag);

        return "redirect:" + PATH;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String ediTag(@RequestParam(value = "id") Tag tag, Model model) {
        model.addAttribute("tagToEdit", tag);
        return PATH + "/editTag";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteTag(@RequestParam(value = "id") Tag tag)
    {
        if(tag.getQuestions().size() == 0)
        tagService.removeTag(tag.getId());
        return "redirect:" + PATH;
    }
}
