package com.jrquiz.question;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import org.junit.Test;
import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.*;

public class TagServiceTest extends EntityAndServiceBaseTest{

    private String tagName = "TestTagName";

   @Test
    public void addTagTest()
    {
        Tag tag = new Tag();
        tag.setTagName(tagName );
        Tag addedTag = tagService.addTag(tag);
        Long id = addedTag.getId();
        assertEquals(tagService.getTag(id).getTagName(), tagName);
        tagService.removeTag(id);
    }

    @Test
    public void removeTagTest()
    {
        Tag tag = new Tag();
        tag.setTagName(tagName );
        Tag addedTag = tagService.addTag(tag);
        Long id = addedTag.getId();
        tagService.removeTag(id);

        Tag deletedTag = tagService.getTag(id);
        assertNull(deletedTag);
    }

    @Test
    public void updateTagTest()
    {
        Tag tag = new Tag();
        tag.setTagName(tagName);
        Tag addedTag = tagService.addTag(tag);
        Long id = addedTag.getId();
        Tag tagToUpdate = tagService.getTag(id);
        String newTagName= "NewTagName";
        tagToUpdate.setTagName(newTagName);
        tagService.updateTag(tagToUpdate);

        assertEquals(tagService.getTag(id).getTagName(), newTagName);
        tagService.removeTag(id);
    }

   @Test
    public void getQuestionByNameTest()
    {
        Tag tag = new Tag();
        tag.setTagName(tagName );
        Tag addedTag = tagService.addTag(tag);
        Long id = addedTag.getId();
        Tag gotTag = tagService.getTagByName(tagName);
        assertEquals(gotTag.getId(), id);
        tagService.removeTag(id);
    }
    

    @Test
    public void getQuestionsByTagsTest()
    {
        Tag tag1 = new Tag();
        tag1.setTagName("tag1");
        tagService.addTag(tag1);
        Tag tag2 = new Tag();
        tag2.setTagName("tag2");
        tagService.addTag(tag2);

        HashSet<Tag> tags = new HashSet<>();
        tags.add(tagService.getTagByName("tag1"));
        tags.add(tagService.getTagByName("tag2"));

        HashSet<Question> questions = new HashSet<>();

        for (int i = 0; i < 3; i++) {
            Question question = new Question();
            question.setTitle("TestTitle" + i);
            question.setText("TestText" + i);
            question.setTags(tags);
            questions.add(question);
            questionService.addQuestion(question);
        }

        HashSet<Tag> tagsFromDB = new HashSet<>();
        tagsFromDB.add(tagService.getTagByName("tag1"));
        tagsFromDB.add(tagService.getTagByName("tag2"));

        HashSet<Question> gotQuestionByTag = new HashSet<>(tagService.getAllQuestionsByTags(tagsFromDB));

        for (Question question : questions) {
            assertTrue(gotQuestionByTag.contains(question));
            gotQuestionByTag.remove(question);
        }
        assertTrue(gotQuestionByTag.isEmpty());

        for(Question question: questions)
        {
            questionService.removeQuestion(question.getId());
        }

        for(Tag tag : tagsFromDB)
        {
            tagService.removeTag(tag.getId());
        }
    }

}
