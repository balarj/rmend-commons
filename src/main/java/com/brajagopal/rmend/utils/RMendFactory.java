package com.brajagopal.rmend.utils;

import com.brajagopal.rmend.dao.GCloudDao;
import com.brajagopal.rmend.dao.IRMendDao;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.datastore.client.DatastoreOptions;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author <bxr4261>
 */
public class RMendFactory {

    private final static Logger logger = Logger.getLogger(RMendFactory.class);

    private static IRMendDao dao;
    private static DocumentManager documentManager;

    private static final String SERVICE_ACCOUNT_EMAIL =
            "777065455744-gqlc8dar2us2amkcig46lt0fffrarlqc" +
                    "@developer.gserviceaccount.com";

    public static IRMendDao getDao() {
        if (dao == null) {
            try {
                logger.info("Generating a new DAO instance");
                dao = new GCloudDao(getCredentials());
            } catch (GeneralSecurityException e) {
                logger.warn(e);
            } catch (IOException e) {
                logger.warn(e);
            }
        }
        return dao;
    }

    public static DocumentManager getDocumentManager() {
        if (documentManager == null) {
            logger.info("Generating a new DocumentManager instance");
            documentManager = new DocumentManager(getDao());
        }
        return documentManager;
    }

    private static GoogleCredential getCredentials() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        File fCredPk = new File("conf/rmend-be.p12");

        GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(DatastoreOptions.SCOPES)
                .setServiceAccountPrivateKeyFromP12File(fCredPk)
                .build();

        return credential;
    }

    public static String getUserInput(String _message) {
        Scanner in = null;
        String retVal = null;
        try {
            in = new Scanner(new InputStreamReader(System.in));
            System.out.print(_message + ": ");
            retVal = in.nextLine();
        }
        catch (NoSuchElementException e) {}
        finally {
            in.reset();
            IOUtils.closeQuietly(in);
        }
        return retVal;
    }

    public static String trimToSentence(String value, int size) {
        return (value.length() > size) ?
                    StringUtils.substringBeforeLast(StringUtils.left(value, size), " ") :
                    value;
    }
}
