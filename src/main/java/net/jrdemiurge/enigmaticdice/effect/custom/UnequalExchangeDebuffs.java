package net.jrdemiurge.enigmaticdice.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class UnequalExchangeDebuffs extends MobEffect {
    public UnequalExchangeDebuffs(int pColor) {
        super(MobEffectCategory.HARMFUL, pColor);
    }
}
