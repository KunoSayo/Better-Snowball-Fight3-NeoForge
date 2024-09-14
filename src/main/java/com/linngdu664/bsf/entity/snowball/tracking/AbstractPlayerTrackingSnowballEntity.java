package com.linngdu664.bsf.entity.snowball.tracking;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class AbstractPlayerTrackingSnowballEntity extends AbstractTrackingSnowballEntity {
    public AbstractPlayerTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel, BSFSnowballEntityProperties pProperties, boolean isLockFeet) {
        super(pEntityType, pLevel, pProperties, isLockFeet);
    }

    public AbstractPlayerTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, LivingEntity pShooter, Level pLevel, BSFSnowballEntityProperties pProperties, boolean isLockFeet, RegionData region) {
        super(pEntityType, pShooter, pLevel, pProperties, isLockFeet, region);
    }

    @Override
    public Entity getTarget() {
        Vec3 velocity = getDeltaMovement();
        Level level = level();
        Entity shooter = getOwner();
        AABB aabb = getBoundingBox().inflate(range);
        BSFTeamSavedData savedData = getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        if (shooter instanceof Player player) {
            List<Player> list = level.getEntitiesOfClass(Player.class, aabb, p -> !p.isSpectator() && !p.equals(shooter) && !savedData.isSameTeam(player, p) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5);
            if (!list.isEmpty()) {
                return level.getNearestEntity(list, TargetingConditions.DEFAULT, null, getX(), getY(), getZ());
            }
            List<BSFSnowGolemEntity> list1 = level.getEntitiesOfClass(BSFSnowGolemEntity.class, aabb, p -> {
                if (p.getFixedTeamId() >= 0) {
                    if (savedData.getTeam(player.getUUID()) != p.getFixedTeamId()) {
                        return BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5;
                    }
                    return false;
                }
                if (p.getOwner() == null) {
                    return true;
                }
                return !p.getOwner().equals(player) && !savedData.isSameTeam(player, p.getOwner()) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5;
            });
            return level.getNearestEntity(list1, TargetingConditions.DEFAULT, null, getX(), getY(), getZ());
        }
        if (shooter instanceof BSFSnowGolemEntity snowGolem) {
            LivingEntity target = snowGolem.getTarget();
            if (target != null && (target.getType().equals(EntityType.PLAYER) || target.getType().equals(EntityRegister.BSF_SNOW_GOLEM.get()))) {
                return target;
            }
        }
        return null;

//        List<Player> list = level.getEntitiesOfClass(Player.class, aabb, p -> !p.isSpectator() && !p.equals(shooter) && !savedData.isSameTeam(shooter, p) && !(shooter instanceof BSFSnowGolemEntity golem && (p.equals(golem.getOwner()) || savedData.isSameTeam(golem.getOwner(), p))) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5);
//        if (!list.isEmpty()) {
//            return level.getNearestEntity(list, TargetingConditions.DEFAULT, null, getX(), getY(), getZ());
//        }
//        List<BSFSnowGolemEntity> list1 = level.getEntitiesOfClass(BSFSnowGolemEntity.class, aabb, p -> {
//            LivingEntity enemyGolemTarget = p.getTarget();      // 这个雪傀儡的目标
//            if (enemyGolemTarget == null) {
//                return false;
//            }
//            LivingEntity enemyGolemOwner = p.getOwner();        // 这个雪傀儡的主人
//            if (shooter instanceof BSFSnowGolemEntity golem) {
//
//                Entity golemOwner = golem.getOwner();
//                return enemyGolemTarget.equals(golemOwner) && !savedData.isSameTeam(golemOwner, enemyGolemOwner) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5;
//            }
//            return enemyGolemTarget.equals(shooter) && !savedData.isSameTeam(shooter, enemyGolemOwner) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5;
//        });
//        return level.getNearestEntity(list1, TargetingConditions.DEFAULT, null, getX(), getY(), getZ());
    }
}
