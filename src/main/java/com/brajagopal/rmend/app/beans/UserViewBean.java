package com.brajagopal.rmend.app.beans;

import com.google.gson.*;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

/**
 * @author <bxr4261>
 */
public class UserViewBean {

    public static final String COMPOSITE_KEY_SEPARATOR = ":";

    private Long uid;
    private Long docNum;
    private DateTime updateTS;

    private UserViewBean(Long _uid, Long _docNum) {
        this.uid = _uid;
        this.docNum = _docNum;
        updateTS = DateTime.now();
    }

    public static UserViewBean create(Long uid, Long docNumber) {
        return new UserViewBean(uid, docNumber);
    }

    @Override
    public String toString() {
        return "UserViewBean{" +
                "uid=" + uid +
                ", docNum=" + docNum +
                ", updateTS=" + updateTS +
                '}';
    }

    public Long getUid() {
        return uid;
    }

    public Long getDocNum() {
        return docNum;
    }

    public DateTime getUpdateTS() {
        return updateTS;
    }

    public static class UserViewBeanSerDe implements JsonSerializer<UserViewBean>, JsonDeserializer<UserViewBean> {

        @Override
        public JsonElement serialize(UserViewBean userViewBean, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject root = new JsonObject();
            root.addProperty("uid", userViewBean.getUid());
            root.addProperty("docNum", userViewBean.getDocNum());
            root.addProperty("updateTS", userViewBean.getUpdateTS().toString("YYYY-MM-dd HH:mm:ss"));
            return root;
        }

        @Override
        public UserViewBean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject root = jsonElement.getAsJsonObject();
            final Long uid = root.get("uid").getAsLong();
            final Long docNum = root.get("docNum").getAsLong();

            return new UserViewBean(uid, docNum);
        }
    }

    public String getCompositeKey() {
        return uid + COMPOSITE_KEY_SEPARATOR + docNum;
    }
}
