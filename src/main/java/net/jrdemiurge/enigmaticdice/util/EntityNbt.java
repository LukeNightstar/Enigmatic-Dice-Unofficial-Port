package net.jrdemiurge.enigmaticdice.util;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.function.Consumer;

public final class EntityNbt {
    private EntityNbt() {}

    /** Saves the entity to NBT, lets {@code edit} change the tag and loads it back (entities save through ValueOutput since 1.21.6). */
    public static void edit(Entity entity, Consumer<CompoundTag> edit) {
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), EnigmaticDice.LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.saveWithoutId(output);
            CompoundTag tag = output.buildResult();
            edit.accept(tag);
            entity.load(TagValueInput.create(reporter, entity.registryAccess(), tag));
        }
    }
}
