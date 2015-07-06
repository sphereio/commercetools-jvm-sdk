package io.sphere.sdk.http;

import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.*;

final class AsyncHttpClientAdapterImpl extends Base implements AsyncHttpClientAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHttpClientAdapterImpl.class);
    private final AsyncHttpClient asyncHttpClient;
    private final ForkJoinPool threadPool = new ForkJoinPool();

    private AsyncHttpClientAdapterImpl(final AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    @Override
    public CompletionStage<HttpResponse> execute(final HttpRequest httpRequest) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executing " + httpRequest);
        }
        final Request request = asAhcRequest(httpRequest);
        try {
            final CompletionStage<Response> future = wrap(asyncHttpClient.executeRequest(request));
            return future.thenApply((Response response) -> {
                final byte[] responseBodyAsBytes = getResponseBodyAsBytes(response);
                Optional<byte[]> body = responseBodyAsBytes.length > 0 ? Optional.of(responseBodyAsBytes) : Optional.empty();
                final HttpResponse httpResponse = HttpResponse.of(response.getStatusCode(), body, Optional.of(httpRequest), HttpHeaders.of(response.getHeaders()));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("response " + httpResponse);
                }
                return httpResponse;
            });
        } catch (final IOException e) {
            final CompletableFuture<HttpResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new HttpException(e));
            return future;
        }
    }

    private byte[] getResponseBodyAsBytes(final Response response) {
        try {
            return response.getResponseBodyAsBytes();
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    /* package scope for testing */
    <T> Request asAhcRequest(final HttpRequest request) {
        final RequestBuilder builder = new RequestBuilder()
                .setUrl(request.getUrl())
                .setMethod(request.getHttpMethod().toString());

        request.getHeaders().getHeadersAsMap().forEach((name, values) -> values.forEach( value -> builder.addHeader(name, value)));

        request.getBody().ifPresent(body -> {
            if (body instanceof StringHttpRequestBody) {
                final String bodyAsString = ((StringHttpRequestBody) body).getString();
                builder.setBodyEncoding(StandardCharsets.UTF_8.name()).setBody(bodyAsString);
            } else if (body instanceof FileHttpRequestBody) {
                builder.setBody(((FileHttpRequestBody) body).getFile());
            } else if (body instanceof FormUrlEncodedHttpRequestBody) {
                ((FormUrlEncodedHttpRequestBody) body).getData().forEach((name, value) -> builder.addQueryParameter(name, value));
            }
        });
        final Request build = builder.build();
        return build;
    }

    @Override
    public void close() {
        asyncHttpClient.close();
        threadPool.shutdown();
    }

    public static AsyncHttpClientAdapterImpl of(final AsyncHttpClient asyncHttpClient) {
        return new AsyncHttpClientAdapterImpl(asyncHttpClient);
    }

    /**
     * Creates a {@link CompletionStage} from a {@link ListenableFuture}.
     * @param listenableFuture the future of the ning library
     * @param executor the executor to run the future in
     * @param <T> Type of the value that will be returned.
     * @return the Java 8 future implementation
     */
    private static <T> CompletionStage<T> wrap(final ListenableFuture<T> listenableFuture, final Executor executor) {
        final CompletableFuture<T> result = new CompletableFuture<>();
        final Runnable listener = () -> {
            try {
                final T value = listenableFuture.get();
                result.complete(value);
            } catch (final InterruptedException | ExecutionException e) {
                result.completeExceptionally(e.getCause());
            }
        };
        listenableFuture.addListener(listener, executor);
        return result;
    }

    private CompletionStage<Response> wrap(final ListenableFuture<Response> listenableFuture) {
        return wrap(listenableFuture, threadPool);
    }
}