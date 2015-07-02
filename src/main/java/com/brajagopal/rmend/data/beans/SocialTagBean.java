package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class SocialTagBean extends BaseContent {

    private int importance;

    public SocialTagBean() {
       this(ContentType.SOCIAL_TAGS);
    }

    private SocialTagBean(ContentType _contentType) {
        super(_contentType);
    }

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.importance = MapUtils.getIntValue(_value, "importance", 0);
        this.name = MapUtils.getString(_value, "name", null);
        this.name = ((name.length() > 50)?StringUtils.substringBeforeLast(StringUtils.left(name, 50), "_"):this.name);
    }

    @Override
    public BaseContent getInstance() {
        return new SocialTagBean();
    }

    @Override
    public double getScore() {
        return getImportance();
    }

    @Override
    public String toString() {
        return "SocialTag {" +
                "contentType=" + getContentType() +
                ", forEndUserDisplay=" + isForEndUserDisplay() +
                ", name='" + getName() + '\'' +
                ", score=" + getScore() +
                '}';
    }

    @Override
    public String getType() {
        throw new UnsupportedOperationException("getType() not supported by "+getClass());
    }

    public int getImportance() {
        return importance;
    }

}
