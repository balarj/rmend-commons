package com.brajagopal.rmend.dao;

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
    public void putDocument(DocumentBean _docBean) throws DatastoreException;
    public void putDocument(DocumentBean _docBean, String _identifier) throws DatastoreException;

    public DocumentBean getDocument(Long _documentNumber) throws DatastoreException;
    public DocumentBean getDocument(Long _documentNumber, Integer _limit) throws DatastoreException;

    // Entity specific DAO methods
    public void putEntityMeta(Collection<Map.Entry<String, DocumentMeta>> _docMetaCollection) throws DatastoreException;

    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier) throws DatastoreException;
    public TreeMultimap<BaseContent.ContentType, DocumentMeta> getEntityMeta(Collection<String> _metaIdentifiers) throws DatastoreException;

    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier, Integer _limit) throws DatastoreException;
    public TreeMultimap<BaseContent.ContentType, DocumentMeta> getEntityMeta(Collection<String> _metaIdentifiers, Integer _limit) throws DatastoreException;
}
