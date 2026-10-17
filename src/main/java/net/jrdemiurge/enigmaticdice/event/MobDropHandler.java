package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.stat.ModStats;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

@EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class MobDropHandler {

    @SubscribeEvent
    public static void onMobDeath(LivingDropsEvent event) {
        DamageSource source = event.getSource();

        LivingEntity killed = event.getEntity();
        if (!(killed instanceof Monster)) {
            return;
        }

        if (source.getEntity() instanceof Player player && !(source.getEntity() instanceof FakePlayer)) {
            if (player.level().getRandom().nextFloat() < Config.EnigmaticDieMobDropChance) {
                if (canObtainDice(player, ModStats.OBTAINED_DICE_FROM_MOB)) {
                    ItemStack drop = new ItemStack(ModItems.ENIGAMTIC_DIE.get());

                    event.getDrops().add(new ItemEntity(player.level(),
                            event.getEntity().getX(),
                            event.getEntity().getY(),
                            event.getEntity().getZ(),
                            drop));

                    player.awardStat(ModStats.OBTAINED_DICE_FROM_MOB);
                    player.sendSystemMessage(Component.translatable("enigmaticdice.mob_drop." + player.level().getRandom().nextInt(4)));
                }
            }
        }
    }

    public static boolean canObtainDice(Player player, Identifier statKey) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (Config.MobDiceTimeInterval == 0) return true;
            int playTimeTicks = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.TOTAL_WORLD_TIME));
            int playTimeMinutes = playTimeTicks / (20 * 60);
            int allowedCount = playTimeMinutes / Config.MobDiceTimeInterval;

            int currentCount = serverPlayer.getStats().getValue(Stats.CUSTOM.get(statKey));

            return currentCount < allowedCount;
        }
        return false;
    }
}
