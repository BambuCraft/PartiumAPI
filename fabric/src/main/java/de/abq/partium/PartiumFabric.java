package de.abq.partium;

import de.abq.partium.common.data_components.PartiumDataComponents;
import de.abq.partium.common.item.ZItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class PartiumFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Partium.init();

        bindItems( ZItems::registerItems );
        bindDataComponents( PartiumDataComponents::register );
        FabricLoader.getInstance().getModContainer(Partium.MOD_ID).ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Partium.path("test_resources"),
                    container,
                    Component.translatable("resourcePack.partium.test_resources.name"),
                    ResourcePackActivationType.NORMAL);
        });
    }

    private void bindItems(Consumer<BiConsumer<Item, ResourceLocation>> source){
        source.accept( (t, rl) ->{
            //TODO: Add to inv
            Registry.register(BuiltInRegistries.ITEM, rl, t);
        });
    }

    private void bindDataComponents(Consumer<BiConsumer<ResourceLocation, DataComponentType<?>>> source){
        source.accept( (rl, t) ->{
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, rl, t);
        });
    }
}
