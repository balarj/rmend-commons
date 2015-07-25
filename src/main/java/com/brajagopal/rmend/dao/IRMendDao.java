package com.brajagopal.rmend.dao;

import com.brajagopal.rmend.data.ResultsType;
import com.brajagopal.rmend.exception.DocumentNotFoundException;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.common.collect.TreeMultimap;

import java.util.Collection;
import java.util.Map;

/**
 * @author <bxr4261>
 */
@SuppressWarnings("unused")
public interface IRMendDao {

    // Document specific DAO methods
    public void putDocument(DocumentBean _docBean) throws DatastoreException, InterruptedException;
    public void putDocument(DocumentBean _docBean, String _identifier) throws DatastoreException, InterruptedException;

    public DocumentBean getDocument(Long _documentNumber) throws DatastoreException, DocumentNotFoundException;
    public DocumentBean getDocument(Long _documentNumber, Integer _limit) throws DatastoreException, DocumentNotFoundException;

    // Entity specific DAO methods
    public void putEntityMeta(Collection<Map.Entry<String, DocumentMeta>> _docMetaCollection) throws DatastoreException, InterruptedException;

    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier) throws DatastoreException;
    public TreeMultimap<BaseContent.ContentType, DocumentMeta> getEntityMeta(Collection<String> _metaIdentifiers) throws DatastoreException;

    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier, ResultsType _resultsType) throws DatastoreException;
    public TreeMultimap<BaseContent.ContentType, DocumentMeta> getEntityMeta(Collection<String> _metaIdentifiers, ResultsType _resultsType) throws DatastoreException;

    public Collection<String> getAllTopics() throws DatastoreException;
}
