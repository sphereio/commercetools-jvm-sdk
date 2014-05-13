package io.sphere.sdk.client;


import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.typesafe.config.Config;

import java.io.IOException;

class NingAsyncHttpClient implements HttpClient {

    private final ClientCredentials clientCredentials;
    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String coreUrl;
    private final String projectKey;

    public NingAsyncHttpClient(final Config config) {
        clientCredentials = SphereClientCredentials.createAndBeginRefreshInBackground(config, new OAuthClient(asyncHttpClient));
        coreUrl = config.getString("sphere.core");
        projectKey = config.getString("sphere.project");
    }

    @Override
    public <T> ListenableFuture<HttpResponse> execute(final Requestable<T> requestable) {
        final Request request = asRequest(requestable);
        try {
            final ListenableFutureAdapter<Response> future = new ListenableFutureAdapter<Response>(asyncHttpClient.executeRequest(request));
            return Futures.transform(future, new Function<Response, HttpResponse>() {
                @Override
                public HttpResponse apply(final Response response) {
                    try {
                        return new HttpResponse(response.getStatusCode(), response.getResponseBody(Charsets.UTF_8.name()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);//TODO unify exception handling, to sphere exception
                    }
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);//TODO unify exception handling, to sphere exception
        }
    }

    private <T> Request asRequest(final Requestable<T> requestable) {
        final HttpRequest request = requestable.httpRequest();
        final RequestBuilder builder = new RequestBuilder().setUrl(coreUrl + "/" + projectKey + request.getPath()).setMethod(request.getHttpMethod().toString()).
                setHeader("Authorization", "Bearer " + clientCredentials.getAccessToken());
        return builder.build();
    }

    @Override
    public void close() {
        asyncHttpClient.close();
    }
}