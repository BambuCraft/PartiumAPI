package de.abq.partium.event;

import de.abq.partium.Partium;
import de.abq.partium.client.renderer.SwordRenderer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApiStatus.Internal
public class ReloadListener implements PreparableReloadListener {
    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller, @NotNull ProfilerFiller profilerFiller1, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
        Partium.LOG.info("reloading");
        SwordRenderer.isReloading = true;
        Partium.known_models.clear();
        /*return CompletableFuture.supplyAsync(() -> {return null;}).thenCompose(preparationBarrier::wait).thenAcceptAsync((nil) -> {
            Partium.LOG.info("finished");
            SwordRenderer.isReloading = false;
        });*/
        CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait);
        return CompletableFuture.runAsync(() -> {
            Partium.LOG.info("finished");
            SwordRenderer.isReloading = false;
        }, gameExecutor);
    }
}
