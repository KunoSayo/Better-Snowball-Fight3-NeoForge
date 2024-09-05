package com.linngdu664.bsf.entity.snowball.special;

import com.linngdu664.bsf.block.entity.CriticalSnowEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.entity.snowball.util.LaunchFrom;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FrozenSnowballEntity extends AbstractBSFSnowballEntity {
    public FrozenSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new BSFSnowballEntityProperties().basicDamage(3).basicBlazeDamage(8).basicFrozenTicks(60));
    }

    public FrozenSnowballEntity(Level pLevel, double pX, double pY, double pZ) {
        super(EntityRegister.FROZEN_SNOWBALL.get(), pX, pY, pZ, pLevel, new BSFSnowballEntityProperties().basicDamage(3).basicBlazeDamage(8).basicFrozenTicks(60));
    }

    public FrozenSnowballEntity(LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment) {
        super(EntityRegister.FROZEN_SNOWBALL.get(), pShooter, pLevel, new BSFSnowballEntityProperties().basicDamage(3).basicBlazeDamage(8).basicFrozenTicks(60).applyAdjustment(launchAdjustment));
    }

    @Override
    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);
        Level level = level();
        if (!level.isClientSide) {
            if (!isCaught) {
                float frozenRange;
                if (getLaunchFrom() == LaunchFrom.FREEZING_CANNON) {
                    frozenRange = 3.5F;
                } else {
                    frozenRange = 2.5F;
                }
                Vec3 location = BSFCommonUtil.getRealHitPosOnMoveVecWithHitResult(this, pResult);
                BlockPos blockPos = new BlockPos(Mth.floor(location.x), Mth.floor(location.y), Mth.floor(location.z));
                BlockState ice = Blocks.ICE.defaultBlockState();
                BlockState basalt = Blocks.BASALT.defaultBlockState();
                BlockState air = Blocks.AIR.defaultBlockState();
                BlockState newBlock = Blocks.SNOW.defaultBlockState();
                for (int i = (int) (blockPos.getX() - frozenRange); i <= (int) (blockPos.getX() + frozenRange); i++) {
                    for (int j = (int) (blockPos.getY() - frozenRange); j <= (int) (blockPos.getY() + frozenRange); j++) {
                        for (int k = (int) (blockPos.getZ() - frozenRange); k <= (int) (blockPos.getZ() + frozenRange); k++) {
                            if (BSFCommonUtil.lengthSqr(i - blockPos.getX(), j - blockPos.getY(), k - blockPos.getZ()) <= frozenRange * frozenRange) {
                                BlockPos blockPos1 = new BlockPos(i, j, k);
                                BlockState blockState = level.getBlockState(blockPos1);
                                if (blockState.getBlock() == Blocks.WATER && blockState.getValue(LiquidBlock.LEVEL) == 0) {
                                    level.setBlockAndUpdate(blockPos1, ice);
                                } else if (blockState.getBlock() == Blocks.LAVA && blockState.getValue(LiquidBlock.LEVEL) == 0) {
                                    level.setBlockAndUpdate(blockPos1, basalt);
                                } else if (blockState.getBlock() == Blocks.FIRE) {
                                    level.setBlockAndUpdate(blockPos1, air);
                                } else if (level.getBlockEntity(blockPos1) instanceof CriticalSnowEntity blockEntity) {
                                    blockEntity.setAge(0);
                                    blockEntity.setChanged();
                                } else if (blockState.canBeReplaced() && newBlock.canSurvive(level, blockPos1)) {
                                    level.setBlockAndUpdate(blockPos1, newBlock);
                                }
                            }
                        }
                    }
                }
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(location,location).inflate(4), p -> !(p instanceof BSFSnowGolemEntity) && !(p instanceof SnowGolem) && !(p instanceof Player player && player.isSpectator()) && distanceToSqr(p) < frozenRange * frozenRange);
                for (LivingEntity entity : list) {
                    int frozenTicks = getFrozenTicks();
                    if (frozenTicks > 0) {
                        if (entity.getTicksFrozen() < 200) {
                            entity.setTicksFrozen(entity.getTicksFrozen() + frozenTicks);
                        }
                        entity.hurt(level.damageSources().thrown(this, this.getOwner()), Float.MIN_NORMAL);
                        if (getLaunchFrom() == LaunchFrom.FREEZING_CANNON) {
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4));
                        }
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
                    }
                }
                if (getLaunchFrom() == LaunchFrom.FREEZING_CANNON) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, location.x, location.y, location.z, 400, 0, 0, 0, 0.32);
                } else {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, location.x, location.y, location.z, 200, 0, 0, 0, 0.32);
                }
                level.playSound(null, location.x, location.y, location.z, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
            this.discard();
        }
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemRegister.FROZEN_SNOWBALL.get();
    }

//    @Override
//    public float getBasicDamage() {
//        return 3;
//    }
//
//    @Override
//    public float getBasicBlazeDamage() {
//        return 8;
//    }
//
//    @Override
//    public int getBasicFrozenTicks() {
//        return 60;
//    }

    @Override
    public float getSubspacePower() {
        return 1.6f;
    }
}
