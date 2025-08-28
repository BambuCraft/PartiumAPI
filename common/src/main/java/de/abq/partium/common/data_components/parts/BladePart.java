package de.abq.partium.common.data_components.parts;

import com.mojang.datafixers.util.Either;
import de.abq.partium.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record BladePart(
    ResourceLocation model,
    float scale,
    PlasmaBlade plasmaBlade
    ){

    public record PlasmaBlade (
            float length,
            int outerColor,
            int innerColor,
            boolean fineCut,
            boolean cracked
    ){}

    public static Map<String, BladePart> into( Map<String, Either<BladesPartComponent.Blade, BladesPartComponent.PlasmaBlade>> component ){
        HashMap<String, BladePart> ret = new HashMap<>();
        component.forEach((key, value) -> {
            var left = value.left();
            var right = value.right();
            BladePart bladePart = null;
            if (left.isPresent()) {
                BladesPartComponent.Blade blade = left.get();
                int outerColor = Util.HexStringToIntARGB(blade.plasmaBlade().outerColor());
                int innerColor = Util.HexStringToIntARGB(blade.plasmaBlade().innerColor());

                PlasmaBlade plasmaBlade = new PlasmaBlade(blade.plasmaBlade().length(), outerColor, innerColor, blade.plasmaBlade().fine_cut() , blade.plasmaBlade().cracked());
                bladePart = new BladePart(blade.model(), blade.scale(), plasmaBlade);
            } else if (right.isPresent()) {
                BladesPartComponent.PlasmaBlade blade = right.get();
                int outerColor = Util.HexStringToIntARGB(blade.outerColor());
                int innerColor = Util.HexStringToIntARGB(blade.innerColor());

                PlasmaBlade plasmaBlade = new PlasmaBlade(blade.length(), outerColor, innerColor, blade.fine_cut() , blade.cracked());
                bladePart = new BladePart(null, 0, plasmaBlade);
            }
            if (bladePart == null) return;
            ret.put(key, bladePart);
        });
        return ret;
    }
    public static BladePart getByString(Map<String, BladePart> self, String id){
        return self.get(id);
    }
}