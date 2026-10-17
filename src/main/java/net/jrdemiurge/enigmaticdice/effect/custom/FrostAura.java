package net.jrdemiurge.enigmaticdice.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FrostAura extends MobEffect {
    public FrostAura(int pColor) {
        super(MobEffectCategory.HARMFUL, pColor);
    }
}
