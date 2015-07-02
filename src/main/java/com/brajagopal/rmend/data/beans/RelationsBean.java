package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class RelationsBean extends BaseContent {

    private String careerType;
    private String status;

    public RelationsBean() {
        this(ContentType.DISCARDED);
    }

    private RelationsBean(ContentType _contentType) {
        super(_contentType);
    }

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.type = MapUtils.getString(_value, "_type", null);
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.careerType = MapUtils.getString(_value, "careertype", null);
        this.status = MapUtils.getString(_value, "status", null);
    }

    @Override
    public BaseContent getInstance() {
        return new RelationsBean();
    }

    @Override
    public double getScore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "RelationsBean {" +
                "contentType=" + getContentType() +
                ", type='" + getType() + '\'' +
                ", forEndUserDisplay=" + isForEndUserDisplay() +
                ", careerType='" + getCareerType() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName() not supported by "+getClass());
    }

    public String getCareerType() {
        return careerType;
    }

    public String getStatus() {
        return status;
    }
}
