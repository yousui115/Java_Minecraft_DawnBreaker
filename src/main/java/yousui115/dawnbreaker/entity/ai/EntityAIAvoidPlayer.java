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
import yousui115.dawnbreaker.capability.undead.CapabilityExplodeHandler;
import yousui115.dawnbreaker.capability.undead.IExplodeHandler;

public class EntityAIAvoidPlayer extends EntityAIAvoidEntity<EntityPlayer>
{
    protected IExplodeHandler exp;

    private final double farSpeed;
    private final double nearSpeed;
    private final float avoidDistance;

    private final Predicate<Entity> canBeSeenSelector;
    private final Predicate <EntityPlayer> avoidTargetSelector;


    public EntityAIAvoidPlayer(EntityCreature entityIn, Class<EntityPlayer> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    public EntityAIAvoidPlayer(EntityCreature entityIn, Class<EntityPlayer> classToAvoidIn, Predicate <EntityPlayer> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        //■
        super(entityIn, classToAvoidIn, avoidTargetSelectorIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);

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
        if (entity.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == true)
        {
            exp = (IExplodeHandler)entity.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);
        }
    }

    @Override
    public boolean shouldExecute()
    {
        //■爆発情報が無いと実行しない
        if (exp == null) { return false; }

        if (exp.getTickAvoid() <= 0) { return false; }

        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return exp.getTickAvoid() > 0;
    }

    @Override
    public void resetTask()
    {
        exp.resetAvoid();

        super.resetTask();
    }

    @Override
    public void updateTask()
    {
        exp.setTickAvoid(exp.getTickAvoid() - 1);

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
}
