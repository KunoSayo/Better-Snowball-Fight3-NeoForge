package com.linngdu664.bsf.item.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import com.linngdu664.bsf.registry.*;

import java.util.List;

public class SmartSnowBlockItem extends BlockItem {
    public SmartSnowBlockItem() {
        super(BlockRegister.SMART_SNOW_BLOCK.get(), new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block0.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block2.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block3.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block4.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block5.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block6.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block7.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block8.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block9.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block10.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block11.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block12.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block13.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block14.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
    }
}