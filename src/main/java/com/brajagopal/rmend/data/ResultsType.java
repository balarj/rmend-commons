package com.brajagopal.rmend.data;

import com.brajagopal.rmend.data.meta.DocumentMeta;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <bxr4261>
 */
public enum ResultsType {
    RANDOM_10(100, 10),
    TOP_10(30, 10),
    TOP_5(20, 5),
    TOP_3(10, 3),
    TOP_1(10, 1),
    ALL(100, 30);

    private int fetchLimit;
    private int daoResultLimit;

    private ResultsType(int _daoResultLimit, int _fetchLimit) {
        daoResultLimit = _daoResultLimit;
        fetchLimit = _fetchLimit;
    }

    public int getDaoResultLimit() {
        return daoResultLimit;
    }

    protected int getFetchLimit() {
        return fetchLimit;
    }

    public static Collection<DocumentMeta> getResults(Collection<DocumentMeta> _input, ResultsType _type) {
        if(_input == null || _input.isEmpty()) {
            return CollectionUtils.EMPTY_COLLECTION;
        }

        final List<DocumentMeta> value = new ArrayList<DocumentMeta>(_input);
        switch (_type){
            case RANDOM_10:
                if (value.size() > _type.getFetchLimit()) {
                    Collections.shuffle(value);
                    return value.subList(0, _type.getFetchLimit());
                }
                return value;
            case TOP_10:
                if (value.size() > _type.getFetchLimit()) {
                    Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                    return value.subList(0, _type.getFetchLimit());
                }
                return value;
            case TOP_5:
                if (value.size() > _type.getFetchLimit()) {
                    Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                    return value.subList(0, _type.getFetchLimit());
                }
                return value;
            case TOP_3:
                if (value.size() > _type.getFetchLimit()) {
                    Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                    return value.subList(0, _type.getFetchLimit());
                }
                return value;
            case ALL:
                Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                return value;
            case TOP_1:
            default:
                Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                return value.subList(0, 1);
        }
    }

    public static final ResultsType DEFAULT_RESULT_TYPE = ResultsType.RANDOM_10;
}
