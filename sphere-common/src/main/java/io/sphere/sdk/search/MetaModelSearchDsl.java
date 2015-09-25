package io.sphere.sdk.search;

import io.sphere.sdk.expansion.ExpansionPath;
import io.sphere.sdk.expansion.MetaModelExpansionDsl;
import io.sphere.sdk.models.LocalizedStringEntry;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 *
 * @param <T> type of the search result
 * @param <C> type of the class implementing this class
 * @param <S> type of the search model
 * @param <E> type of the expansion model
 */
public interface MetaModelSearchDsl<T, C extends MetaModelSearchDsl<T, C, S, E>, S, E> extends EntitySearch<T>, SearchDsl<T, C>, MetaModelExpansionDsl<T, C, E> {

    @Override
    C withText(final LocalizedStringEntry text);

    @Override
    C withText(final Locale locale, final String text);

    @Override
    C withFacets(final List<FacetExpression<T>> facets);

    @Override
    C withFacets(final FacetExpression<T> facet);

    C withFacets(final Function<S, FacetExpression<T>> m);

    @Override
    C plusFacets(final List<FacetExpression<T>> facets);

    @Override
    C plusFacets(final FacetExpression<T> facet);

    C plusFacets(final Function<S, FacetExpression<T>> m);

    @Override
    C withResultFilters(final List<FilterExpression<T>> resultFilters);

    C withResultFilters(final Function<S, List<FilterExpression<T>>> m);

    @Override
    C plusResultFilters(final List<FilterExpression<T>> resultFilters);

    C plusResultFilters(final Function<S, List<FilterExpression<T>>> m);

    @Override
    C withQueryFilters(final List<FilterExpression<T>> queryFilters);

    C withQueryFilters(final Function<S, List<FilterExpression<T>>> m);

    @Override
    C plusQueryFilters(final List<FilterExpression<T>> queryFilters);

    C plusQueryFilters(final Function<S, List<FilterExpression<T>>> m);

    @Override
    C withFacetFilters(final List<FilterExpression<T>> facetFilters);

    C withFacetFilters(final Function<S, List<FilterExpression<T>>> m);

    @Override
    C plusFacetFilters(final List<FilterExpression<T>> facetFilters);

    C plusFacetFilters(final Function<S, List<FilterExpression<T>>> m);

    @Override
    C plusFacetedSearch(final FacetedSearchExpression<T> facetedSearchExpression);

    C plusFacetedSearch(final Function<S, FacetedSearchExpression<T>> m);

    @Override
    C withSort(final SortExpression<T> sort);

    C withSort(final Function<S, SortExpression<T>> m);

    @Override
    C withLimit(final long limit);

    @Override
    C withOffset(final long offset);

    @Override
    C withExpansionPaths(final Function<E, ExpansionPath<T>> m);

    @Override
    C plusExpansionPaths(final Function<E, ExpansionPath<T>> m);
}
