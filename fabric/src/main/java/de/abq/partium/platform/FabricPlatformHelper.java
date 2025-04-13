package de.abq.partium.platform;

import com.mojang.blaze3d.vertex.VertexFormat;
import de.abq.partium.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public String getEnvironmentName() {
        return IPlatformHelper.super.getEnvironmentName();
    }

    @Override
    public Item.Properties defaultItemBuilderWithCustomDamageOnFabric() {
        return IPlatformHelper.super.defaultItemBuilderWithCustomDamageOnFabric();
    }

    @Override
    public Item.Properties defaultItemBuilder() {
        return new Item.Properties();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> fn, Block... blocks) {
        return BlockEntityType.Builder.of(fn::apply, blocks).build(null);
    }

    @Override
    public Optional<ShaderInstance> loaderShaderInstance(ResourceLocation rl, VertexFormat vertexFormat) {
        return Optional.empty();
    }
}
