package de.abq.partium.common.data_components.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.abq.partium.Partium;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ModelPart(ResourceLocation model, float scale){
    public static final ModelPart DEFAULT = new ModelPart(ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID,"test_grip"), 1.0f);

    public static Codec<ModelPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("model").forGetter(ModelPart::model),
                    Codec.FLOAT.optionalFieldOf("scale", 1f).forGetter(ModelPart::scale)
            ).apply(instance, ModelPart::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ModelPart> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(ResourceLocation.CODEC), ModelPart::model,
            ByteBufCodecs.FLOAT, ModelPart::scale,
            ModelPart::new
    );
    // Unit stream codec if nothing should be sent across the network
    public static final StreamCodec<RegistryFriendlyByteBuf, ModelPart> UNIT_STREAM_CODEC = StreamCodec.unit(DEFAULT);
}
