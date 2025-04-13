package de.abq.partium.common.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PartBuilderBlockEntity extends AbstractGeoBlockEntity {
    public PartBuilderBlockEntity(BlockPos $$1, BlockState $$2) {
        super(BlockEntityType.CRAFTER, $$1, $$2);
    }

    @Override
    protected void saveAdditional(CompoundTag $$0, HolderLookup.Provider $$1) {
        super.saveAdditional($$0, $$1);
    }

    @Override
    protected void loadAdditional(CompoundTag $$0, HolderLookup.Provider $$1) {
        super.loadAdditional($$0, $$1);
    }
}
