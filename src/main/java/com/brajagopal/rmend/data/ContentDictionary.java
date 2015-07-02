package com.brajagopal.rmend.data;

import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.beans.RelationsBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public class ContentDictionary {

    private final SetMultimap<String, DocumentMeta> dict;
    public static final String KEY_SEPARATOR = ":";
    private static final Logger logger = Logger.getLogger(ContentDictionary.class);

    public ContentDictionary() {
        dict = HashMultimap.create();
    }

    public void putData(DocumentBean _documentBean) {

        for (BaseContent _contentBean : _documentBean.getContentBeans()) {
            // Skip RelationsBean
            if (_contentBean instanceof RelationsBean) {
                continue;
            }

            String key = makeKeyFromBean(_contentBean);
            dict.put(key, DocumentMeta.createInstance(_documentBean.getDocumentNumber(), _documentBean.getDocId(), _contentBean.getScore()));
        }
    }

    /*public Collection<DocumentMeta> getData(BaseContent.ContentType _contentType, String _type, String _name) {
        Collection<DocumentMeta> retVal;
        String key = _contentType + KEY_SEPARATOR + _type  + KEY_SEPARATOR + _name;
        StringUtils.replace(key, KEY_SEPARATOR+KEY_SEPARATOR, KEY_SEPARATOR);
        retVal = new TreeList<DocumentMeta>(dict.get(key));
        return retVal;
    }*/

    public static String makeKeyFromBean(BaseContent _contentBean) {

        String beanType = "";
        String beanName = "";

        try {
            beanType = _contentBean.getType();
        }
        catch (UnsupportedOperationException e) {}
        try {
            beanName = _contentBean.getName();
        }
        catch (UnsupportedOperationException e) {}

        return StringUtils.join(Arrays.asList(_contentBean.getContentType(), beanType, beanName), KEY_SEPARATOR);
    }

    public static BaseContent makeBeanFromKey(String _key) throws IllegalAccessException, InvalidClassException, InstantiationException {
        String[] keyToken = _key.split(KEY_SEPARATOR);
        Map<String, String> tokens = new HashMap<String, String>(3);
        if (keyToken.length != 3) {
            throw new UnsupportedOperationException("The key '"+_key+"' is not a valid one.");
        }
        tokens.put("contentType", keyToken[0]);
        tokens.put("type", keyToken[1]);
        tokens.put("name", keyToken[2]);
        return BaseContent.getChildInstance(tokens);

    }

    public static BaseContent.ContentType getContentType(String _key) {
        return BaseContent.ContentType.valueOf(StringUtils.split(_key, KEY_SEPARATOR)[0]);
    }

    public int size() {
        return dict.size();
    }

    public void persistData(IRMendDao _dao) throws DatastoreException {
        if (CollectionUtils.isEmpty(dict.entries())) {
            logger.warn("The ContentDictionary is empty.");
        }
        _dao.putEntityMeta(dict.entries());
    }

    @Override
    public String toString() {
        return "ContentDictionary{" +
                "dict=" + dict +
                '}';
    }
}
