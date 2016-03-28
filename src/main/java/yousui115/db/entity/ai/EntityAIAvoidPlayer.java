package yousui115.db.entity.ai;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class EntityAIAvoidPlayer extends EntityAIBase
{
    public final Predicate field_179509_a = new Predicate()
    {
        private static final String __OBFID = "CL_00001575";
        public boolean func_180419_a(Entity p_180419_1_)
        {
            return true;//p_180419_1_.isEntityAlive() && EntityAIEscapeFromUnko.this.theEntity.getEntitySenses().canSee(p_180419_1_);
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.func_180419_a((Entity)p_apply_1_);
        }
    };
    /** The entity we are attached to */
    protected EntityLiving theEntity;
    private double farSpeed;
    private double nearSpeed;
    protected Entity closestLivingEntity;
    private float expandXZ;
    /** The PathEntity of our entity */
    private PathEntity entityPathEntity;
    /** The PathNavigate of our entity */
    private PathNavigate entityPathNavigate;
    private Predicate predicate;
    private static final String __OBFID = "CL_00001574";

    public EntityAIAvoidPlayer(EntityLiving entityLiving, Predicate predicate, float expandXZIn, double farSpeedIn, double nearSpeedIn)
    {
        this.theEntity = entityLiving;
        this.predicate = predicate;
        this.expandXZ = expandXZIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.entityPathNavigate = entityLiving.getNavigator();
        this.setMutexBits(15);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        List list = this.theEntity.worldObj.getEntitiesInAABBexcluding(this.theEntity,
                                                          this.theEntity.getEntityBoundingBox().expand((double)this.expandXZ,
                                                                                                       3.0D,
                                                                                                       (double)this.expandXZ),
                                                          Predicates.and(new Predicate[]
                                                          {
                                                              //IEntitySelector.NOT_SPECTATING,
                                                              //this.field_179509_a,
                                                             this.predicate
                                                          }
                                                          ));

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            //■条件に合う敵性Entity（第一発見Entity)
            this.closestLivingEntity = (Entity)list.get(0);

            //■敵性Entityの立ち位置
            Vec3d vec = new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ);

            //■逃走先の選出
            Vec3d vec3 = null;
            if (this.theEntity instanceof EntityCreature)
            {
                vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom((EntityCreature)this.theEntity, 16, 7, vec);
            }
            else
            {
                vec3 = findRandomTargetBlockAwayFrom(this.theEntity, 16, 2, vec);
            }
            //★逃走先が無い
            if (vec3 == null)
            {
                //■諦めが肝心
                return false;
            }
            //★寧ろ近づいてます
            else if (this.closestLivingEntity.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(this.theEntity))
            {
                //■身を捧げよ
                return false;
            }
            //★逃げ道があった
            else
            {
                //★逃走ルート構築
                this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);

                //■蜘蛛等のでかいサイズのMOBだと誤差が生じるので、
                //  PathEntityが生成出来たら問答無用でtrue!
                //return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3);
                return this.entityPathEntity == null ? false : true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entityPathNavigate.noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.closestLivingEntity = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0D)
        {
            this.theEntity.getNavigator().setSpeed(this.nearSpeed);
        }
        else
        {
            this.theEntity.getNavigator().setSpeed(this.farSpeed);
        }
    }

    /**
     * ■RandomPositionGenerator.findRandomTargetBlockAwayFrom のパクリ
     * @param p_75461_0_
     * @param p_75461_1_
     * @param p_75461_2_
     * @param p_75461_3_
     * @return
     */
    public static Vec3d findRandomTargetBlockAwayFrom(EntityLiving p_75461_0_, int p_75461_1_, int p_75461_2_, Vec3d p_75461_3_)
    {
        Vec3d staticVector = (new Vec3d(p_75461_0_.posX, p_75461_0_.posY, p_75461_0_.posZ)).subtract(p_75461_3_);
        /**
         * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
         * of par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
         */
        return findRandomTargetBlock(p_75461_0_, p_75461_1_, p_75461_2_, staticVector);
    }

    /**
     * ■RandomPositionGenerator.findRandomTargetBlock のパクリ
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    private static Vec3d findRandomTargetBlock(EntityLiving p_75462_0_, int p_75462_1_, int p_75462_2_, Vec3d p_75462_3_)
    {
        Random random = p_75462_0_.getRNG();
        boolean flag = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        float f = -99999.0F;
        boolean flag1 = false;

/*        if (p_75462_0_.hasHome())
        {
            double d0 = p_75462_0_.func_180486_cf().distanceSq((double)MathHelper.floor_double(p_75462_0_.posX), (double)MathHelper.floor_double(p_75462_0_.posY), (double)MathHelper.floor_double(p_75462_0_.posZ)) + 4.0D;
            double d1 = (double)(p_75462_0_.getMaximumHomeDistance() + (float)p_75462_1_);
            flag1 = d0 < d1 * d1;
        }
        else
        {
            flag1 = false;
        }
*/
        for (int l1 = 0; l1 < 10; ++l1)
        {
            int j1 = random.nextInt(2 * p_75462_1_ + 1) - p_75462_1_;
            int i2 = random.nextInt(2 * p_75462_2_ + 1) - p_75462_2_;
            int k1 = random.nextInt(2 * p_75462_1_ + 1) - p_75462_1_;

            if (p_75462_3_ == null || (double)j1 * p_75462_3_.xCoord + (double)k1 * p_75462_3_.zCoord >= 0.0D)
            {
                BlockPos blockpos;

/*                if (p_75462_0_.hasHome() && p_75462_1_ > 1)
                {
                    blockpos = p_75462_0_.func_180486_cf();
                    if (p_75462_0_.posX > (double)blockpos.getX())
                    {
                        j1 -= random.nextInt(p_75462_1_ / 2);
                    }
                    else
                    {
                        j1 += random.nextInt(p_75462_1_ / 2);
                    }
                    if (p_75462_0_.posZ > (double)blockpos.getZ())
                    {
                        k1 -= random.nextInt(p_75462_1_ / 2);
                    }
                    else
                    {
                        k1 += random.nextInt(p_75462_1_ / 2);
                    }
                }
*/
                j1 += MathHelper.floor_double(p_75462_0_.posX);
                i2 += MathHelper.floor_double(p_75462_0_.posY);
                k1 += MathHelper.floor_double(p_75462_0_.posZ);
                blockpos = new BlockPos(j1, i2, k1);

                if (!flag1 || true)//func_180485_d(p_75462_0_, blockpos))
                {
                    float f1 = 0.0f;//func_180484_a(p_75462_0_, blockpos);

                    if (f1 > f)
                    {
                        f = f1;
                        k = j1;
                        l = i2;
                        i1 = k1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return new Vec3d((double)k, (double)l, (double)i1);
        }
        else
        {
            return null;
        }
    }

/*    public static boolean func_180485_d(EntityLiving living, BlockPos p_180485_1_)
    {
        return this.maximumHomeDistance == -1.0F ? true : this.homePosition.distanceSq(p_180485_1_) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
    }
    public static float func_180484_a(EntityLiving living, BlockPos p_180484_1_)
    {
        return 0.0F;
    }*/


}