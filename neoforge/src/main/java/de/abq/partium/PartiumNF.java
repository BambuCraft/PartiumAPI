package de.abq.partium;

import de.abq.partium.common.data_components.PartiumDataComponents;
import de.abq.partium.common.data_components.parts.BladesPart;
import de.abq.partium.common.item.PartiumSwordItem;
import de.abq.partium.common.item.ZItems;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderUniformCache;
import foundry.veil.api.client.util.Easing;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(Partium.MOD_ID)
public class PartiumNF {
    public static PostPipeline mirrorPipeline;
    public static ShaderUniformCache.Uniform mirrorOffset;
    public static ShaderUniformCache.Uniform shaderAccent;

    public PartiumNF(IEventBus eventBus) {

        Partium.init();

        eventBus.addListener((RegisterEvent event) -> {
            bindDataComponents( event, PartiumDataComponents::register );
            bindItems( event, ZItems::registerItems );
        });
    }



    public static void makeBlackWholeShaders() {
        try {
            mirrorPipeline.getUniformSafe("mirrorOffset").setFloat(Easing.EASE_OUT_CIRC.ease(0));
            mirrorPipeline.getUniformSafe("shaderAccent").setVector(0xff, 0xff, 0xff);
            VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(mirrorPipeline);
        }catch (Exception ignored){}
    }

    private void bindItems(RegisterEvent event, Consumer<BiConsumer<Item, ResourceLocation>> source){
        if (event.getRegistryKey().equals(Registries.ITEM)){
            source.accept( (t, rl) ->{
                //TODO: Add to inv
                event.register(Registries.ITEM, rl, () -> t);
            });
        }
    }

    private void bindDataComponents(RegisterEvent event, Consumer<BiConsumer<ResourceLocation, DataComponentType<?>>> source){
        if (event.getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)){
            source.accept( (rl, t) ->{
                event.register(Registries.DATA_COMPONENT_TYPE, rl, () -> t);
            });
        }
    }

    @EventBusSubscriber(
            value = Dist.CLIENT,
            bus = EventBusSubscriber.Bus.GAME,
            modid = Partium.MOD_ID
    )
    static class ClientGameEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            mirrorPipeline = postProcessingManager.getPipeline(Partium.path("mirror"));
        }
        @SubscribeEvent
        public static void onRenderLevelStage(RenderLevelStageEvent event){
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
            makeBlackWholeShaders();
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onRenderHandPost(RenderHandEvent event){
            ItemStack itemStack = event.getItemStack();
            if (!(itemStack.getItem() instanceof PartiumSwordItem) ) return;
            var components = itemStack.getComponents();
            if (!components.has(PartiumDataComponents.SWORD_PARTS)) return;
            var sword_comps = components.get(PartiumDataComponents.SWORD_PARTS);
            for (Iterator<BladesPart.Blade> it = BladesPart.iter(sword_comps.blades()); it.hasNext(); ) {
                BladesPart.Blade blade = it.next();
                if (!blade.model().getPath().isEmpty()) return;
                var outer_color = TextColor.parseColor(blade.outerColor()).result().get().getValue();
                makeBlackWholeShaders();
            }
        }
    }
}