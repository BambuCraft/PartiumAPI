package de.abq.partium.common.data_components.parts;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.abq.partium.util.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record BladesPartComponent() {
    public static final Codec<Either<Blade, SimpleBlade>> CODEC = Codec.either(BladesPartComponent.Blade.CODEC, BladesPartComponent.SimpleBlade.CODEC);

    public record Blade(float scale, ResourceLocation model, PlasmaBlade plasmaBlade){
        public static final Blade DEFAULT = new Blade(1, Util.EMPTY_RESOURCE_LOCATION, null);

        public static final Codec<Blade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("scale", 8f).forGetter(Blade::scale),
                ResourceLocation.CODEC.fieldOf("model").forGetter(Blade::model),
                PlasmaBlade.CODEC.optionalFieldOf("plasmaBlade", null).forGetter(Blade::plasmaBlade)
             ).apply(instance, Blade::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Blade> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Blade::scale,
                ByteBufCodecs.fromCodec(ResourceLocation.CODEC), Blade::model,
                ByteBufCodecs.fromCodec(PlasmaBlade.CODEC), Blade::plasmaBlade,
                Blade::new
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Blade> UNIT_STREAM_CODEC = StreamCodec.unit(DEFAULT);

        public record PlasmaBlade(float length, String innerColor, String outerColor, boolean fine_cut, boolean cracked){
            public static final PlasmaBlade DEFAULT = new PlasmaBlade(16, "#ffffff", "#000000",  false, false);
            public static final Codec<PlasmaBlade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.FLOAT.fieldOf("length").forGetter(PlasmaBlade::length),
                            Codec.STRING.fieldOf("outerColor").forGetter(PlasmaBlade::outerColor),
                            Codec.STRING.optionalFieldOf("innerColor", "#ffffff").forGetter(PlasmaBlade::innerColor),
                            Codec.BOOL.optionalFieldOf("fineCut", false).forGetter(PlasmaBlade::fine_cut),
                            Codec.BOOL.optionalFieldOf("cracked", false).forGetter(PlasmaBlade::cracked)
                    ).apply(instance, PlasmaBlade::new)
            );
            public static final StreamCodec<RegistryFriendlyByteBuf, PlasmaBlade> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.FLOAT, PlasmaBlade::length,
                    ByteBufCodecs.STRING_UTF8, PlasmaBlade::outerColor,
                    ByteBufCodecs.STRING_UTF8, PlasmaBlade::innerColor,
                    ByteBufCodecs.BOOL, PlasmaBlade::fine_cut,
                    ByteBufCodecs.BOOL, PlasmaBlade::cracked,
                    PlasmaBlade::new
            );
            public static final StreamCodec<RegistryFriendlyByteBuf, PlasmaBlade> UNIT_STREAM_CODEC = StreamCodec.unit(DEFAULT);
        }
    }

    public record SimpleBlade(float scale, ResourceLocation model){
        public static final SimpleBlade DEFAULT = new SimpleBlade(16, Util.EMPTY_RESOURCE_LOCATION);

        public static final Codec<SimpleBlade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.FLOAT.fieldOf("scale").forGetter(SimpleBlade::scale),
                        ResourceLocation.CODEC.optionalFieldOf("model", Util.EMPTY_RESOURCE_LOCATION).forGetter(SimpleBlade::model)
                ).apply(instance, SimpleBlade::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleBlade> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, SimpleBlade::scale,
                ByteBufCodecs.fromCodec(ResourceLocation.CODEC), SimpleBlade::model,
                SimpleBlade::new
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleBlade> UNIT_STREAM_CODEC = StreamCodec.unit(DEFAULT);
    }
}
