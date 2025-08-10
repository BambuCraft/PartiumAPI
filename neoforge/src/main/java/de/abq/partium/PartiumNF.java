package de.abq.partium;

import de.abq.partium.common.data_components.PartiumDataComponents;
import de.abq.partium.common.item.ZItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(Partium.MOD_ID)
public class PartiumNF {
    public PartiumNF(IEventBus eventBus) {

        Partium.init();

        eventBus.addListener((RegisterEvent event) -> {
            bindDataComponents( event, PartiumDataComponents::register );
            bindItems( event, ZItems::registerItems );
        });
        eventBus.addListener((FMLClientSetupEvent event) -> {
            Partium.commonClientSetup();
        });
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
     static class ClientGameEvents{
         @SubscribeEvent(priority = EventPriority.HIGHEST)
         public static void onRenderHandPost(RenderHandEvent event){
             Partium.onCommonRenderHandPost(event.getItemStack());
         }
    }
}