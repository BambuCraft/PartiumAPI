package de.abq.partium.common.data_components;

import de.abq.partium.Partium;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class ELDataComponents {

    public static final DataComponentType<PartsComponents> SWORD_PARTS = DataComponentType.<PartsComponents>builder()
                    // The codec to read/write the data to disk
                    .persistent(PartsComponents.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(PartsComponents.STREAM_CODEC)
                    .build();

    public static void register( BiConsumer<ResourceLocation, DataComponentType<?>> register) {
        register.accept(ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "parts"), SWORD_PARTS);
    }
}
