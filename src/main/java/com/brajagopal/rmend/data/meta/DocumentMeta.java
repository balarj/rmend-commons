package com.brajagopal.rmend.data.meta;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Comparator;

/**
 * @author <bxr4261>
 */
public class DocumentMeta {

    private final long documentNumber;
    private final String docId;
    private final double score;

    private DocumentMeta(long _documentNumber, String _docId, Double _score) {
        this.documentNumber = _documentNumber;
        this.docId = _docId;
        this.score = _score;
    }

    public static DocumentMeta createInstance(long _documentNumber, String _docId, Double _score) {
        return new DocumentMeta(_documentNumber, _docId, _score);
    }

    public long getDocumentNumber() {
        return documentNumber;
    }

    public String getDocId() {
        return docId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "DocumentMeta {" +
                "documentNumber=" + documentNumber +
                ", docId='" + docId + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentMeta)) return false;

        DocumentMeta that = (DocumentMeta) o;

        if (Double.compare(that.score, score) != 0) return false;
        if (documentNumber != that.documentNumber) return false;
        if (!docId.equals(that.docId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (documentNumber ^ (documentNumber >>> 32));
        result = 31 * result + docId.hashCode();
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static final Comparator<DocumentMeta> DOCUMENT_META_COMPARATOR = new Comparator<DocumentMeta>() {
        @Override
        public int compare(DocumentMeta o1, DocumentMeta o2) {
            if (o1.score > o2.score) {
                return -1;
            } else if (o1.score < o2.score) {
                return 1;
            }

            if (o1.documentNumber < o2.documentNumber) {
                return -1;
            } else if (o1.documentNumber > o2.documentNumber) {
                return 1;
            }

            return 0;
        }
    };

    public static class DocumentMetaSerDe implements JsonSerializer<DocumentMeta>, JsonDeserializer<DocumentMeta> {

        @Override
        public JsonElement serialize(DocumentMeta documentMeta, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject root = new JsonObject();
            root.addProperty("docId", documentMeta.docId);
            root.addProperty("docNum", documentMeta.documentNumber);
            root.addProperty("score", documentMeta.score);
            return root;
        }

        @Override
        public DocumentMeta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            final JsonObject root = jsonElement.getAsJsonObject();
            final String docId = root.get("docId").getAsString();
            final Long documentNumber = root.get("docNum").getAsLong();
            final Double score = root.get("score").getAsDouble();

            return new DocumentMeta(documentNumber, docId, score);
        }
    }
}
