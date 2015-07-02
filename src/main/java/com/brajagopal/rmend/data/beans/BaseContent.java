package com.brajagopal.rmend.data.beans;

import org.apache.commons.lang3.StringUtils;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public abstract class BaseContent {
    public static final String KEY_TYPEGROUP = "_typeGroup";

    protected final ContentType contentType;
    protected boolean forEndUserDisplay;
    protected String name;
    protected String type;

    protected BaseContent(ContentType _contentType) {
        this.contentType = _contentType;
    }

    public static Class<? extends BaseContent> find(Map<String, ? extends Object> _value) {
        if (_value.containsKey(KEY_TYPEGROUP)) {
            ContentType contentType = ContentType.getInstance((String) _value.get(KEY_TYPEGROUP));
            if (contentType != null) {
                return contentType.getClassInstance();
            }
        }
        else if (_value.containsKey("contentType")) {
            ContentType contentType = null;
            try {
                contentType = ContentType.valueOf((String) _value.get("contentType"));
            } catch (IllegalArgumentException e) {}
            if (contentType != null) {
                return contentType.getClassInstance();
            }
        }
        else if (_value.keySet().containsAll(Arrays.asList("info", "meta"))) { // document information
            return ContentType.DOCUMENT_INFO.getClassInstance();
        }
        return null;
    }

    public abstract void process(Map<String, ? extends Object> _value);
    public abstract BaseContent getInstance();
    public abstract double getScore();

    public ContentType getContentType() {
        return contentType;
    }

    public Boolean isForEndUserDisplay() {
        return forEndUserDisplay;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        String retVal = StringUtils.replace(name.toLowerCase(), " ", "_");
        return retVal;
    }

    public static enum ContentType {
        TOPICS("topics", TopicBean.class),
        SOCIAL_TAGS("socialTag", SocialTagBean.class),
        ENTITIES("entities", EntitiesBean.class),
        /*RELATIONS("relations", RelationsBean.class),*/
        DOCUMENT_INFO("document", DocumentBean.class),
        DISCARDED(StringUtils.EMPTY, null);

        private String name;
        private Class<? extends BaseContent> classInstance;

        private ContentType(String _typeGroup, Class<? extends BaseContent> _classInstance) {
            this.name = _typeGroup;
            this.classInstance = _classInstance;

        }

        public static ContentType getInstance(String _typeGroup) {
            for (ContentType value : values()) {
                if (value.getTypeGroup().equals(_typeGroup)) {
                    return value;
                }
            }
            return ContentType.DISCARDED;
        }

        public Class<? extends BaseContent> getClassInstance() {
            return classInstance;
        }

        public String getTypeGroup() {
            return name;
        }
    }

    public static final BaseContent getChildInstance(Map<String, ? extends Object> entityValue)
            throws IllegalAccessException, InstantiationException, InvalidClassException {

        Class<? extends BaseContent> content = BaseContent.find(entityValue);
        if (content != null) {
            BaseContent beanValue = content.newInstance().getInstance();
            beanValue.process(entityValue);
            return beanValue;
        }
        else {
            throw new InvalidClassException("Skipping processing for entity: " + entityValue.get(BaseContent.KEY_TYPEGROUP));
        }
    }

    public static final Comparator<? extends BaseContent> CONTENT_COMPARATOR = new Comparator<BaseContent>() {
        @Override
        public int compare(BaseContent o1, BaseContent o2) {
            if (o1.getScore() > o2.getScore()) {
                return -1;
            } else if (o1.getScore() < o2.getScore()) {
                return 1;
            }

            return  (o1.getName().compareTo(o2.getName()));
        }
    };
}
