package com.brajagopal.rmend.dao;

import com.brajagopal.rmend.data.ContentDictionary;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.brajagopal.rmend.exception.DatastoreExceptionManager;
import com.brajagopal.rmend.utils.JsonUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.datastore.DatastoreV1.*;
import com.google.api.services.datastore.client.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


/**
 * @author <bxr4261>
 */
@SuppressWarnings("unused")
public class GCloudDao implements IRMendDao {

    private static final Logger logger = Logger.getLogger(GCloudDao.class);
    private final Datastore datastore;

    private int writeBatchSize;
    private int readBatchSize;

    static final String DOCUMENT_KIND = "document";
    static final String DOCUMENT_MD5SUM_KIND = "md5sum";
    static final String DOCUMENT_TITLE_KIND = "title";
    static final String DOCUMENT_NUMBER_KIND = "docNumber";
    static final String DOCUMENT_JSON_KIND = "docBody";
    static final String DOCUMENT_TOPIC_KIND = "topic";
    static final String KEY_PROPERTY = "__key__";

    static final String ENTITY_KIND = "entity";
    static final String DOCUMENT_SCORE_KIND = "score";
    static final String ENTITIY_CLASSIFIER_KIND = "entityId";
    static final String DOCUMENT_META_KIND = "docMeta";

    static final int DEFAULT_RESULT_LIMIT = 20;
    static final int DEFAULT_READ_BATCH_SIZE = 10;
    static final int DEFAULT_WRITE_BATCH_SIZE = 1;
    static final long DEFAULT_SLEEP = 500l;

    public GCloudDao() throws GeneralSecurityException, IOException {
        this(DEFAULT_WRITE_BATCH_SIZE, DEFAULT_READ_BATCH_SIZE);
    }

    public GCloudDao(GoogleCredential credential) throws GeneralSecurityException, IOException {
        datastore = DatastoreFactory.get().create(DatastoreHelper.getOptionsFromEnv().credential(credential).build());
        this.readBatchSize = DEFAULT_READ_BATCH_SIZE;
        this.writeBatchSize = DEFAULT_WRITE_BATCH_SIZE;
    }

    private GCloudDao(int _writeBatchSize, int _readBatchSize) throws GeneralSecurityException, IOException {
        datastore = DatastoreHelper.getDatastoreFromEnv();
        this.readBatchSize = _readBatchSize;
        this.writeBatchSize = _writeBatchSize;
    }

    public static GCloudDao getLocalInstance() throws GeneralSecurityException, IOException {
        return new GCloudDao();
    }

    @Override
    public void putDocument(DocumentBean _docBean) throws DatastoreException, InterruptedException {
        putDocument(_docBean, _docBean.getContentMD5Sum());
    }

    @Override
    public void putDocument(DocumentBean _docBean, String _identifier) throws DatastoreException, InterruptedException {
        Collection<Value> topicValues = new ArrayList<Value>();
        Mutation.Builder builder = Mutation.newBuilder();
        for (String topic : _docBean.getTopic()) {
            topicValues.add(DatastoreHelper.makeValue(topic).build());
        }

        Entity article = Entity.newBuilder()
                .setKey(DatastoreHelper.makeKey(_docBean.getContentType().toString(), _identifier))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_MD5SUM_KIND, DatastoreHelper.makeValue(_docBean.getContentMD5Sum()).setIndexed(false)))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_TITLE_KIND, DatastoreHelper.makeValue(_docBean.getTitle()).setIndexed(false)))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_TOPIC_KIND, DatastoreHelper.makeValue(topicValues)))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_NUMBER_KIND, DatastoreHelper.makeValue(_docBean.getDocumentNumber())))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_JSON_KIND, DatastoreHelper.makeValue(JsonUtils.getGsonInstance().toJson(_docBean)).setIndexed(false)))
                .build();

        CommitRequest request = CommitRequest.newBuilder()
                .setMode(CommitRequest.Mode.NON_TRANSACTIONAL)
                .setMutation(Mutation.newBuilder().addInsert(article))
                .build();

        builder.addInsert(article);

        try {
            persist(builder);
        }
        catch (DatastoreException e) {
            DatastoreExceptionManager.trackException(e, "putDocument()");
            throw e;
        }
    }

    @Override
    public DocumentBean getDocument(Long _documentNumber) throws DatastoreException {
        return getDocument(_documentNumber, DEFAULT_RESULT_LIMIT);
    }

    @Override
    public DocumentBean getDocument(Long _documentNumber, Integer _limit) throws DatastoreException {
        Query.Builder query = Query.newBuilder();
        query.addKindBuilder().setName(BaseContent.ContentType.DOCUMENT_INFO.toString());
        query.setFilter(DatastoreHelper.makeFilter(
                DOCUMENT_NUMBER_KIND,
                PropertyFilter.Operator.EQUAL,
                DatastoreHelper.makeValue(_documentNumber)));
        query.setLimit(_limit);
        List<Entity> documents = runQuery(query.build());
        if (documents.size() == 0) {
            logger.warn("No Document found for DocumentNumber: " + _documentNumber);
        }
        else if (documents.size() == 1) {
            Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(documents.get(0));
            List<Value> topicValues = DatastoreHelper.getList(propertyMap.get(DOCUMENT_TOPIC_KIND));
            Collection<String> topics = Lists.transform(topicValues, new Function<Value, String>() {
                @Nullable
                @Override
                public String apply(Value value) {
                    return DatastoreHelper.getString(value);
                }
            });

            return JsonUtils.getGsonInstance().fromJson(DatastoreHelper.getString(propertyMap.get(DOCUMENT_JSON_KIND)), DocumentBean.class);
        }
        return null;
    }

    @Override
    public void putEntityMeta(Collection<Map.Entry<String , DocumentMeta>> _docMetaCollection) throws DatastoreException, InterruptedException {
        int runningCtr = 0;
        int totalCount = _docMetaCollection.size();
        String lastEntityIdentifier = "";
        int entityCount = 0;
        Mutation.Builder builder = Mutation.newBuilder();
        for (Map.Entry<String, DocumentMeta> entry : _docMetaCollection) {
            String _entityIdentifier = entry.getKey();
            DocumentMeta _docMeta = entry.getValue();
            String identifier = _entityIdentifier + ContentDictionary.KEY_SEPARATOR + _docMeta.getDocId();
            Entity article = Entity.newBuilder()
                    .setKey(DatastoreHelper.makeKey(ContentDictionary.getContentType(_entityIdentifier).toString(), identifier))
                    .addProperty(DatastoreHelper.makeProperty(ENTITIY_CLASSIFIER_KIND, DatastoreHelper.makeValue(_entityIdentifier)))
                    .addProperty(DatastoreHelper.makeProperty(DOCUMENT_SCORE_KIND, DatastoreHelper.makeValue(_docMeta.getScore())))
                    .addProperty(DatastoreHelper.makeProperty(DOCUMENT_META_KIND, DatastoreHelper.makeValue(JsonUtils.getGsonInstance().toJson(_docMeta)).setIndexed(false)))
                    .build();

            builder.addInsert(article);

            if (!lastEntityIdentifier.equals(_entityIdentifier)) {
                if (entityCount != 0) {
                    logger.info("Finished processing (" + entityCount + " records) for entity: {" + lastEntityIdentifier + "}");
                    logger.info("*** Overall progress: "+Math.round(runningCtr/totalCount) + "% ***");
                    logger.info("Starting to process entity: {" + _entityIdentifier + "}");
                }
                // reset the identifier and counter
                lastEntityIdentifier = _entityIdentifier;
                entityCount = 0;
            }

            if ((runningCtr++ % writeBatchSize) == 0) {
                try {
                    persist(builder);
                    entityCount++;
                }
                catch (DatastoreException e) {
                    DatastoreExceptionManager.trackException(e, "putEntityMeta(Collection)");
                }
                finally {
                    builder = Mutation.newBuilder();
                }
            }
        }
    }

    @Override
    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier) throws DatastoreException {
        return getEntityMeta(_metaIdentifier, DEFAULT_RESULT_LIMIT);
    }

    @Override
    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier, Integer _limit) throws DatastoreException {
        Collection<DocumentMeta> retVal = null;
        Query.Builder query = Query.newBuilder();
        query.addKindBuilder().setName(ContentDictionary.getContentType(_metaIdentifier).toString());
        query.setFilter(DatastoreHelper.makeFilter(
                ENTITIY_CLASSIFIER_KIND,
                PropertyFilter.Operator.EQUAL,
                DatastoreHelper.makeValue(_metaIdentifier)
        ));
        query.addOrder(DatastoreHelper.makeOrder("score", PropertyOrder.Direction.DESCENDING));
        query.setLimit(_limit);
        List<Entity> entityMetadata = runQuery(query.build());
        if (entityMetadata.size() == 0) {
            logger.warn("No Metadata found for EntityId: " + _metaIdentifier);
        }
        else if (entityMetadata.size() > 0) {
            retVal = new ArrayList<DocumentMeta>(entityMetadata.size());
            for (Entity entity : entityMetadata) {
                Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(entity);
                retVal.add(JsonUtils.getGsonInstance().fromJson(DatastoreHelper.getString(propertyMap.get(DOCUMENT_META_KIND)), DocumentMeta.class));
            }
        }
        return retVal;
    }

    @Override
    public TreeMultimap<BaseContent.ContentType, DocumentMeta> getEntityMeta(Collection<String> _metaIdentifiers) throws DatastoreException {
        return getEntityMeta(_metaIdentifiers, DEFAULT_RESULT_LIMIT);
    }

    @Override
    public TreeMultimap<BaseContent.ContentType, DocumentMeta> getEntityMeta(Collection<String> _metaIdentifiers, Integer _limit) throws DatastoreException {
        TreeMultimap<BaseContent.ContentType, DocumentMeta> retVal = TreeMultimap.create(ComparatorUtils.NATURAL_COMPARATOR, DocumentMeta.DOCUMENT_META_COMPARATOR);
        for (String metaIdentifier : _metaIdentifiers) {
            Collection<DocumentMeta> entityMeta = getEntityMeta(metaIdentifier);
            if (!CollectionUtils.isEmpty(entityMeta)) {
                BaseContent.ContentType key = ContentDictionary.getContentType(metaIdentifier);
                retVal.putAll(key, getEntityMeta(metaIdentifier));
            }
        }
        return retVal;
    }

    private void persist(Mutation.Builder _builder) throws DatastoreException, InterruptedException {
        CommitRequest request = CommitRequest.newBuilder()
                .setMode(CommitRequest.Mode.NON_TRANSACTIONAL)
                .setMutation(_builder)
                .build();

        int timeout_ms = 100;
        int timeout_cnt = 10;
        while(true && timeout_cnt != 0) {
            try {
                datastore.commit(request);
                break;
            } catch (DatastoreException e) {
                if (Arrays.asList(403, 409, 503).contains(e.getCode())) {
                    Thread.sleep(timeout_ms);
                    timeout_ms *= 2;
                    timeout_cnt--;
                }
                else if (e.getCode() == 500) {
                    timeout_cnt-=6; // We dont want to keep retrying this more than once
                    Thread.sleep(30000l);
                }
                else {
                    throw e;
                }
            }
        }
    }

    private List<Entity> runQuery(Query query) throws DatastoreException {
        return runQuery(query, 10);
    }

    private List<Entity> runQuery(Query query, int _resultLimit) throws DatastoreException {
        RunQueryRequest.Builder request = RunQueryRequest.newBuilder();
        request.setQuery(query);
        RunQueryResponse response = datastore.runQuery(request.build());

        if (response.getBatch().getMoreResults() == QueryResultBatch.MoreResultsType.NOT_FINISHED) {
            System.err.println("WARNING: partial results\n");
        }
        List<EntityResult> results = response.getBatch().getEntityResultList();
        List<Entity> entities = new ArrayList<Entity>(results.size());
        for (EntityResult result : results) {
            entities.add(result.getEntity());
        }
        return entities;
    }
}
