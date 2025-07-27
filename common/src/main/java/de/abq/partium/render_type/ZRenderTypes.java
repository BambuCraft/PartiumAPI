package de.abq.partium.render_type;

import com.mojang.blaze3d.vertex.VertexFormat;
import de.abq.partium.Partium;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class ZRenderTypes extends RenderType {
    private static final ResourceLocation LIGHTSABER_BLADE = Partium.path("lightsaber_blade");

    public ZRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static @Nullable RenderType lightsaber_blade() {
        return VeilRenderType.get(LIGHTSABER_BLADE);
    }
}
