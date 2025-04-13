package de.abq.partium.common.data_components.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.abq.partium.util.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record BladesPart(Blade primary, Blade secondary, Blade tertiary){
    public static final BladesPart DEFAULT = new BladesPart(Blade.DEFAULT, Blade.DEFAULT, Blade.DEFAULT);
    public static Codec<BladesPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Blade.CODEC.optionalFieldOf("primary", Blade.DEFAULT).forGetter(BladesPart::primary),
                    Blade.CODEC.optionalFieldOf("secondary", Blade.DEFAULT).forGetter(BladesPart::secondary),
                    Blade.CODEC.optionalFieldOf("tertiary", Blade.DEFAULT).forGetter(BladesPart::tertiary)
            ).apply(instance, BladesPart::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BladesPart> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Blade.CODEC), BladesPart::primary,
            ByteBufCodecs.fromCodec(Blade.CODEC), BladesPart::secondary,
            ByteBufCodecs.fromCodec(Blade.CODEC), BladesPart::tertiary,
            BladesPart::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BladesPart> UNIT_STREAM_CODEC = StreamCodec.unit(DEFAULT);

    public record Blade(float length, String innerColor, String outerColor, ResourceLocation model, boolean fine_cut, boolean cracked){
        public static final Blade DEFAULT = new Blade(16, "#ffffff","#000000", Util.EMPTY_RESOURCE_LOCATION, false, false);

        public static Codec<Blade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("length").forGetter(Blade::length),
                Codec.STRING.fieldOf("innerColor").forGetter(Blade::innerColor),
                Codec.STRING.fieldOf("outerColor").forGetter(Blade::outerColor),
                ResourceLocation.CODEC.optionalFieldOf("model", Util.EMPTY_RESOURCE_LOCATION).forGetter(Blade::model),
                Codec.BOOL.optionalFieldOf("fineCut", false).forGetter(Blade::fine_cut),
                Codec.BOOL.optionalFieldOf("cracked", false).forGetter(Blade::cracked)
             ).apply(instance, Blade::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Blade> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Blade::length,
                ByteBufCodecs.STRING_UTF8, Blade::innerColor,
                ByteBufCodecs.STRING_UTF8, Blade::outerColor,
                ByteBufCodecs.fromCodec(ResourceLocation.CODEC), Blade::model,
                ByteBufCodecs.BOOL, Blade::fine_cut,
                ByteBufCodecs.BOOL, Blade::cracked,
                Blade::new
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Blade> UNIT_STREAM_CODEC = StreamCodec.unit(DEFAULT);
    }

    public Optional<Blade> getByString(String match){
        if (match.toLowerCase().startsWith("primary")){
            return Optional.ofNullable(this.primary);
        } else if (match.toLowerCase().startsWith("secondary")) {
            if (this.secondary == null) return getByString("primary");
            return Optional.of(this.secondary);
        } else if (match.toLowerCase().startsWith("tertiary")) {
            if (this.tertiary == null) return getByString("secondary");
            return Optional.of(this.tertiary);
        } else {
            return Optional.empty();
        }
    }
}
