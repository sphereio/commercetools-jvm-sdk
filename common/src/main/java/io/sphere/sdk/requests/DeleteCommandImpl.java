package io.sphere.sdk.requests;

import io.sphere.sdk.models.Versioned;

public abstract class DeleteCommandImpl<I> extends CommandImpl<I> {
    private final Versioned versionData;

    protected DeleteCommandImpl(final Versioned versionData) {
        this.versionData = versionData;
    }

    @Override
    public HttpRequest httpRequest() {
        return HttpRequest.of(HttpMethod.DELETE, baseEndpointWithoutId() + "/" + versionData.getId() + "?version=" + versionData.getVersion());
    }

    protected abstract String baseEndpointWithoutId();
}