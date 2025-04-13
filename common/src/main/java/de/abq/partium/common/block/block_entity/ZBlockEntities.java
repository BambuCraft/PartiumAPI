package de.abq.partium.common.block.block_entity;

import de.abq.partium.common.block.ZSBlocks;
import de.abq.partium.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ZBlockEntities {
    private static final Map<ResourceLocation, BlockEntityType<?>> ALL = new HashMap<>();

    public static final BlockEntityType<PartBuilderBlockEntity> SWORD_PART_BUILDER_BLOCK_ENTITY = assign(ZSBlocks.SWORD_PART_BUILDER, PartBuilderBlockEntity::new, ZSBlocks.swordPartBuilderBlock);

    private static <T extends BlockEntity> BlockEntityType<T> assign(ResourceLocation id, BiFunction<BlockPos, BlockState, T> fn, Block... blocks){
        var ret = Services.PLATFORM.createBlockEntityType(fn, blocks);
        var old = ALL.put(id, ret);
        if (old != null) throw new IllegalArgumentException("ID duplicated" + id);
        return ret;
    }

    public static void registerBlockEntities(BiConsumer<BlockEntityType<?>, ResourceLocation> r){
        for (var be : ALL.entrySet()){
            r.accept(be.getValue(),be.getKey());
        }
    }

    public static <T extends AbstractGeoBlockEntity> String getId(BlockEntityType<T> be) {
        for (var type : ALL.entrySet()){
            if (type.getValue().equals(be)){
                return type.getKey().getPath();
            }
        }
        return null;
    }
}
