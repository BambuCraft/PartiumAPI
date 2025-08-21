package de.abq.partium.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.abq.partium.Partium;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniform;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;

public class LightsaberBladeRenderHelper {
    private static final ResourceLocation BLADE_SHADER_ID = Partium.path("lightsaber");

    static Tuple<MultiBufferSource, PoseStack> render(
            MultiBufferSource bufferSource, PoseStack poseStack, GeoBone blade_joint,
            float completeBladeLength, int outerColor, int innerColor,
            Vector3f emitterLocation, float parentScale, boolean isBladeFineCut, boolean isBladeCracked
    ){
        poseStack.pushPose();
        poseStack.translate(
                (emitterLocation.x+blade_joint.getPivotX())/(16+parentScale*0.8f),
                (emitterLocation.y+blade_joint.getPivotY())/(16+parentScale*0.8f),
                (emitterLocation.z+blade_joint.getPivotZ())/(16+parentScale*0.8f)
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
        //float outer_blade_thickness = inner_blade_thickness * 3.5f, outer_blade_length = inner_blade_length * 3.5f;

        if (isBladeFineCut) {
            tip_length = 1f;
            inner_blade_thickness = .2f;
            inner_blade_length = .75f;
            //outer_blade_thickness = inner_blade_thickness * 1.5f;
            //outer_blade_length = inner_blade_length * 1.5f;
        }
        float bladeHeight = completeBladeLength - tip_length;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.enableDepthTest();
        /*
        //Outer Blade
        VertexConsumer outerBuffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "textures/misc/lightsaber_blade_glow.png")));
        // Front face
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

        /* // Top face
        outerBuffer.addVertex(matrix, -outer_blade_length, (bladeHeight + 0.01f), outer_blade_length).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, (bladeHeight + 0.01f), outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, (bladeHeight + 0.01f), -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, (bladeHeight + 0.01f), -outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

        // Bottom face
        outerBuffer.addVertex(matrix, -outer_blade_length, -0.51f, -outer_blade_length).setColor(outerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, -0.51f, -outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, outer_blade_thickness, -0.51f, outer_blade_length).setColor(outerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);
        outerBuffer.addVertex(matrix, -outer_blade_length, -0.5f, outer_blade_length).setColor(outerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight).setNormal(1f,1f,1f);

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
**/

        //Inner Blade

        //VertexConsumer innerBuffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "textures/misc/lightsaber_blade.png"), false));
        RenderType veilType = VeilRenderType.get(BLADE_SHADER_ID, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "textures/misc/lightsaber_blade.png"));
        if (veilType == null) {
            Partium.LOG.error("Error loading RenderType {}", BLADE_SHADER_ID);
            return new Tuple<>(bufferSource, poseStack);
        }
        ShaderProgram shader = VeilRenderSystem.setShader(BLADE_SHADER_ID);
        if (shader == null) {
            Partium.LOG.error("Error loading Shader {}", BLADE_SHADER_ID);
            return new Tuple<>(bufferSource, poseStack);
        }
        ShaderUniform uInnerColor =  shader.getUniform("u_InnerColor");
        if (uInnerColor == null) {
            Partium.LOG.error("Error getting uniform: u_InnerColor");
        } else {
            uInnerColor.setInt(innerColor);
        }
        //TODO: Find out how I can supply multiple uniforms.
        //TODO: create a framebuffer and create a bloom effect for the lightsaber.
        shader.bind();
        VertexConsumer innerBuffer = bufferSource.getBuffer(veilType);
        // Bottom square
        /*
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(maxLight);
        */

        // Front face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        // Back face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        // Left face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        // Right face
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, -0.5f, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        /*
        // Top face
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v).setLight(maxLight);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight);
        */

        //Tip front
        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u + 1.0f, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v + 1.0f).setLight(maxLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1f,1f,1f);

        innerBuffer.addVertex(matrix, 0f, bladeHeight + tip_length, 0f).setColor(innerColor).setUv(u, v).setLight(maxLight).setNormal(1f,1f,1f).setOverlay(OverlayTexture.NO_OVERLAY);
        innerBuffer.addVertex(matrix, inner_blade_thickness, bladeHeight, inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight).setNormal(1f,1f,1f).setOverlay(OverlayTexture.NO_OVERLAY);
        innerBuffer.addVertex(matrix, -inner_blade_thickness, bladeHeight, -inner_blade_length).setColor(innerColor).setUv(u, v).setLight(maxLight).setNormal(1f,1f,1f).setOverlay(OverlayTexture.NO_OVERLAY);
        //Partium.makeLightsaberBladePost();
        ShaderProgram.unbind();
        poseStack.popPose();
        return new Tuple<>(bufferSource, poseStack);
    }
}
