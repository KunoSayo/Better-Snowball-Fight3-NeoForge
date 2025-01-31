package com.linngdu664.bsf.entity.snowball.tracking;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class AbstractMonsterTrackingSnowballEntity extends AbstractTrackingSnowballEntity {
    public AbstractMonsterTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel, BSFSnowballEntityProperties pProperties, boolean isLockFeet) {
        super(pEntityType, pLevel, pProperties, isLockFeet);
    }

    public AbstractMonsterTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, LivingEntity pShooter, Level pLevel, BSFSnowballEntityProperties pProperties, boolean isLockFeet, RegionData region) {
        super(pEntityType, pShooter, pLevel, pProperties, isLockFeet, region);
    }

    @Override
    public Entity getTarget() {
        Level level = level();
        Vec3 velocity = getDeltaMovement();
        List<Mob> list = level.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(range), (p) -> p instanceof Enemy && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5);
        return getNearest(list);
    }
}
