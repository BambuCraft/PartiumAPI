package de.abq.partium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.abq.partium.Partium;
import de.abq.partium.client.model.DynamicItemModel;
import de.abq.partium.common.data_components.PartiumDataComponents;
import de.abq.partium.common.data_components.PartsComponents;
import de.abq.partium.common.data_components.parts.BladePart;
import de.abq.partium.common.data_components.parts.ModelPart;
import de.abq.partium.common.item.PartiumSwordItem;
import de.abq.partium.util.CheckedResourceLocation;
import de.abq.partium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Map;
import java.util.Optional;

public class SwordRenderer extends GeoItemRenderer<PartiumSwordItem> {
    private final BladeRenderLayer bladeRenderLayer = new BladeRenderLayer(this);
    private final ModelRenderLayer<PartiumSwordItem> emitterRenderLayer = new ModelRenderLayer<>(this, "emitter");
    private final ModelRenderLayer<PartiumSwordItem> pommelRenderLayer = new ModelRenderLayer<>(this, "pommel");
    private final ModelRenderLayer<PartiumSwordItem> guardRenderLayer = new ModelRenderLayer<>(this, "guard");

    public static final String ANCHOR_ROOT = "grip";

    private BakedGeoModel gripModel = null;
    private boolean gripChange = true;

    private boolean isGUIRendered = false;
    private boolean isFixed = false;

    public SwordRenderer() {
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
            PartsComponents parts = stack.getComponents().get(PartiumDataComponents.SWORD_PARTS);

            if (renderType == null) {
                Partium.LOG.warn("renderType == null");
                return;
            }
            if (parts == null) {
                Partium.LOG.warn("parts == null");
                return;
            }
            Map<String, BladePart> bladesData = BladePart.into(parts.blades());

            ModelPart emitterData = parts.emitter();
            ModelPart guardData = parts.guard();
            ModelPart gripData = parts.grip();
            ModelPart pommelData = parts.pommel();

            DynamicItemModel<PartiumSwordItem> localGripModel = new DynamicItemModel<>(gripData.model());
            if ( CheckedResourceLocation.exists(localGripModel.getModelResource(animatable))) {
                this.gripModel = localGripModel.getBakedModel(localGripModel.getModelResource(animatable));
                BakedGeoModel rootModel = model.getBakedModel(this.model.getModelResource((PartiumSwordItem) stack.getItem()));
                this.gripChange = false;
                this.gripModel.getBone(ANCHOR_ROOT).ifPresent(gripBone -> {
                    Optional<GeoBone> rootBone = rootModel.getBone("root");
                    if (rootBone.isEmpty()) {
                        Partium.LOG.info("No root bone found in {}", this.animatable);
                        return;
                    }

                    gripBone.updatePosition(
                            rootBone.get().getPosX()-8,
                            rootBone.get().getPosY()+8,
                            rootBone.get().getPosZ()+8.5f
                    );
                    gripBone.markPositionAsChanged();
                });

                this.gripModel.getBone("joint_emitter").ifPresent(emitterJoint -> {
                    rootModel.getBone("joint_emitter").get().setPivotX(emitterJoint.getPivotX() + .13f);
                    rootModel.getBone("joint_emitter").get().setPivotY(emitterJoint.getPivotY() - .13f);
                    rootModel.getBone("joint_emitter").get().setPivotZ(emitterJoint.getPivotZ() + .5f);

                    rootModel.getBone("joint_emitter").get().setRotX(emitterJoint.getRotX());
                    rootModel.getBone("joint_emitter").get().setRotY(emitterJoint.getRotY());
                    rootModel.getBone("joint_emitter").get().setRotZ(emitterJoint.getRotZ());

                });

                this.gripModel.getBone("joint_guard").ifPresent(guardJoint -> {
                    rootModel.getBone("joint_guard").get().setPivotX(guardJoint.getPivotX() + .13f);
                    rootModel.getBone("joint_guard").get().setPivotY(guardJoint.getPivotY() - .13f);
                    rootModel.getBone("joint_guard").get().setPivotZ(guardJoint.getPivotZ() + .5f);

                    rootModel.getBone("joint_guard").get().setRotX(guardJoint.getRotX());
                    rootModel.getBone("joint_guard").get().setRotY(guardJoint.getRotY());
                    rootModel.getBone("joint_guard").get().setRotZ(guardJoint.getRotZ());
                });

                this.gripModel.getBone("joint_pommel").ifPresent((pommelJoint) -> {
                    rootModel.getBone("joint_pommel").get().setPivotX(pommelJoint.getPivotX() + .13f);
                    rootModel.getBone("joint_pommel").get().setPivotY(pommelJoint.getPivotY() - .13f);
                    rootModel.getBone("joint_pommel").get().setPivotZ(pommelJoint.getPivotZ() + .5f);

                    rootModel.getBone("joint_pommel").get().setRotX(pommelJoint.getRotX());
                    rootModel.getBone("joint_pommel").get().setRotY(pommelJoint.getRotY());
                    rootModel.getBone("joint_pommel").get().setRotZ(pommelJoint.getRotZ());

                });
            }

            //TODO: convert to simple class
            if (this.bladeRenderLayer.getBlades() != bladesData) {
                this.bladeRenderLayer.setBlades(bladesData);
            }
            if (shouldRender(emitterData.model(), this.emitterRenderLayer.getResource())) {
                this.emitterRenderLayer.setResource(emitterData.model());
                this.emitterRenderLayer.setScale(emitterData.scale());
                this.emitterRenderLayer.setRetry(true);
            }
            if (shouldRender(guardData.model(), this.guardRenderLayer.getResource())) {
                this.guardRenderLayer.setResource(guardData.model());
                this.guardRenderLayer.setScale(guardData.scale());
                this.guardRenderLayer.setRetry(true);
            }
            if (shouldRender(pommelData.model(), this.pommelRenderLayer.getResource())) {
                this.pommelRenderLayer.setResource(pommelData.model());
                this.pommelRenderLayer.setScale(pommelData.scale());
                this.pommelRenderLayer.setRetry(true);
            }

            if ( CheckedResourceLocation.exists(localGripModel.getModelResource(animatable)) ){
                if (CheckedResourceLocation.exists(localGripModel.getTextureResource(animatable))) renderType = RenderType.entityTranslucent(localGripModel.getTextureResource(animatable));
                this.reRender(this.gripModel, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, this.getRenderColor(animatable, partialTick, packedLight).argbInt());
            }
        }

        this.bladeRenderLayer.setShouldRender(renderPerspective != ItemDisplayContext.GUI);
        this.emitterRenderLayer.setGUIRender(renderPerspective == ItemDisplayContext.GUI);
        this.guardRenderLayer.setGUIRender(renderPerspective == ItemDisplayContext.GUI);
        this.pommelRenderLayer.setGUIRender(renderPerspective == ItemDisplayContext.GUI);

        /*
        if (renderPerspective == ItemDisplayContext.GUI){
            //TODO: FIX!!!!!! (Make a triangle to indicate the inner and outer color )
           bufferSource.getBuffer(renderType).addVertex(-1,-1,-1).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryOuterColor()).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1,1,1).setUv(0,0);
           bufferSource.getBuffer(renderType).addVertex(0f,0f,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryOuterColor()).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1,1,1).setUv(0,0);
           bufferSource.getBuffer(renderType).addVertex(1f,1f,1).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryOuterColor()).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(1,1,1).setUv(0,0);
           // //bufferSource.getBuffer(renderType).addVertex(matrix, 0.5f,0,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryInnerColor()).setOverlay(packedOverlay).setNormal(1,1,1).setUv(0,0);
            //bufferSource.getBuffer(renderType).addVertex(matrix, 0,1,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryInnerColor()).setOverlay(packedOverlay).setNormal(1,1,1).setUv(0,0);
            //bufferSource.getBuffer(renderType).addVertex(matrix, 1,1,0).setLight(0xF000F0).setColor(this.bladeRenderLayer.getPrimaryInnerColor()).setOverlay(packedOverlay).setNormal(1,1,1).setUv(0,0);
        }*/
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
    public Vector3f getBladeEmitterLocation(){
        return bladeRenderLayer.getEmitterLocation();
    }
    public void setBladeParentScale(float scale){
        bladeRenderLayer.setParentScale(scale);
    }
}
