package net.jrdemiurge.enigmaticdice.mixin;

import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Entities render from a render state since 1.21.2; the size scale now goes into the state's model scale
// and shadow radius instead of wrapping the render call in a scaled pose.
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void enigmaticdice$applySizeScale(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        AttributeInstance inst = entity.getAttribute(ModAttributes.SIZE_SCALE);
        if (inst == null) return;
        float scale = (float) inst.getValue();
        if (scale != 1F) {
            state.scale *= scale;
            state.shadowRadius *= scale;
        }
    }
}
