package net.jrdemiurge.enigmaticdice.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.entity.custom.DragonclawHookEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class DragonclawHookRender extends EntityRenderer<DragonclawHookEntity, ArrowRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "textures/entity/dragonclaw_hook.png");
    private final DragonclawHookModel model;

    public DragonclawHookRender(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new DragonclawHookModel(pContext.bakeLayer(ModModelLayers.DRAGONCLAW_HOOK_LAYER));
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public void extractRenderState(DragonclawHookEntity pEntity, ArrowRenderState pState, float pPartialTick) {
        super.extractRenderState(pEntity, pState, pPartialTick);
        pState.yRot = pEntity.getYRot(pPartialTick);
        pState.xRot = pEntity.getXRot(pPartialTick);
    }

    @Override
    public void submit(ArrowRenderState pState, PoseStack pPoseStack, SubmitNodeCollector pCollector, CameraRenderState pCamera) {
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(pState.yRot - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(pState.xRot + 90.0F));
        pPoseStack.scale(0.65F, 0.65F, 0.65F);
        pCollector.submitModel(this.model, pState, pPoseStack, TEXTURE, pState.lightCoords, OverlayTexture.NO_OVERLAY, pState.outlineColor, null);

        pPoseStack.popPose();
        super.submit(pState, pPoseStack, pCollector, pCamera);
    }
}
