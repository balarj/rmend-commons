package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class TopicBean extends BaseContent {

    private double score;

    public TopicBean() {
        this(ContentType.TOPICS);
    }

    private TopicBean(ContentType _contentType) {
        super(_contentType);
    }

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.score = MapUtils.getDoubleValue(_value, "score", 0.0);
        this.name = MapUtils.getString(_value, "name", null);
        this.name = ((name.length() > 50)?StringUtils.substringBeforeLast(StringUtils.left(name, 50), "_"):this.name);
    }

    @Override
    public BaseContent getInstance() {
        return new TopicBean();
    }

    @Override
    public String toString() {
        return "TopicBean {" +
                "contentType=" + getContentType() +
                ", forEndUserDisplay=" + isForEndUserDisplay() +
                ", name='" + getName() + '\'' +
                ", score=" + getScore() +
                '}';
    }

    public double getScore() {
        return score;
    }

    @Override
    public String getType() {
        throw new UnsupportedOperationException("getType() not supported by "+getClass());
    }
}
