package com.brajagopal.rmend.app.beans;

import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;

/**
 * @author <bxr4261>
 */
public class UserBean {

    private static final long BASE_MODIFIER = (System.currentTimeMillis() - new DateTime("2015-07-07").getMillis());
    private Long uid;
    private String userName;

    private UserBean(Long _uid, String _userName) {
        this.uid = _uid;
        this.userName = _userName;
    }

    public Long getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public static UserBean create(String userName) {
        return new UserBean((BASE_MODIFIER +  RandomUtils.nextLong(1, 200)), userName);
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uid=" + uid +
                ", userName='" + userName + '\'' +
                '}';
    }
}
