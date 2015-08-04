package com.brajagopal.rmend.app.beans;

import com.brajagopal.rmend.utils.SerDeUtils;
import com.google.common.base.Strings;
import com.google.gson.*;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

/**
 * @author <bxr4261>
 */
public class UserViewBean {

    public static final String COMPOSITE_KEY_SEPARATOR = ":";

    private String uuid;
    private Long uid;
    private Long docNum;
    private Long parentDocNum;
    private DateTime updateTS;

    private String referrer = "UNKNOWN";

    private Boolean isInvalid = false;

    private UserViewBean(Long _uid, Long _docNum) {
        if (_uid == 0 || _docNum == 0) {
            this.isInvalid = true;
        }
        this.uid = _uid;
        this.docNum = _docNum;
        updateTS = DateTime.now();
    }

    private UserViewBean(String _uuid, Long _docNum) {
        if (_docNum == 0 || Strings.isNullOrEmpty(_uuid)) {
            this.isInvalid = true;
        }
        this.uuid = _uuid;
        this.docNum = _docNum;
        updateTS = DateTime.now();
    }

    private UserViewBean(String _uuid, Long _uid, Long _docNum, Long _parentDocNum, String _referrer) {
        if (Strings.isNullOrEmpty(_uuid) && _uid == 0) {
            this.isInvalid = true;
        }
        if (_docNum == 0) {
            this.isInvalid = true;
        }
        this.uuid = _uuid;
        this.uid = _uid;
        this.docNum = _docNum;
        this.parentDocNum = _parentDocNum;
        updateTS = DateTime.now();
        this.referrer = _referrer;
    }

    public static UserViewBean create(Long uid, Long docNumber) {
        return new UserViewBean(uid, docNumber);
    }

    public static UserViewBean create(String uuid, Long docNumber) {
        return new UserViewBean(uuid, docNumber);
    }

    @Override
    public String toString() {
        return "UserViewBean{" +
                "uuid='" + uuid + '\'' +
                ", uid=" + uid +
                ", docNum=" + docNum +
                ", updateTS=" + updateTS +
                ", referrer='" + referrer + '\'' +
                ", isInvalid=" + isInvalid +
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

    public String getUuid() {
        return uuid;
    }

    public Boolean getIsInvalid() {
        return isInvalid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setDocNum(Long docNum) {
        this.docNum = docNum;
    }

    public String getReferrer() {
        return referrer;
    }

    public Long getParentDocNum() {
        return parentDocNum;
    }

    public void setParentDocNum(Long parentDocNum) {
        this.parentDocNum = parentDocNum;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public static class UserViewBeanSerDe implements JsonSerializer<UserViewBean>, JsonDeserializer<UserViewBean> {

        @Override
        public JsonElement serialize(
                UserViewBean userViewBean,
                Type type,
                JsonSerializationContext jsonSerializationContext) {

            JsonObject root = new JsonObject();
            root.addProperty("uuid", userViewBean.getUuid());
            root.addProperty("uid", userViewBean.getUid());
            root.addProperty("docNum", userViewBean.getDocNum());
            root.addProperty("parentDoc", userViewBean.getParentDocNum());
            root.addProperty("updateTS", userViewBean.getUpdateTS().toString("YYYY-MM-dd HH:mm:ss"));
            return root;
        }

        @Override
        public UserViewBean deserialize(
                JsonElement jsonElement,
                Type type,
                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            final JsonObject root = jsonElement.getAsJsonObject();
            final String uuid = SerDeUtils.getValue(root, "uuid", "");
            final Long uid = SerDeUtils.getValue(root, "uid", 0l);
            final Long docNum = SerDeUtils.getValue(root, "docNum", 0l);
            final Long parentDocNum = SerDeUtils.getValue(root, "parentDoc", -1l);
            final String recType = SerDeUtils.getValue(root, "referrer", "UNKNOWN").toUpperCase();

            return new UserViewBean(uuid, uid, docNum, parentDocNum, recType);
        }
    }

    public String getCompositeKey() {
        return uid + COMPOSITE_KEY_SEPARATOR + docNum;
    }
}
