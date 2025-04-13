package de.abq.partium.common.block;

import de.abq.partium.Partium;
import de.abq.partium.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiConsumer;

public class ZSBlocks {

    public static final Block swordPartBuilderBlock = new PartBuilderBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(3).noOcclusion());
    public static final ResourceLocation SWORD_PART_BUILDER = ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "sword_part_builder.png");

    public static void registerBlocks(BiConsumer<Block, ResourceLocation> register){
        register.accept(swordPartBuilderBlock, SWORD_PART_BUILDER);
    }

    public static void registerBlockItems(BiConsumer<Item, ResourceLocation> register){
        Item.Properties props = Services.PLATFORM.defaultItemBuilder();
        register.accept(new BlockItem(swordPartBuilderBlock, props), BuiltInRegistries.BLOCK.getKey(swordPartBuilderBlock));
    }
}
