package de.abq.partium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.abq.partium.common.data_components.parts.BladesPart;
import de.abq.partium.common.item.PartiumSwordItem;
import de.abq.partium.util.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Tuple;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class BladeRenderLayer extends ModelRenderLayer<PartiumSwordItem>{
    private BladesPart blades = null;
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
        if (!shouldRender && blades == null) return;
        for ( GeoBone blade_joint : this.bones ){

            /* TODO: factor `bladeData` out to global variables.
             *   Change only on slot change.
             *   Global variables for main- and offhand
             *   Convert color when reading to int
             */

            Optional<BladesPart.Blade> bladeDataOpt = blades.getByString(blade_joint.getName());
            if (bladeDataOpt.isEmpty()) continue;
            BladesPart.Blade bladeData = bladeDataOpt.get();

            if (bladeData.model().getPath().isBlank() || bladeData.model().getNamespace().isBlank()){
                primaryInnerColor = Util.HexStringToIntARGB(bladeData.innerColor());

                boolean isBladeFineCut = bladeData.fine_cut();
                boolean isBladeCracked = bladeData.cracked(); //TODO: Not handled yet (prob, tessellation)

                Tuple<MultiBufferSource, PoseStack> blade = LightsaberBladeRenderHelper.render(
                        bufferSource, poseStack, blade_joint, bladeData.length() * 2,
                        primaryInnerColor,
                        this.emitterLocation, this.parentScale, isBladeFineCut, isBladeCracked
                );

                bufferSource = blade.getA();
                poseStack = blade.getB();
            } else {
                setModel(bladeData.model());
                super.renderModel(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            }
        }
        setBones(new ArrayList<>());
        super.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    public BladesPart getBlades() {
        return blades;
    }
    public void setBlades(BladesPart blades) {
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
