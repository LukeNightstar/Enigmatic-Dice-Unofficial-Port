package net.jrdemiurge.enigmaticdice.entity;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.entity.custom.DragonclawHookEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(EnigmaticDice.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DragonclawHookEntity>> DRAGONCLAW_HOOK =
            ENTITY_TYPES.registerEntityType("dragonclaw_hook", DragonclawHookEntity::new, MobCategory.MISC,
                    builder -> builder.sized(0.5F, 0.5F));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
