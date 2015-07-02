package com.brajagopal.rmend.config;

import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <bxr4261>
 */
public class RMendConfig {
    private final Properties configuration = new Properties();

    public static final String API_URL_KEY = "calais.api.url";
    public static final String API_TOKEN_KEY = "calais.api.access.token";
    public static final String API_RESPONSE_TYPE_KEY = "calais.api.response.type";
    public static final String API_REQUEST_METADATA_KEY = "calais.api.request.metadatatype";
    public static final String RMEND_SLEEP_DELAY_KEY = "rmend.ingest.sleep.delay.ms";

    public static final long DEFAULT_SLEEP_DELAY_IN_MS = 500l;

    private static final Logger logger = Logger.getLogger(RMendConfig.class);

    public RMendConfig() throws IOException {
        configuration.load(getClass().getClassLoader().getResourceAsStream("config/prod.properties"));
    }

    public RMendConfig(String fileName) throws IOException {
        configuration.load(new FileReader(fileName));
    }

    public String getApiURL() {
        return configuration.getProperty(
                API_URL_KEY,
                "https://api.thomsonreuters.com/permid/calais"
        );
    }

    public String getApiToken() {
        return configuration.getProperty(API_TOKEN_KEY);
    }

    public String getResponseType() {
        return configuration.getProperty(
                API_RESPONSE_TYPE_KEY,
                ContentType.APPLICATION_JSON.getMimeType()
        );
    }

    public String getMetaDataType() {
        return configuration.getProperty(API_REQUEST_METADATA_KEY);
    }

    public long getDelayInMillis() {

        try {
            return Long.parseLong(configuration.getProperty(
                    RMEND_SLEEP_DELAY_KEY,
                    String.valueOf(DEFAULT_SLEEP_DELAY_IN_MS)
            ));
        }
        catch (NumberFormatException nfe) {
            logger.warn(nfe);
        }

        return DEFAULT_SLEEP_DELAY_IN_MS;
    }
}
