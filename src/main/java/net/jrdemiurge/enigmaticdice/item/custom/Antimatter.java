package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public class Antimatter extends Item {
    public Antimatter(Properties pProperties) {
        super(pProperties);
    }

    public static void init() {
        DispenserBlock.registerBehavior(ModItems.ANTIMATTER.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.level();
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(direction);

                if (!level.isClientSide()) {
                    level.destroyBlock(targetPos, false);

                    level.explode(null,
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5,
                            10F,
                            false,
                            Level.ExplosionInteraction.NONE);

                    stack.shrink(1);
                }

                return stack;
            }
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();

        if (player != null && player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                level.destroyBlock(pos, false);

                level.explode(null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        10F,
                        false,
                        Level.ExplosionInteraction.NONE);

                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay pTooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Minecraft.getInstance().hasShiftDown()) {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.antimatter.1"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.antimatter.2"));
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.antimatter.3"));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pContext, pTooltipDisplay, pTooltipComponents, pIsAdvanced);
    }
}
