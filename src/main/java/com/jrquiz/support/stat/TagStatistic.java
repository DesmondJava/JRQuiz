package com.jrquiz.support.stat;

import com.jrquiz.entity.Tag;

public class TagStatistic {
    private Tag tag;
    private long count;
    private long countOfQuestions;

    public TagStatistic(Tag tag, long count) {
        this.tag = tag;
        this.count = count;
        this.countOfQuestions = tag.getQuestions().size();
    }
    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
    public int percentageOfPassingByTag()
    {
        return (int) (((double)count/(double)countOfQuestions)*100);
    }
}
