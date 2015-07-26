package com.brajagopal.rmend.utils;

import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.ContentDictionary;
import com.brajagopal.rmend.data.ResultsType;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.beans.TopicBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.brajagopal.rmend.exception.DocumentNotFoundException;
import com.google.api.services.datastore.client.DatastoreException;
import org.apache.log4j.Logger;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public class DocumentManager {

    private IRMendDao dao;
    private static final Logger logger = Logger.getLogger(DocumentManager.class);

    public DocumentManager(IRMendDao _dao) {
        this.dao = _dao;
    }

    /**
     * Method to retrieve Document (content), filtered by topic
     *
     * @param _topicBean
     * @param _resultsType
     * @return
     * @throws DatastoreException
     */
    public Collection<DocumentBean> getContentByTopic(TopicBean _topicBean, ResultsType _resultsType) throws DatastoreException, DocumentNotFoundException {
        Collection<DocumentBean> docBeans = new ArrayList<DocumentBean>();
        Collection<DocumentMeta> docMetaCollection = getDocumentMeta(ContentDictionary.makeKeyFromBean(_topicBean), _resultsType);
        for (DocumentMeta documentMeta : docMetaCollection) {
            docBeans.add(dao.getDocument(documentMeta.getDocumentNumber()));
        }
        return docBeans;
    }

    public Collection<String> getAllAvailableTopics() throws DatastoreException {
        Collection<String> topics = dao.getAllTopics();
        return topics;
    }

    public static TopicBean makeTopicBean(String _topic) throws IllegalAccessException, InvalidClassException, InstantiationException {
        Map<String, String> beanValues = new HashMap<String, String>();
        beanValues.put("contentType", BaseContent.ContentType.TOPICS.toString());
        beanValues.put("name", _topic);
        BaseContent retVal = BaseContent.getChildInstance(beanValues);
        if (retVal != null && retVal instanceof TopicBean) {
            return (TopicBean) retVal;
        }
        else {
            throw new RuntimeException("Unable to generate a TopicBean for the topic: "+_topic);
        }
    }

    protected Collection<DocumentMeta> getDocumentMeta(String _metaIdentifier,  ResultsType resultsType) throws DatastoreException {
        Collection<DocumentMeta> entityValues = dao.getEntityMeta(_metaIdentifier);
        return ResultsType.getResultsForCR(entityValues, resultsType);
    }
}
