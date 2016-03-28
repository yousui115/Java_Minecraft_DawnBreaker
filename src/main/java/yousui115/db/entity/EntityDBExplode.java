package yousui115.db.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yousui115.db.Util_DB;
import yousui115.db.entity.ai.EntityAIAvoidPlayer;
import yousui115.db.network.MessageDW_BoD;
import yousui115.db.network.PacketHandler;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class EntityDBExplode extends EntityWeatherEffect
{
    //■トリガー(爆発発生源)
    protected Entity trigger;
    //■寿命
    protected int ticksMax;
    //■多段Hit防止用リスト
    protected List<Entity> hitEntities = Lists.newArrayList();


    public EntityDBExplode(World worldIn)
    {
        super(worldIn);
    }

    public EntityDBExplode(World worldIn, Entity entityIn)
    {
        this(worldIn);

        trigger = entityIn;

        ticksMax = 20;

        //■サイズの設定
        setSize(10.0F, 10.0F);

        //■爆心地
        setLocationAndAngles(trigger.posX, trigger.posY + trigger.height/2.0F, trigger.posZ, 0.0F, 0.0F);

        //■当たり判定エリアの補正
//        setEntityBoundingBox(AxisAlignedBB.fromBounds(this.posX - this.width  / 2,
//                                                      this.posY - this.height / 2,
//                                                      this.posZ - this.width  / 2,
//                                                      this.posX + this.width  / 2,
//                                                      this.posY + this.height / 2,
//                                                      this.posZ + this.width  / 2));
        setEntityBoundingBox(this.getEntityBoundingBox().expand(this.width / 2f, this.height / 2f, this.width / 2));

        //■爆心Entityは対象外
        hitEntities.add(this.trigger);
    }

    @Override
    public void onUpdate()
    {
        //■死 ぬ が よ い
//        if (this.getRidingEntity() != null) { this.getRidingEntity().setDead(); this. = null; }
//        if (this.riddenByEntity != null) { this.riddenByEntity.setDead(); this.riddenByEntity = null; }

        //■初回起動時にだけ行いたい処理
        if (this.firstUpdate)
        {
            //■1.発射音
            if (!this.worldObj.isRemote)
            {
                //TODO いい爆発音のSE探さないとなぁ。
                //trigger.worldObj.playSoundAtEntity(trigger, "random.explode", 4.0f, 0.5f);
                trigger.worldObj.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.entity_generic_explode, SoundCategory.BLOCKS, 4.0f, 0.5f);
            }
        }

        //■位置・回転情報の保存
        lastTickPosX = prevPosX = posX;
        lastTickPosY = prevPosY = posY;
        lastTickPosZ = prevPosZ = posZ;
        prevRotationPitch = rotationPitch;
        prevRotationYaw   = rotationYaw;

        //■位置調整
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        // ▼今まで無駄にnewしてた。これは酷い。
        //setPosition(posX, posY, posZ);

        //■寿命
        if (ticksExisted > ticksMax)
        {
            this.setDead();
        };

        //■当たり判定補正
        //if(!worldObj.isRemote)
        {
            checkHitMagic();
        }

        //========================
        //やりたい事はここから記述

        //■角度調整
        this.rotationYaw += 20.0F;

        //■初回起動フラグ off
        this.firstUpdate = false;
    }

    @Override
    protected void entityInit(){}
    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {}
    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {}

    /* ======================================== イカ、自作 =====================================*/

    /**
     * ■魔法の当たり判定処理
     */
    //@SideOnly(Side.SERVER)
    protected void checkHitMagic()
    {
        //■対象エリアのEntityをかき集める
        List<Entity> list = collectEntity();
        if (list == null) { return; }

        //■調べなくても良いEntityを除去
        list.removeAll(this.hitEntities);
        //■新規に取得したEntityを多段Hit防止用リストに追加
        this.hitEntities.addAll(list);

        //■集めたEntityはどんなものかなー？
        for (Entity target : list)
        {
            //■ダメージ判定を受けないEntityは吹き飛ぶ
            if (target.canBeCollidedWith() == false)
            {
                // ▼ベクトル
                double vecX = target.posX - this.posX;
                double vecY = target.posY - this.posY;
                double vecZ = target.posZ - this.posZ;
                // ▼距離
                double dist = (double)MathHelper.sqrt_double(vecX * vecX + vecY * vecY + vecZ * vecZ);
                // ▼補正
                double ofst = (7d - dist) * 0.1d;
                // ▼吹っ飛ぶ
                target.motionX = vecX * ofst;
                target.motionY = 0.5d;
                target.motionZ = vecZ * ofst;

                continue;
            }

            //■ここより下はサーバ側だけでよい。
            if (this.worldObj.isRemote) { continue; }

            //■ファイヤーボール＆投擲物を消し去る
            if (target instanceof EntityFireball ||
                target instanceof IProjectile)
            {
                target.setDead();
                continue;
            }

            //■生物はUndeadにのみ作用
            if (Util_DB.isUndead(target))
            {
                //■追加MOBがEntityLivingを継承してない場合は、個別対応が必要。超麺包
                if (!(target instanceof EntityLiving)) { continue; }

                EntityLiving living = (EntityLiving)target;

                boolean isEscape = false;
                for (EntityAITasks.EntityAITaskEntry entry : living.tasks.taskEntries)
                {
                    //■調教済み
                    if (entry.action instanceof EntityAIAvoidPlayer) { isEscape = true; break; }
                }

                if (!isEscape)
                {
                    //■調教
                    //  TODO スケさんが射撃体勢に入ってると逃げない。
                    //       リフレクション使ってtargetTasksを掃除しないといけないかも。
                    living.tasks.addTask(0, this.createAIAvoidPlayer(living));
                    living.targetTasks.addTask(0, this.createAIAvoidPlayer(living));

                    //■DWに記録
                    Util_DB.setAvoid(living);

                    //■Server -> Client
                    PacketHandler.INSTANCE.sendToAll(new MessageDW_BoD(living, Util_DB.getID_DW_BoD(), Util_DB.getDW_DB_Flag(living)));

                }
            }
        }
    }

    /**
     * ■EntityAIAvoidPlayer の生成
     * Playerを探す時に使用される。
     *  World.func_175674_a()内のgetEntitiesWithinAABBForEntity()に渡してる
     */
    public EntityAIBase createAIAvoidPlayer(EntityLiving living)
    {
        return new EntityAIAvoidPlayer(living, new Predicate()
        {
            public boolean func_179958_a(Entity entity)
            {
                //■プレイヤーなら逃げ出すよ。
                return entity instanceof EntityPlayer;
//                if (!(entity instanceof EntityPlayer)) { return false; }
//
//                //■プレイヤー かつ 手にうんこ持ってる
//                EntityPlayer player = (EntityPlayer)entity;
//                if (player.getCurrentEquippedItem() == null) { return false; }
//                return player.getCurrentEquippedItem().getItem() instanceof ItemUnko;
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.func_179958_a((Entity)p_apply_1_);
            }
        }, 15.0F, 1.0D, 1.25D);

    }


    protected List<Entity> collectEntity()
    {
        return worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
    }

    public int getTickMax() { return ticksMax; }
    public int getTriggerID() { return this.trigger.getEntityId(); }
}
