package de.abq.partium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.abq.partium.Partium;
import de.abq.partium.client.model.DynamicItemModel;
import de.abq.partium.common.item.PartiumAxeItem;
import de.abq.partium.util.CheckedResourceLocation;
import de.abq.partium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

import java.util.Objects;
import java.util.Optional;

public class ModelRenderLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    ResourceLocation resource;
    float scale;
    protected float parentScale;
    private String joint_name;
    boolean retryWholeDraw = true;
    private boolean retryTextureDraw = true;
    private boolean isGUIRender = false;

    public ModelRenderLayer(GeoRenderer<T> renderer, String joint_name) {
        super(renderer);
        this.joint_name = joint_name;
    }
    public ModelRenderLayer(GeoRenderer<T> renderer) {
        super(renderer);
        this.joint_name = "";
    }

    @Override
    public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        renderModel(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        super.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    public void renderModel(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay){
        if (retryWholeDraw && resource != Util.EMPTY_RESOURCE_LOCATION && !Objects.equals(resource, ResourceLocation.fromNamespaceAndPath("minecraft", ""))) {
            if (bone.getName().equals("joint_"+joint_name)) {

                if (!Partium.known_models.containsKey(resource)) {
                    DynamicItemModel<T> tmpModel = new DynamicItemModel<>(resource);
                    Partium.known_models.put(resource, tmpModel);
                }
                if (Partium.known_models.get(resource) == null) return;

                DynamicItemModel<T> dynModel = (DynamicItemModel<T>) Partium.known_models.get(resource);
                ResourceLocation dynResource = dynModel.getModelResource(animatable);

                if (!CheckedResourceLocation.exists(dynResource)) {
                    //TODO: make configurable
                    if (Partium.known_models.get(resource) != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("[WARNING] "+ dynResource + " is not available, you mite need the correct resourcepack to use this part"));
                        Partium.known_models.replace(resource, null);
                    }
                    return;
                }

                BakedGeoModel bakedGeoModel = dynModel.getBakedModel(dynResource);
                Optional<GeoBone> additionalBoneOpt = bakedGeoModel.getBone(joint_name);
                if (additionalBoneOpt.isEmpty()) return;
                GeoBone additionalBone = additionalBoneOpt.get();

                poseStack.pushPose();
                Vector3f translate = new Vector3f(
                        (bone.getPivotX() - additionalBone.getPivotX() * scale),
                        (bone.getPivotY() - additionalBone.getPivotY() * scale),
                        (bone.getPivotZ() - additionalBone.getPivotZ() * scale)
                );
                poseStack.translate(
                        translate.x/16.0,
                        translate.y/16.0,
                        translate.z/16.0
                );

                poseStack.scale(scale, scale, scale);

                poseStack.rotateAround(new Quaternionf().rotationXYZ(bone.getRotX(), bone.getRotY(), bone.getRotZ()), bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());

                if (CheckedResourceLocation.exists(dynModel.getTextureResource(animatable))) {
                    renderType = RenderType.entityTranslucent(dynModel.getTextureResource(animatable));
                }

                this.getRenderer().reRender(bakedGeoModel, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
                poseStack.popPose();

                if (bakedGeoModel.getBone("joint_blade").isPresent() && !isGUIRender)
                    sendBladeJoints(translate, bakedGeoModel.getBone("joint_blade").get());

            }
        }
    }

    private void sendBladeJoints(Vector3f emitter, GeoBone bladeBone){
        SwordRenderer swordRenderer = ((SwordRenderer) this.getRenderer());
        swordRenderer.setBladeEmitterLocation( emitter );
        for (GeoBone emitterBone : bladeBone.getChildBones()) {
            swordRenderer.pushBladeJointsChecked(emitterBone);
            swordRenderer.setBladeParentScale(scale);
        }
    }

    public ResourceLocation getResource() {
        return resource;
    }
    public void setResource(ResourceLocation resource) {
        this.resource = resource;
    }
    public void setJointName(String name){
        this.joint_name = name;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
    public void setRetryWholeDraw(boolean retryWholeDraw) {
        this.retryWholeDraw = retryWholeDraw;
    }

    public void setRetryTextureDraw(boolean retryTextureDraw) {
        this.retryTextureDraw = retryTextureDraw;
    }
    public void setRetry(boolean retry){
        this.setRetryTextureDraw(retry);
        this.setRetryWholeDraw(retry);
    }
    public void setParentScale(float scale) {
        this.parentScale = scale;
    }

    public void setGUIRender(boolean GUIRender) {
        isGUIRender = GUIRender;
    }
}
