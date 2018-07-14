package yousui115.dawnbreaker.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;
import yousui115.dawnbreaker.capability.undead.CapabilityUndeadHandler;
import yousui115.dawnbreaker.capability.undead.IUndeadHandler;

public class EntityAIAvoidPlayer extends EntityAIAvoidEntity<EntityPlayer>
{
    protected IUndeadHandler hdlUndead;

    private double farSpeed;
    private double nearSpeed;
    private final float avoidDistance;

    private final Predicate<Entity> canBeSeenSelector;
    private final Predicate <EntityPlayer> avoidTargetSelector;


    public EntityAIAvoidPlayer(EntityCreature entityIn, Class<EntityPlayer> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    public EntityAIAvoidPlayer(EntityCreature entityIn, Class<EntityPlayer> classToAvoidIn, Predicate<EntityPlayer> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        //■
        super(entityIn, classToAvoidIn, avoidTargetSelectorIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);

        this.setMutexBits(~0x0);

        //■
        this.canBeSeenSelector = new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return p_apply_1_.isEntityAlive() && EntityAIAvoidPlayer.this.entity.getEntitySenses().canSee(p_apply_1_) && !EntityAIAvoidPlayer.this.entity.isOnSameTeam(p_apply_1_);
            }
        };
        farSpeed = farSpeedIn;
        nearSpeed = nearSpeedIn;
        avoidDistance = avoidDistanceIn;
        avoidTargetSelector = avoidTargetSelectorIn;

        //■
        if (entity.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null) == true)
        {
            hdlUndead = (IUndeadHandler)entity.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);
        }
    }

    @Override
    public boolean shouldExecute()
    {
        //■爆発情報が無いと実行しない
        if (hdlUndead == null) { return false; }

        if (hdlUndead.getTickAvoid() <= 0) { return false; }

        super.shouldExecute();

        if (this.closestLivingEntity == null && this.entity.getAttackTarget() instanceof EntityPlayer)
        {
            this.closestLivingEntity = (EntityPlayer)this.entity.getAttackTarget();
        }

        if (this.closestLivingEntity == null) { return false; }

        return true;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return hdlUndead.getTickAvoid() > 0;
    }

    @Override
    public boolean isInterruptible()
    {
        return false;
    }

    @Override
    public void resetTask()
    {
        hdlUndead.resetAvoid();

        super.resetTask();
    }

    @Override
    public void updateTask()
    {
        hdlUndead.setTickAvoid(hdlUndead.getTickAvoid() - 1);

        super.updateTask();

        if (this.entity.getNavigator().noPath() == true)
        {
            entity.getNavigator().setPath(getPath(), farSpeed);
        }
    }

    //=======================================================

    protected Path getPath()
    {
        List<EntityPlayer> list = this.entity.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, this.entity.getEntityBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), Predicates.and(EntitySelectors.CAN_AI_TARGET, this.canBeSeenSelector, this.avoidTargetSelector));

        if (list.isEmpty())
        {
            return null;
        }
        else
        {
            this.closestLivingEntity = list.get(0);
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));

            if (vec3d == null)
            {
                return null;
            }
            else if (this.closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.getDistanceSq(this.entity))
            {
                return null;
            }
            else
            {
                return entity.getNavigator().getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
            }
        }
    }

    public void setFarSpeed(double farSpeedIn)
    {
        this.farSpeed = farSpeedIn;
    }
}
