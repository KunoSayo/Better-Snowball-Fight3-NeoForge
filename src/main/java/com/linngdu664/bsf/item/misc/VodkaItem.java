package com.linngdu664.bsf.item.misc;

import com.linngdu664.bsf.registry.EffectRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VodkaItem extends Item {
    public VodkaItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player) {
            if (!pLevel.isClientSide) {
                int t = 0;
                if (pEntityLiving.hasEffect(EffectRegister.COLD_RESISTANCE)) {
                    t = pEntityLiving.getEffect(EffectRegister.COLD_RESISTANCE).getDuration();
                    pEntityLiving.setRemainingFireTicks(pEntityLiving.getRemainingFireTicks() + 60);
                }
                pEntityLiving.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100 + t));
                pEntityLiving.addEffect(new MobEffectInstance(EffectRegister.COLD_RESISTANCE, 600));
                pEntityLiving.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600));
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, pStack);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
                player.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE), true);
            }
            pLevel.gameEvent(pEntityLiving, GameEvent.DRINK, pEntityLiving.getEyePosition());
        }
        return pStack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("vodka.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
