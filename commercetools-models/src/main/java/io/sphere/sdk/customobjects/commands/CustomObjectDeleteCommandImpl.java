package io.sphere.sdk.customobjects.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import io.sphere.sdk.client.HttpRequestIntent;
import io.sphere.sdk.commands.CommandImpl;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.CustomObjectUtils;
import io.sphere.sdk.http.HttpMethod;
import io.sphere.sdk.json.SphereJsonUtils;

import static java.lang.String.format;

/**
 * Deletes a custom object in the platform.
 *
 *
 */
final class CustomObjectDeleteCommandImpl<T> extends CommandImpl<CustomObject<T>> implements CustomObjectDeleteCommand<T> {
    private final String path;
    private final JavaType javaType;

    @Override
    protected JavaType jacksonJavaType() {
        return javaType;
    }

    @Override
    public HttpRequestIntent httpRequestIntent() {
        return HttpRequestIntent.of(HttpMethod.DELETE, CustomObjectEndpoint.PATH + path);
    }

    CustomObjectDeleteCommandImpl(final String container, final String key, final Class<T> valueClass) {
        this(container, key, SphereJsonUtils.convertToJavaType(valueClass));
    }

    CustomObjectDeleteCommandImpl(final String container, final String key, final TypeReference<T> typeReference) {
        this(container, key, SphereJsonUtils.convertToJavaType(typeReference));
    }

    CustomObjectDeleteCommandImpl(final String container, final String key, final JavaType valueJavaType) {
        this.path = format("/%s/%s", container, key);
        this.javaType = CustomObjectUtils.getCustomObjectJavaTypeForValue(valueJavaType);
    }

    CustomObjectDeleteCommandImpl(final String id, final Long version, final Class<T> valueClass) {
        this(id, version, SphereJsonUtils.convertToJavaType(valueClass));
    }

    CustomObjectDeleteCommandImpl(final String id, final Long version, final TypeReference<T> typeReference) {
        this(id, version, SphereJsonUtils.convertToJavaType(typeReference));
    }

    CustomObjectDeleteCommandImpl(final String id, final Long version, final JavaType valueJavaType) {
        this.path = format("/%s?version=%d", id, version);
        this.javaType = CustomObjectUtils.getCustomObjectJavaTypeForValue(valueJavaType);
    }
}
