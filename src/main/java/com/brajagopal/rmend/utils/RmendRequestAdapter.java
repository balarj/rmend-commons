package com.brajagopal.rmend.utils;

import com.brajagopal.rmend.app.beans.UserViewBean;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpPipeliningClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author <bxr4261>
 */
public class RmendRequestAdapter {

    private final ConnectingIOReactor ioReactor;
    private final PoolingNHttpClientConnectionManager cm;
    private final CloseableHttpPipeliningClient httpclient;
    private final String targetHost;
    private final String endpointTemplate;

    private static final Logger logger = Logger.getLogger(RmendRequestAdapter.class);

    public RmendRequestAdapter(String _targetHost, String _endpointTemplate) throws IOReactorException {
        ioReactor = new DefaultConnectingIOReactor();
        cm = new PoolingNHttpClientConnectionManager(ioReactor);
        cm.setMaxTotal(20);

        httpclient = HttpAsyncClients.createPipelining(cm);
        this.targetHost = _targetHost;
        this.endpointTemplate = _endpointTemplate;
    }

    public Future<List<HttpResponse>> makeRequests(Long userId, Collection<Long> documentIds) {
        Multimap<String, Integer> responses = HashMultimap.create();
        Future<List<HttpResponse>> futureResponses =
                httpclient.execute(new HttpHost(targetHost), buildRequests(userId, documentIds), null);

        return futureResponses;
    }

    private List<HttpRequest> buildRequests(Long userId, Collection<Long> documentIds) {
        String endpoint = String.format(endpointTemplate, userId);
        List<HttpRequest> requests = new ArrayList<HttpRequest>();
        for (Long documentId : documentIds) {
            UserViewBean userViewBean = UserViewBean.create(userId, documentId);
            HttpPut request = new HttpPut(endpoint);
            try {
                request.setEntity(new StringEntity(JsonUtils.getGsonInstance().toJson(userViewBean)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            requests.add(request);
        }
        return requests;
    }

    static final class ResponseConsumer extends AsyncCharConsumer<Boolean> {

        @Override
        protected void onResponseReceived(final HttpResponse response) {
        }

        @Override
        protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
            while (buf.hasRemaining()) {
                System.out.print(buf.get());
            }
        }

        @Override
        protected void releaseResources() {
        }

        @Override
        protected Boolean buildResult(final HttpContext context) {
            return Boolean.TRUE;
        }

    }
}
