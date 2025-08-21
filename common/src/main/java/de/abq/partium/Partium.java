package de.abq.partium;

import com.mojang.authlib.minecraft.client.MinecraftClient;
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
import net.minecraft.world.item.SwordItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class Partium {
    public static final String MOD_ID = "partium";
    public static final String MOD_NAME = "PartiumAPI";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        //PartsRegistry.loadParts(Minecraft.getInstance().getResourceManager());
    }

    public static ResourceLocation path(String location) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, location);
    }

    public static void onCommonRenderHandPost(ItemStack itemStack){
        //Make more common, move to right methods
        //if (!(itemStack.getItem() instanceof PartiumSwordItem) ) return;
        //makeLightsaberBladePost();
    }

    public static final ResourceLocation LIGHTSABER_POST_SHADER = Partium.path("lightsaber_bloom");
    public static void commonClientSetup(){
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, deltaTracker,camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_LEVEL){
                Minecraft instance = Minecraft.getInstance();
                LocalPlayer player = instance.player;
                if (player == null) return;
                ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
                //ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
                PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
                PostPipeline pipeline = postProcessingManager.getPipeline(LIGHTSABER_POST_SHADER);
                assert pipeline != null;
                //pipeline.getUniformSafe("u_OuterColor").setInt(0xffffff);
                if (main.getItem() instanceof PartiumSwordItem){
                    if (!postProcessingManager.isActive(LIGHTSABER_POST_SHADER))
                        postProcessingManager.add(LIGHTSABER_POST_SHADER);
                } else if (postProcessingManager.isActive(LIGHTSABER_POST_SHADER)){
                    postProcessingManager.remove(LIGHTSABER_POST_SHADER);
                }
            }
        });
    }
}