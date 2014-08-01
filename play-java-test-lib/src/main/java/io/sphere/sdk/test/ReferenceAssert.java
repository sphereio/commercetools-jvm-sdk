package io.sphere.sdk.test;

import io.sphere.sdk.models.Reference;
import org.fest.assertions.Assertions;
import org.fest.assertions.GenericAssert;

public class ReferenceAssert extends GenericAssert<ReferenceAssert, Reference<?>> {

    protected ReferenceAssert(final Reference<?> actual) {
        super(ReferenceAssert.class, actual);
    }

    public static ReferenceAssert assertThat(final Reference<?> actual) {
        return new ReferenceAssert(actual);
    }

    public ReferenceAssert hasId(final String id) {
        Assertions.assertThat(actual.getId()).isEqualTo(id);
        return this;
    }
}