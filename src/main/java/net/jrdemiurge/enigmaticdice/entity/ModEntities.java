package net.jrdemiurge.enigmaticdice.entity;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.entity.custom.DragonclawHookEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, EnigmaticDice.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DragonclawHookEntity>> DRAGONCLAW_HOOK =
            ENTITY_TYPES.register("dragonclaw_hook", () -> EntityType.Builder.<DragonclawHookEntity>of(DragonclawHookEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).build(EnigmaticDice.MOD_ID + ":dragonclaw_hook"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
