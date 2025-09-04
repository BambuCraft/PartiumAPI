package de.abq.partium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.abq.partium.Partium;
import de.abq.partium.client.model.DynamicItemModel;
import de.abq.partium.common.data_components.parts.BladePart;
import de.abq.partium.common.item.PartiumSwordItem;
import de.abq.partium.util.CheckedResourceLocation;
import de.abq.partium.util.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.Color;

import java.util.*;

public class BladeRenderLayer extends ModelRenderLayer<PartiumSwordItem>{
    private Map<String, BladePart> blades = null;
    private List<GeoBone> bones = new ArrayList<>();
    private Vector3f emitterLocation;
    private boolean shouldRender = true;

    private int primaryInnerColor = -1;
    private int primaryOuterColor = -1;

    public BladeRenderLayer(SwordRenderer entityRendererIn) {
        super(entityRendererIn);
        this.emitterLocation = new Vector3f();
    }

    @Override
    public void renderForBone(PoseStack poseStack, PartiumSwordItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!shouldRender || blades == null) return;
        for ( GeoBone blade_joint : this.bones ){

            /* TODO: factor `bladeData` out to global variables.
             *   Change only on slot change.
             *   Global variables for main- and offhand
             *   Convert color when reading to int
             */

            BladePart bladeData = BladePart.getByString(blades, blade_joint.getName());
            if (bladeData == null) continue;

            //TODO: Render both if model is not empty
            if (bladeData.model() == null || bladeData.model().getPath().isBlank() || bladeData.model().getNamespace().isBlank()){
                primaryInnerColor = bladeData.plasmaBlade().innerColor();
                boolean isBladeFineCut = bladeData.plasmaBlade().fineCut();
                boolean isBladeCracked = bladeData.plasmaBlade().cracked(); //TODO: Not handled yet (prob, tessellation)

                Tuple<MultiBufferSource, PoseStack> blade = LightsaberBladeRenderHelper.render(
                        bufferSource, poseStack, blade_joint, bladeData.plasmaBlade().length() * 2,
                        primaryInnerColor, this.emitterLocation, this.parentScale, isBladeFineCut, isBladeCracked
                );

                bufferSource = blade.getA();
                poseStack = blade.getB();
            } else {
                var a = CheckedResourceLocation.exists(bladeData.model());
                Partium.LOG.info("should render blade {} @ {} ? {}",bladeData.model(), blade_joint.getName(),a );

                setScale(bladeData.scale());
                setResource(bladeData.model());
                setRetryWholeDraw(true);
                renderModel(poseStack, animatable, blade_joint, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            }
        }
        setBones(new ArrayList<>());
        super.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void renderModel(PoseStack poseStack, PartiumSwordItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!retryWholeDraw || resource == null ||  resource == Util.EMPTY_RESOURCE_LOCATION || resource.equals(ResourceLocation.fromNamespaceAndPath("minecraft", ""))) return;
        if (bone.getParent() == null || !bone.getParent().getName().equals("joint_blade")) return;

        SwordRenderer swordRenderer = ((SwordRenderer) this.getRenderer());
        Vector3f translate = swordRenderer.getBladeEmitterLocation();
        translate.add(new Vector3f(
                bone.getPivotX(),
                bone.getPivotY(),
                bone.getPivotZ())
        );

        DynamicItemModel<PartiumSwordItem> dynModel = new DynamicItemModel<>(resource);
        ResourceLocation dynResource = dynModel.getModelResource(animatable);
        if (!CheckedResourceLocation.exists(dynResource)) return;
        BakedGeoModel bakedGeoModel = dynModel.getBakedModel(dynResource);

        poseStack.pushPose();
        poseStack.translate(
                translate.x / 16.0,
                translate.y / 16.0,
                translate.z / 16.0
        );

        poseStack.scale(scale, scale, scale);
        poseStack.rotateAround(new Quaternionf().rotationXYZ(bone.getRotX(), bone.getRotY(), bone.getRotZ()), bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());

        if (CheckedResourceLocation.exists(dynModel.getTextureResource(animatable))) {
            renderType = RenderType.entityTranslucent(dynModel.getTextureResource(animatable));
        }

        this.getRenderer().reRender(bakedGeoModel, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, Color.WHITE.argbInt());
        poseStack.popPose();
    }

    public Map<String, BladePart> getBlades() {
        return blades;
    }
    public void setBlades(Map<String, BladePart> blades) {
        this.blades = blades;
    }

    public Vector3f getEmitterLocation() {
        return emitterLocation;
    }
    public void setEmitterLocation(Vector3f emitterLocation) {
        this.emitterLocation.set(emitterLocation);
    }

    public List<GeoBone> getBones() {
        return bones;
    }
    public void setBones(List<GeoBone> bones) {
        this.bones = bones;
    }
    public void pushBones(List<GeoBone> bones){
        this.bones.addAll(bones);
    }
    public void pushBones(GeoBone bone){
        this.bones.add(bone);
    }
    public void pushBonesChecked(List<GeoBone> bones){
        if (!new HashSet<>(this.bones).containsAll(bones)) pushBones(bones);
    }
    public void pushBonesChecked(GeoBone bone){
        if (!this.bones.contains(bone)) pushBones(bone);
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public int getPrimaryInnerColor(){
        return this.primaryInnerColor;
    }
    public int getPrimaryOuterColor(){
        return this.primaryOuterColor;
    }
}
