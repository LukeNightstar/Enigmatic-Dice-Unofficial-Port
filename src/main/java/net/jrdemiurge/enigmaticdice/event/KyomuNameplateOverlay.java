package net.jrdemiurge.enigmaticdice.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Matrix4f;

/*@EventBusSubscriber(modid = EnigmaticDice.MOD_ID, value = Dist.CLIENT)
public final class KyomuNameplateOverlay {

    @SubscribeEvent
    public static void onRenderNameTag(net.neoforged.neoforge.client.event.RenderNameTagEvent e) {
     if (!(e.getEntity() instanceof LivingEntity victim)) return;

     double pending = 10;
     if (pending <= 0.0001) return;

     PoseStack pose = e.getPoseStack();
     MultiBufferSource buf = e.getMultiBufferSource();
     int packedLight = e.getPackedLight();

     EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
     double d0 = erd.distanceToSqr(victim);

     boolean seeThrough = !victim.isDiscrete();
     float baseYOffset = victim.getNameTagOffsetY();

     Component comp = Component.literal(String.format("%.0f", pending))
             .withStyle(ChatFormatting.DARK_PURPLE);

     pose.pushPose();

     pose.translate(0.0F, baseYOffset + 0.4F, 0.0F);
     pose.mulPose(erd.cameraOrientation());
     pose.scale(-0.04F, -0.04F, 0.04F);

     Font font = Minecraft.getInstance().font;
     float x = -font.width(comp) / 2f;

     Matrix4f mat = pose.last().pose();

     if (seeThrough) {
         font.drawInBatch(comp, x, 0, 0xFFFFFFFF, false, mat, buf, Font.DisplayMode.NORMAL, 0, packedLight);
     }

     pose.popPose();
    }
}*/
