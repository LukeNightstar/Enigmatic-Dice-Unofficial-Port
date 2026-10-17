package net.jrdemiurge.enigmaticdice.mixin;

import net.jrdemiurge.enigmaticdice.item.custom.Permafrost;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        UUID id = self.getUUID();

        if (Permafrost.stackMap.getOrDefault(id, 0) >= 10) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadAdditionalSaveData(ValueInput pInput, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        UUID playerId = player.getUUID();

        float permafrostStacksFlySpeedReduction = player.getPersistentData().getFloatOr(Permafrost.PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION, 0.0F);
        float permafrostAuraFlySpeedReduction = player.getPersistentData().getFloatOr(Permafrost.PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION, 0.0F);
        float permafrostFlySpeedRecovery = 0;

        if (permafrostStacksFlySpeedReduction != 0 && !Permafrost.stackMap.containsKey(playerId)) {
            permafrostFlySpeedRecovery += permafrostStacksFlySpeedReduction;
        }
        if (permafrostAuraFlySpeedReduction != 0 && !Permafrost.auraApplied.contains(playerId)) {
            permafrostFlySpeedRecovery += permafrostAuraFlySpeedReduction;
        }
        if (permafrostFlySpeedRecovery != 0) {
            float restoredFlySpeed = player.getAbilities().getFlyingSpeed() + permafrostFlySpeedRecovery;
            player.getAbilities().setFlyingSpeed(restoredFlySpeed);
            player.onUpdateAbilities();

            player.getPersistentData().remove(Permafrost.PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION);
            player.getPersistentData().remove(Permafrost.PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION);
        }
    }
}
