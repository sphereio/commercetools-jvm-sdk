package de.commercetools.internal;

import de.commercetools.sphere.client.FacetDefinition;
import de.commercetools.sphere.client.QueryParam;
import static de.commercetools.internal.util.QueryStringConstruction.*;

import java.util.List;
import java.util.Map;


/** Definition of a facet that matches on a custom attribute. */
public abstract class FacetDefinitionBase<T> implements FacetDefinition<T> {
    /** Name of the application-level query parameter for this facet. */
    protected String queryParam;
    /** Backend name of the custom attribute. */
    protected String attribute;
    /** The attribute on which this facet matches and aggregates counts. */
    public String getAttributeName() {
        return attribute;
    }
    /** If true, only one value can be selected by user at a time. */
    protected boolean isSingleSelect = false;

    /** Creates a new instance of facet definition. */
    protected FacetDefinitionBase(String attribute) {
        this.attribute = attribute;
        this.queryParam = attribute;
    }

    /** {@inheritDoc} */
    public abstract List<QueryParam> getUrlParams(T item);
    /** {@inheritDoc} */
    @Override public final String getSelectLink(T item, Map<String, String[]> queryParams) {
        if (isSingleSelect) {
            // If single select, remove all existing query params for this facet.
            List<QueryParam> itemUrlParams = getUrlParams(item);
            return toQueryString(addURLParams(clearParams(queryParams, itemUrlParams), itemUrlParams));
        } else {
            return toQueryString(addURLParams(queryParams, getUrlParams(item)));
        }
    }
    /** {@inheritDoc} */
    @Override public final String getUnselectLink(T item, Map<String, String[]> queryParams) {
        return toQueryString(removeURLParams(queryParams, getUrlParams(item)));
    }
    /** {@inheritDoc} */
    @Override public final boolean isSelected(T item, Map<String, String[]> queryParams) {
        return containsAllURLParams(queryParams, getUrlParams(item));
    }
}