package net.jrdemiurge.enigmaticdice.mixin;

import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Players get their dimensions from Avatar since 1.21.9 (previously Player overrode getDefaultDimensions).
@Mixin(Avatar.class)
public abstract class AvatarMixin {

    @Inject(method = "getDefaultDimensions", at = @At("TAIL"), cancellable = true)
    private void enigmaticdice$getDefaultDimensions(Pose pPose, CallbackInfoReturnable<EntityDimensions> cir) {
        float scale = ((SizeScaled) this).enigmaticdice$getSizeScale();
        if (scale != 1F) {
            cir.setReturnValue(cir.getReturnValue().scale(scale));
        }
    }
}
