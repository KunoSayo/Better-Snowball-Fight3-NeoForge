package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.ZoneControllerEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.tool.TeamLinkerItem;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZoneController extends Block implements EntityBlock {
    public ZoneController() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }
    protected static final VoxelShape SHAPE;
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ZoneControllerEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BlockEntityRegister.ZONE_CONTROLLER_ENTITY.get() ? ZoneControllerEntity::tick : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ZoneControllerEntity zoneControllerEntity) {
            // 空手点击时的逻辑，选择一个雪傀儡出生点传送
            if (!level.isClientSide()) {
                randomTp(zoneControllerEntity, level, player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ZoneControllerEntity zoneControllerEntity) {
            if (stack.has(DataComponentRegister.REGION) && player.getAbilities().instabuild) {
                if (!level.isClientSide()) {
                    RegionData regionData = stack.get(DataComponentRegister.REGION);
                    if (regionData.start().getY() < regionData.end().getY()) {
                        zoneControllerEntity.setSnowGolemList(regionData);
                        player.displayClientMessage(Component.literal("Add " + zoneControllerEntity.getSnowGolemCount() + " golems"), false);
                    } else {
                        zoneControllerEntity.setRegionAndSummon(regionData);
                        player.displayClientMessage(Component.literal("Add " + zoneControllerEntity.getSummonPosList().size() + " spawn points"), false);
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
            if (stack.getItem() instanceof TeamLinkerItem teamLinkerItem && player.getAbilities().instabuild) {
                if (!level.isClientSide()) {
                    byte teamId = teamLinkerItem.getTeamId();
                    zoneControllerEntity.setTeamId(teamId);
                    player.displayClientMessage(Component.literal("Set controller team " + DyeColor.byId(teamId).getName()), false);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;     // 传到空手右击的逻辑
    }

    private void randomTp(ZoneControllerEntity entity, Level level, Player player) {
        List<BlockPos> blockPosList = entity.getSummonPosList();
        if (!blockPosList.isEmpty()) {
            BlockPos blockPos = blockPosList.get(level.random.nextInt(blockPosList.size()));
            Vec3 pos = blockPos.above().getBottomCenter();
            player.teleportTo(pos.x, pos.y, pos.z);
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
    }
}
