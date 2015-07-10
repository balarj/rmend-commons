package com.brajagopal.rmend.app.beans;

import com.google.gson.*;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @author <bxr4261>
 */
@SuppressWarnings("unused")
public class UserBean {

    private static final long BASE_MODIFIER = (System.currentTimeMillis() - new DateTime("2015-07-07").getMillis());
    private Long uid;
    private String userName;
    private String uuid;
    private String createDate;

    private UserBean(Long _uid, String _userName, String _uuid) {
        this.uid = _uid;
        this.userName = _userName;
        this.uuid = _uuid;
        this.createDate = DateTime.now().toString("YYYY-MM-dd HH:mm:ss");
    }

    private UserBean(Long _uid, String _userName, String _uuid, String _createDate) {
        this.uid = _uid;
        this.userName = _userName;
        this.uuid = _uuid;
        this.createDate = _createDate;
    }

    public Long getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getCreateDate() {
        return createDate;
    }

    public static UserBean create(String userName) {
        return new UserBean((BASE_MODIFIER +  RandomUtils.nextLong(1, 200)), userName, UUIDGenerator.generateUUID());
    }

    public static UserBean load(Long _uid, String _userName, String _uuid, String _createDate) {
        return new UserBean(_uid, _userName, _uuid, _createDate);
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uid=" + uid +
                ", userName='" + userName + '\'' +
                ", uuid='" + uuid + '\'' +
                ", createDate=" + createDate +
                '}';
    }

    private static class UUIDGenerator {
        /**
         * Build a UUID.
         */
        public static String generateUUID() {
            return UUID.randomUUID().toString();
        }
    }

    public static class UserBeanSerDe implements JsonSerializer<UserBean>, JsonDeserializer<UserBean> {

        @Override
        public JsonElement serialize(UserBean userBean, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject root = new JsonObject();
            root.addProperty("uid", userBean.getUid());
            root.addProperty("userName", userBean.userName);
            root.addProperty("uuid", userBean.uuid);
            root.addProperty("createDate", userBean.createDate);
            return root;
        }

        @Override
        public UserBean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject root = jsonElement.getAsJsonObject();
            final Long uid = root.get("uid").getAsLong();
            final String userName = root.get("userName").getAsString();
            final String uuid = root.get("uuid").getAsString();
            final String createDate = root.get("createDate").getAsString();

            return new UserBean(uid, userName, uuid, createDate);
        }
    }
}
