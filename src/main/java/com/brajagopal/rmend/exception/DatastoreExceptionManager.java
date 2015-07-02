package com.brajagopal.rmend.exception;

import com.brajagopal.rmend.exception.beans.DSErrorBean;
import com.google.api.services.datastore.client.DatastoreException;
import org.json.JSONException;
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
        try {
            JSONObject json = new JSONObject(new JSONTokener(_datastoreException.getMessage()));
            DSErrorBean dsErrorBean = DSErrorBean.createInstance(_datastoreException.getCode(), json.getJSONObject("error").getString("message"), _method);
            if (!exceptionLog.containsKey(dsErrorBean.toString())) {
                exceptionLog.put(dsErrorBean.toString(), new AtomicInteger());
            }
            exceptionLog.get(dsErrorBean.toString()).incrementAndGet();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getValues() {
        return "DatastoreExceptionManager{" +
                "exceptionLog=" + exceptionLog +
                '}';
    }
}
