package de.abq.partium.platform;

import com.mojang.blaze3d.vertex.VertexFormat;
import de.abq.partium.Partium;
import de.abq.partium.platform.services.IPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
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
        try {
            return Optional.of( new ShaderInstance(Minecraft.getInstance().getResourceManager(), rl, vertexFormat));
        } catch (IOException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }
}