package de.abq.partium;

import de.abq.partium.client.model.DynamicItemModel;
import de.abq.partium.common.item.PartiumSwordItem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Partium {
    public static final String MOD_ID = "partium";
    public static final String MOD_NAME = "PartiumAPI";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static HashMap<ResourceLocation, DynamicItemModel<?>> known_models = new HashMap<>();

    public static void init() {}

    public static ResourceLocation path(String location) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, location);
    }

    public static final ResourceLocation LIGHTSABER_POST_SHADER = Partium.path("lightsaber_post");

    public static void commonClientSetup(){
        //Partium.commonVeilSetup();
    }

    //BROKEN: Run to enable lightsaber effects
    public static void commonVeilSetup(){
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, deltaTracker,camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL){
                Minecraft instance = Minecraft.getInstance();
                LocalPlayer player = instance.player;
                if (player == null) return;
                ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
                ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
                PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
                PostPipeline pipeline = postProcessingManager.getPipeline(LIGHTSABER_POST_SHADER);
                assert pipeline != null;
                int isFirstPerson = camera.isDetached() ? 0 : 1;
                pipeline.getUniformSafe("u_IsFirstPerson").setInt(isFirstPerson);
                if (main.getItem() instanceof PartiumSwordItem || off.getItem() instanceof PartiumSwordItem){
                    if (!postProcessingManager.isActive(LIGHTSABER_POST_SHADER))
                        postProcessingManager.add(LIGHTSABER_POST_SHADER);
                } else if (postProcessingManager.isActive(LIGHTSABER_POST_SHADER)){
                    postProcessingManager.remove(LIGHTSABER_POST_SHADER);
                }
            }
        });
    }
}