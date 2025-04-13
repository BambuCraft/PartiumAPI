package de.abq.partium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.abq.partium.Partium;
import de.abq.partium.client.model.DynamicItemModel;
import de.abq.partium.common.data_components.ELDataComponents;
import de.abq.partium.common.data_components.PartsComponents;
import de.abq.partium.common.data_components.parts.BladesPart;
import de.abq.partium.common.data_components.parts.ModelPart;
import de.abq.partium.common.item.PartiumSwordItem;
import de.abq.partium.util.CheckedResourceLocation;
import de.abq.partium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

//TODO: Only works for sword item
public class ToolRenderer extends GeoItemRenderer<PartiumSwordItem> {
    private final BladeRenderLayer bladeRenderLayer = new BladeRenderLayer(this);
    private final ModelRenderLayer<PartiumSwordItem> emitterRenderLayer = new ModelRenderLayer<>(this, "emitter");
    private final ModelRenderLayer<PartiumSwordItem> pommelRenderLayer = new ModelRenderLayer<>(this, "pommel");
    private final ModelRenderLayer<PartiumSwordItem> guardRenderLayer = new ModelRenderLayer<>(this, "guard");

    private BakedGeoModel gripModel = null;
    private boolean gripChange = true;

    private boolean isGUIRendered = false;
    private boolean isFixed = false;

    public ToolRenderer() {
        super(new DefaultedItemGeoModel<>(Partium.path("base")));
        addRenderLayer(emitterRenderLayer);
        addRenderLayer(guardRenderLayer);
        addRenderLayer(pommelRenderLayer);
        addRenderLayer(bladeRenderLayer);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.animatable = (PartiumSwordItem) stack.getItem();
        this.currentItemStack = stack;
        this.renderPerspective = transformType;
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);

        RenderType renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);
        if (!isFixed) {
            PartsComponents parts = stack.getComponents().get(ELDataComponents.SWORD_PARTS);

            if (renderType == null) {
                Partium.LOG.warn("renderType == null");
            }
            if (parts == null) {
                Partium.LOG.warn("parts == null");
            } else {
                BladesPart bladesData = parts.blades();
                ModelPart emitterData = parts.emitter();
                ModelPart guardData = parts.guard();
                ModelPart gripData = parts.grip();
                ModelPart pommelData = parts.pommel();

                DynamicItemModel<PartiumSwordItem> localGripModel = new DynamicItemModel<>(gripData.model());
                if ( CheckedResourceLocation.exists(localGripModel.getModelResource(animatable))) {
                    this.gripModel = localGripModel.getBakedModel(localGripModel.getModelResource(animatable));
                    BakedGeoModel rootModel = model.getBakedModel(this.model.getModelResource((PartiumSwordItem) stack.getItem()));
                    this.gripChange = false;
                    this.gripModel.getBone("bb_main").ifPresent(gripBone -> {
                        GeoBone rootBone = rootModel.getBone("root").get();

                        gripBone.updatePosition(
                                rootBone.getPosX()-8,
                                rootBone.getPosY()+8,
                                rootBone.getPosZ()+8.5f
                        );
                        gripBone.markPositionAsChanged();
                    });

                    model.getBone("joint_emitter").get().setPivotX(this.gripModel.getBone("joint_emitter").get().getPivotX() + .13f);
                    model.getBone("joint_emitter").get().setPivotY(this.gripModel.getBone("joint_emitter").get().getPivotY() - .13f);
                    model.getBone("joint_emitter").get().setPivotZ(this.gripModel.getBone("joint_emitter").get().getPivotZ() + .5f);

                    model.getBone("joint_guard").get().setPivotX(this.gripModel.getBone("joint_guard").get().getPivotX() + .13f);
                    model.getBone("joint_guard").get().setPivotY(this.gripModel.getBone("joint_guard").get().getPivotY() - .13f);
                    model.getBone("joint_guard").get().setPivotZ(this.gripModel.getBone("joint_guard").get().getPivotZ() + .5f);

                    model.getBone("joint_pommel").get().setPivotX(this.gripModel.getBone("joint_pommel").get().getPivotX() + .13f);
                    model.getBone("joint_pommel").get().setPivotY(this.gripModel.getBone("joint_pommel").get().getPivotY() - .13f);
                    model.getBone("joint_pommel").get().setPivotZ(this.gripModel.getBone("joint_pommel").get().getPivotZ() + .5f);

                    model.getBone("joint_emitter").get().setRotX(this.gripModel.getBone("joint_emitter").get().getRotX());
                    model.getBone("joint_emitter").get().setRotY(this.gripModel.getBone("joint_emitter").get().getRotY());
                    model.getBone("joint_emitter").get().setRotZ(this.gripModel.getBone("joint_emitter").get().getRotZ());

                    model.getBone("joint_guard").get().setRotX(this.gripModel.getBone("joint_guard").get().getRotX());
                    model.getBone("joint_guard").get().setRotY(this.gripModel.getBone("joint_guard").get().getRotY());
                    model.getBone("joint_guard").get().setRotZ(this.gripModel.getBone("joint_guard").get().getRotZ());

                    model.getBone("joint_pommel").get().setRotX(this.gripModel.getBone("joint_pommel").get().getRotX());
                    model.getBone("joint_pommel").get().setRotY(this.gripModel.getBone("joint_pommel").get().getRotY());
                    model.getBone("joint_pommel").get().setRotZ(this.gripModel.getBone("joint_pommel").get().getRotZ());
                }

                if (this.bladeRenderLayer.getBlades() != bladesData) {
                    this.bladeRenderLayer.setBlades(bladesData);
                }
                if (shouldRender(emitterData.model(), this.emitterRenderLayer.getModel())) {
                    this.emitterRenderLayer.setModel(emitterData.model());
                    this.emitterRenderLayer.setScale(emitterData.scale());
                    this.emitterRenderLayer.setRetry(true);
                }
                if (shouldRender(guardData.model(), this.guardRenderLayer.getModel())) {
                    this.guardRenderLayer.setModel(guardData.model());
                    this.guardRenderLayer.setScale(guardData.scale());
                    this.guardRenderLayer.setRetry(true);
                }
                if (shouldRender(pommelData.model(), this.pommelRenderLayer.getModel())) {
                    this.pommelRenderLayer.setModel(pommelData.model());
                    this.pommelRenderLayer.setScale(pommelData.scale());
                    this.pommelRenderLayer.setRetry(true);
                }

                if ( CheckedResourceLocation.exists(localGripModel.getModelResource(animatable)) ){
                    if (CheckedResourceLocation.exists(localGripModel.getTextureResource(animatable))) renderType = RenderType.entityTranslucent(localGripModel.getTextureResource(animatable));
                    this.reRender(this.gripModel, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, this.getRenderColor(animatable, partialTick, packedLight).argbInt());
                }
            }
        }

        this.bladeRenderLayer.setShouldRender(renderPerspective != ItemDisplayContext.GUI);
        this.emitterRenderLayer.setGUIRender(renderPerspective == ItemDisplayContext.GUI);
        this.guardRenderLayer.setGUIRender(renderPerspective == ItemDisplayContext.GUI);
        this.pommelRenderLayer.setGUIRender(renderPerspective == ItemDisplayContext.GUI);

        if (renderPerspective == ItemDisplayContext.GUI){
            //TODO: FIX!!!!!! (Make a triangle to indicate the inner and outer color )
            bufferSource.getBuffer(renderType).addVertex(-1,-1,-1).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryOuterColor()).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1,1,1).setUv(0,0);
            bufferSource.getBuffer(renderType).addVertex(0f,0f,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryOuterColor()).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1,1,1).setUv(0,0);
            bufferSource.getBuffer(renderType).addVertex(1f,1f,1).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryOuterColor()).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1,1,1).setUv(0,0);
            //bufferSource.getBuffer(renderType).addVertex(matrix, 0.5f,0,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryInnerColor()).setOverlay(packedOverlay).setNormal(1,1,1).setUv(0,0);
            //bufferSource.getBuffer(renderType).addVertex(matrix, 0,1,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryInnerColor()).setOverlay(packedOverlay).setNormal(1,1,1).setUv(0,0);
            //bufferSource.getBuffer(renderType).addVertex(matrix, 1,1,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryInnerColor()).setOverlay(packedOverlay).setNormal(1,1,1).setUv(0,0);
        }
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        this.isFixed = transformType == ItemDisplayContext.FIXED;
    }

    private boolean shouldRender(ResourceLocation input, ResourceLocation compare){
        return (
                (input != compare) &&
                (input != Util.EMPTY_RESOURCE_LOCATION)
        );
    }

    public void pushBladeJointsChecked(GeoBone bladeJoint) {
        bladeRenderLayer.pushBonesChecked(bladeJoint);
    }
    public void setBladeEmitterLocation(Vector3f location){
        bladeRenderLayer.setEmitterLocation(location);
    }
    public void setBladeParentScale(float scale){
        bladeRenderLayer.setParentScale(scale);
    }
}
