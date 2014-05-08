package io.sphere.sdk.client;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class RenameMeTestDouble implements RenameMe {
    @Override
    public final <T> ListenableFuture<Optional<T>> execute(final Fetch<T> fetch) {
        return Futures.immediateFuture(result(fetch));
    }

    public abstract <T> Optional<T> result(final Fetch<T> fetch);

    @Override
    public <T> ListenableFuture<PagedQueryResult<T>> execute(Query<T> query) {
        return null;//TODO implement
    }
}