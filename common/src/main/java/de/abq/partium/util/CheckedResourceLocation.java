package de.abq.partium.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.Optional;

public class CheckedResourceLocation {
    public static boolean exists(ResourceLocation resourceLocation) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        return resourceManager.getResource(resourceLocation).isPresent();
    }

    public static Optional<BakedGeoModel> getCheckedBackedModel(GeoModel model, ResourceLocation modelLocation){
        if (exists(modelLocation)) return Optional.of(model.getBakedModel(modelLocation));
        return Optional.empty();

    }
}
