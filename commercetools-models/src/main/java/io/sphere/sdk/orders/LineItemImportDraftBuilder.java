package io.sphere.sdk.orders;

import io.sphere.sdk.carts.ItemState;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.models.*;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.taxcategories.TaxRate;
import io.sphere.sdk.types.CustomFieldsDraft;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public final class LineItemImportDraftBuilder extends Base implements Builder<LineItemImportDraft> {
    @Nullable
    private final String productId;
    private final LocalizedString name;
    private final ProductVariantImportDraft variant;
    private Price price;
    private Long quantity;
    @Nullable
    private Set<ItemState> state;
    @Nullable
    private Reference<Channel> supplyChannel;
    @Nullable
    private TaxRate taxRate;
    @Nullable
    private CustomFieldsDraft custom;

    private LineItemImportDraftBuilder(final ProductVariantImportDraft variant, final Long quantity, final Price price, @Nullable final String productId, final LocalizedString name) {
        this.price = price;
        this.variant = variant;
        this.quantity = quantity;
        this.productId = productId;
        this.name = name;
    }

    public LineItemImportDraftBuilder price(final Price price) {
        this.price = price;
        return this;
    }

    public LineItemImportDraftBuilder quantity(final Long quantity) {
        this.quantity = quantity;
        return this;
    }

    public LineItemImportDraftBuilder state(@Nullable final Set<ItemState> state) {
        this.state = state;
        return this;
    }

    public LineItemImportDraftBuilder supplyChannel(@Nullable final Referenceable<Channel> supplyChannel) {
        this.supplyChannel = Optional.ofNullable(supplyChannel).map(Referenceable::toReference).orElse(null);
        return this;
    }

    public LineItemImportDraftBuilder taxRate(@Nullable final TaxRate taxRate) {
        this.taxRate = taxRate;
        return this;
    }

    public LineItemImportDraftBuilder custom(@Nullable final CustomFieldsDraft custom) {
        this.custom = custom;
        return this;
    }

    public static LineItemImportDraftBuilder of(final ProductVariantImportDraft variant, final long quantity, final Price price, final LocalizedString name) {
        return new LineItemImportDraftBuilder(variant, quantity, price, variant.getProductId(), name);
    }

    @Override
    public LineItemImportDraft build() {
        return new LineItemImportDraftImpl(name, productId, variant, price, quantity, state, supplyChannel, taxRate, custom);
    }
}
