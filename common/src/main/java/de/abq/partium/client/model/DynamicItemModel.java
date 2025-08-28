package de.abq.partium.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class DynamicItemModel<T extends GeoAnimatable> extends GeoModel<T> {
    private ResourceLocation resourceLocation;
    public DynamicItemModel(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "geo/partium/" + resourceLocation.getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "textures/partium/" + resourceLocation.getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "animations/partium/" + resourceLocation.getPath() + ".animation.json");
    }

    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getBasicResourceLocation(){
        return this.resourceLocation;
    }
}
