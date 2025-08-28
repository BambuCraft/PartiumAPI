package de.abq.partium.common.data_components.parts;

import net.minecraft.resources.ResourceLocation;

public record BladePart(
    ResourceLocation model,
    float scale,
    PlasmaBlade blade
    ){
    record PlasmaBlade (
            float length,
            int outerColor,
            int innerColor,
            boolean fineCut,
            boolean cracked
    ){}
}