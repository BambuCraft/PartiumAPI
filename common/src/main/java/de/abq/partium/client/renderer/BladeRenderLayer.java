package de.abq.partium.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.abq.partium.Partium;
import de.abq.partium.common.data_components.parts.BladesPart;
import de.abq.partium.common.item.PartiumSwordItem;
import de.abq.partium.render_type.ZRenderTypes;
import de.abq.partium.util.Util;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.*;

import java.util.*;

public class BladeRenderLayer extends ModelRenderLayer<PartiumSwordItem>{
    private BladesPart blades = null;
    private List<GeoBone> bones = new ArrayList<>();
    private Vector3f emitterLocation;
    private boolean shouldRender = true;

    private boolean isBladeCracked = false;
    private boolean isBladeFineCut = false;

    private int primaryInnerColor = -1;
    private int primaryOuterColor = -1;

    public BladeRenderLayer(SwordRenderer entityRendererIn) {
        super(entityRendererIn);
        this.emitterLocation = new Vector3f();
    }

    @Override
    public void renderForBone(PoseStack poseStack, PartiumSwordItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!shouldRender || blades == null) return;
        for (GeoBone blade_joint : this.bones) {
            Optional<BladesPart.Blade> bladeDataOpt = blades.getByString(blade_joint.getName());
            if (bladeDataOpt.isEmpty()) continue;
            BladesPart.Blade bladeData = bladeDataOpt.get();
            isBladeFineCut = bladeData.fine_cut();
            isBladeCracked = bladeData.cracked();

            if (primaryInnerColor == -1 && blade_joint.getName().equals("primary"))
                primaryInnerColor = Util.HexStringToIntARGB(bladeData.innerColor());
            if (primaryOuterColor == -1 && blade_joint.getName().equals("primary"))
                primaryOuterColor = Util.HexStringToIntARGB(bladeData.outerColor());

            if (bladeData.model().getPath().isBlank() || bladeData.model().getNamespace().isBlank()) {
                Tuple<MultiBufferSource, PoseStack> blade = renderLightsaberBlade(animatable, poseStack, bufferSource, blade_joint, bladeData.length() * 2, Util.HexStringToIntARGB(bladeData.outerColor(), 0x88), Util.HexStringToIntARGB(bladeData.innerColor(), 0xff));
                if (blade == null) return;
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

    @Nullable
    private Tuple<MultiBufferSource, PoseStack> renderLightsaberBlade(PartiumSwordItem animatable, PoseStack poseStack, MultiBufferSource bufferSource, GeoBone blade_joint, float completeBladeLength, int outerColor, int innerColor){

        RenderType renderType = ZRenderTypes.lightsaber_blade();
        if (renderType == null) {
            Partium.LOG.error("RenderType == null");
            return null;
        }
        renderType.setupRenderState();
        ShaderProgram shader = VeilRenderSystem.getShader();
        if (shader == null) {
            Partium.LOG.error("shader == null\n\t {}",renderType);
            renderType.clearRenderState();
        }

        poseStack.pushPose();
        poseStack.translate(
                (this.emitterLocation.x+blade_joint.getPivotX())/(16+this.parentScale*0.8f),
                (this.emitterLocation.y+blade_joint.getPivotY())/(16+this.parentScale*0.8f),
                (this.emitterLocation.z+blade_joint.getPivotZ())/(16+this.parentScale*0.8f)
        );

        poseStack.mulPose(new Quaternionf().rotationXYZ(
                blade_joint.getRotX(),
                blade_joint.getRotY(),
                blade_joint.getRotZ()));

        poseStack.scale(0.0625f, 0.0625f, 0.0625f);

        Matrix4f matrix = poseStack.last().pose();

        int maxLight = 0xF000F0;
        float u = 0.0f, v = 0.0f;

        float tip_length = 0.73f;
        float inner_blade_thickness = .25f, inner_blade_length = .25f;
        float outer_blade_thickness = inner_blade_thickness * 3.5f, outer_blade_length = inner_blade_length * 3.5f;

        if (isBladeFineCut) {
            tip_length = 1f;
            inner_blade_thickness = .2f;
            inner_blade_length = .75f;
            outer_blade_thickness = inner_blade_thickness * 1.5f;
            outer_blade_length = inner_blade_length * 1.5f;
        }
        float bladeHeight = completeBladeLength - tip_length;
        VertexConsumer outerBuffer = bufferSource.getBuffer(renderType);

        outerBuffer.addVertex(matrix, -outer_blade_thickness, (bladeHeight + 0.01f), outer_blade_length).setColor(outerColor).setUv(u + 0.5f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, (bladeHeight + 0.01f), outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, -0.5f, outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        outerBuffer.addVertex(matrix, -outer_blade_thickness, -0.5f, outer_blade_length).setColor(outerColor).setUv(u + 0.5f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        // Back face
        outerBuffer.addVertex(matrix, -outer_blade_thickness, -0.51f, -outer_blade_length).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, -0.51f, -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, (bladeHeight + 0.01f), -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        outerBuffer.addVertex(matrix, -outer_blade_thickness, (bladeHeight + 0.01f), -outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        // Left face
        outerBuffer.addVertex(matrix, -outer_blade_thickness, (bladeHeight + 0.01f), -outer_blade_length).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_thickness, (bladeHeight + 0.01f), outer_blade_length).setColor(outerColor).setUv(u + 0.5f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_thickness, -0.51f, outer_blade_length).setColor(outerColor).setUv(u + 0.5f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_thickness, -0.51f, -outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        // Right face
        outerBuffer.addVertex(matrix, outer_blade_thickness, (bladeHeight + 0.01f), outer_blade_length).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, (bladeHeight + 0.01f), -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, -0.51f, -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, -0.51f, outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        //Tip front
        outerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length+0.75f, 0f).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, bladeHeight, outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, bladeHeight, outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        outerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length+0.75f, 0f).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, bladeHeight, outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, bladeHeight, -outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        outerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length+0.75f, 0f).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, bladeHeight, -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, bladeHeight, -outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        outerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length+0.75f, 0f).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, bladeHeight, outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, bladeHeight, -outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        //Inner Blade
        BufferBuilder innerBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // Bottom square
        // Front face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        // Back face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        // Left face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        // Right face
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        //Tip front
        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f, 1f, 1f);

        poseStack.popPose();
        return new Tuple<>(bufferSource, poseStack);
    }

    private float calcScaleOffset(float scale){
        if (scale == 1) return 1;
        if (scale == 0) return 0;
        return 1-1f;
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
