package net.jrdemiurge.enigmaticdice.mixin;

/** Implemented on every LivingEntity by {@link LivingEntityMixin}; exposes the cached size scale to subclass mixins. */
public interface SizeScaled {
    float enigmaticdice$getSizeScale();
}
