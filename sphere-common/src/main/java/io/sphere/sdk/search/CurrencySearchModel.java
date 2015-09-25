package io.sphere.sdk.search;

import javax.annotation.Nullable;
import javax.money.CurrencyUnit;

public class CurrencySearchModel<T, S extends DirectionlessSearchSortModel<T>> extends SortableTermModel<T, S, CurrencyUnit> implements SearchSortModel<T, S> {

    public CurrencySearchModel(@Nullable final SearchModel<T> parent, @Nullable final String pathSegment, final SortBuilder<T, S> sortBuilder) {
        super(parent, pathSegment, sortBuilder);
    }

    @Override
    public FilterSearchModel<T, CurrencyUnit> filtered() {
        return new FilterSearchModel<>(this, null, TypeSerializer.ofCurrency());
    }

    @Override
    public FacetSearchModel<T, CurrencyUnit> faceted() {
        return new FacetSearchModel<>(this, null, TypeSerializer.ofCurrency());
    }

    @Override
    public FacetedSearchModel<T> facetedSearch() {
        return super.facetedSearch();
    }

    @Override
    public S sorted() {
        return sortBuilder.apply(this);
    }
}