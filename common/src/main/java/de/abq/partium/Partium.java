package de.abq.partium;

import de.abq.partium.common.item.PartiumSwordItem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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

    public static final ResourceLocation LIGHTSABER_POST_SHADER = Partium.path("lightsaber_blade");
    public static void commonSetup(){

    }

    public static void makeLightsaberBladePost(){
        try {
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            PostPipeline pipeline = postProcessingManager.getPipeline(LIGHTSABER_POST_SHADER);
            postProcessingManager.runPipeline(pipeline);
        } catch (Exception exception){
            Partium.LOG.error("++>", exception);
        }
    }
}