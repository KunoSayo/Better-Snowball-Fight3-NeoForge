package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.network.to_client.TeamMembersPayload;
import com.linngdu664.bsf.util.BSFTeamSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TeamLinkerItem extends Item {
    public static boolean shouldShowHighlight = false;  // client only
    private final int teamId;

    public TeamLinkerItem(int teamId) {
        super(new Properties());
        this.teamId = teamId;
    }

    private String getColorNameKeyById(int id) {
        return "color.minecraft." + DyeColor.byId(id).getName();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isShiftKeyDown()) {
            if (pLevel.isClientSide) {
                shouldShowHighlight = !shouldShowHighlight;
            }
        } else if (!pLevel.isClientSide) {
            BSFTeamSavedData savedData = pPlayer.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            String playerName = pPlayer.getName().getString();
            UUID uuid = pPlayer.getUUID();
            int oldId = savedData.getTeam(uuid);
            String[] oldNameParam = new String[]{playerName, MutableComponent.create(new TranslatableContents(getColorNameKeyById(oldId), null, new Object[]{})).getString()};
            String[] newNameParam = new String[]{playerName, MutableComponent.create(new TranslatableContents(getColorNameKeyById(teamId), null, new Object[]{})).getString()};
            HashSet<UUID> oldMembers = savedData.getMembers(oldId);
            oldMembers.stream()
                    .map(p -> (ServerPlayer) pLevel.getPlayerByUUID(p))
                    .filter(Objects::nonNull)
                    .forEach(p -> p.displayClientMessage(MutableComponent.create(new TranslatableContents("leave_bsf_team.tip", null, oldNameParam)), false));
            if (oldId == teamId) {
                // 退队
                savedData.exitTeam(uuid);
                oldMembers.stream()
                        .map(p -> (ServerPlayer) pLevel.getPlayerByUUID(p))
                        .filter(Objects::nonNull)
                        .forEach(p -> PacketDistributor.sendToPlayer(p, new TeamMembersPayload(oldMembers)));
                PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new TeamMembersPayload(new HashSet<>()));
            } else {
                // 退队后进队
                savedData.joinTeam(uuid, teamId);
                oldMembers.stream()
                        .map(p -> (ServerPlayer) pLevel.getPlayerByUUID(p))
                        .filter(Objects::nonNull)
                        .forEach(p -> PacketDistributor.sendToPlayer(p, new TeamMembersPayload(oldMembers)));
                HashSet<UUID> newMembers = savedData.getMembers(teamId);
                newMembers.stream()
                        .map(p -> (ServerPlayer) pLevel.getPlayerByUUID(p))
                        .filter(Objects::nonNull)
                        .forEach(p -> {
                            p.displayClientMessage(MutableComponent.create(new TranslatableContents("join_bsf_team.tip", null, newNameParam)), false);
                            PacketDistributor.sendToPlayer(p, new TeamMembersPayload(newMembers));
                        });
            }
            savedData.setDirty();
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(itemstack);

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Options options = Minecraft.getInstance().options;
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("team_linker.tooltip", null, new Object[]{options.keyUse.getTranslatedKeyMessage()})).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("team_linker1.tooltip", null, new Object[]{options.keyShift.getTranslatedKeyMessage(),options.keyUse.getTranslatedKeyMessage()})).withStyle(ChatFormatting.GRAY));
    }
}