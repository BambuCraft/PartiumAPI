package de.abq.partium.common.item;

import de.abq.partium.client.renderer.SwordRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.function.Consumer;

/**
 * This interface implements the default {@link de.abq.partium.client.renderer.SwordRenderer}.
 * <p>
 * When registering your item you will need to call {@code .component(PartiumDataComponents.SWORD_PARTS, PartsComponents.DEFAULT)} on the properties.
 * <p>
 * <p>
 * <strong>Examples</strong> can be found in {@link de.abq.partium.common.item.ZItems}
 */
public interface IPartiumItem extends GeoItem {
    @Override
    default void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private SwordRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new SwordRenderer();
                return this.renderer;
            }
        });
    }
}