package com.brajagopal.rmend.exception;

import com.brajagopal.rmend.exception.beans.DSErrorBean;
import com.google.api.services.datastore.client.DatastoreException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <bxr4261>
 */
public class DatastoreExceptionManager {

    private static final Map<String, AtomicInteger> exceptionLog = new HashMap<String, AtomicInteger>();

    public static void trackException(DatastoreException _datastoreException, String _method) {
        DSErrorBean dsErrorBean = null;
        try {
            JSONObject json = new JSONObject(new JSONTokener(_datastoreException.getMessage()));
            dsErrorBean = DSErrorBean.createInstance(_datastoreException.getCode(), json.getJSONObject("error").getString("message"), _method);
        } catch (Exception e) {}

        try {
            dsErrorBean = DSErrorBean.createInstance(_datastoreException.getCode(), _datastoreException.getMessage(), _method);
        } catch (Exception e) {}

        if (!exceptionLog.containsKey(dsErrorBean.toString())) {
            exceptionLog.put(dsErrorBean.toString(), new AtomicInteger());
        }
        exceptionLog.get(dsErrorBean.toString()).incrementAndGet();
    }

    public static String getValues() {
        return "DatastoreExceptionManager{" +
                "exceptionLog=" + exceptionLog +
                '}';
    }
}
