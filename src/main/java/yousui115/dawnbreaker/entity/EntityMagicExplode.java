package yousui115.dawnbreaker.entity;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yousui115.dawnbreaker.capability.undead.CapabilityExplodeHandler;
import yousui115.dawnbreaker.capability.undead.IExplodeHandler;
import yousui115.dawnbreaker.util.DBUtils;

public class EntityMagicExplode extends EntityWeatherEffect
{
    //■トリガー(爆発発生源)
    protected Entity trigger;
    //■寿命
    protected int ticksMax;
    //■多段Hit防止用リスト
    protected List<Entity> hitEntities = Lists.newArrayList();


    public EntityMagicExplode(World worldIn) { super(worldIn); }
    public EntityMagicExplode(World worldIn, Entity entityIn)
    {
        this(worldIn);

        trigger = entityIn;

        ticksMax = 20;

        //■爆心地
        setLocationAndAngles(trigger.posX, trigger.posY + trigger.height/2.0F, trigger.posZ, 0.0F, 0.0F);

        //■サイズの設定
        setSize(10.0F, 10.0F);

        //■当たり判定エリアの補正
        setEntityBoundingBox(this.getEntityBoundingBox().grow(this.width / 2f, this.height / 2f, this.width / 2));

        //■爆心Entityは対象外
        hitEntities.add(this.trigger);
    }

    @Override
    public void onUpdate()
    {
      //■初回起動時にだけ行いたい処理
        if (this.firstUpdate)
        {
            //■爆発音
            if (this.world.isRemote == false)
            {
                trigger.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.AMBIENT, 4.0f, 0.5f);
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

        //■寿命
        if (ticksExisted > ticksMax)
        {
            this.setDead();
        };

        //■当たり判定補正
        checkHitMagic();

        //■角度調整
        this.rotationYaw += 20.0F;

        //■初回起動フラグ off
        this.firstUpdate = false;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}

    //========================== イカ、自作 =====================

    /**
     * ■当たり判定処理
     */
    //@SideOnly(Side.SERVER)
    protected void checkHitMagic()
    {
        //■サーバーのみ
        if (this.world.isRemote == true) { return; }

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
                double dist = (double)MathHelper.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
                // ▼補正
                double ofst = (7d - dist) * 0.1d;
                // ▼吹っ飛ぶ
                target.motionX = vecX * ofst;
                target.motionY = 0.5d;
                target.motionZ = vecZ * ofst;

                continue;
            }

            //■ファイヤーボール＆投擲物を消し去る
            if (target instanceof EntityFireball || target instanceof IProjectile)
            {
                target.setDead();
                continue;
            }

            //■生物はUndeadにのみ作用
            if (target instanceof EntityCreature && DBUtils.isUndead((EntityCreature)target) == true)
            {
                EntityCreature undead = (EntityCreature)target;

                if (undead.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == true)
                {
                    IExplodeHandler exp = (IExplodeHandler)undead.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);

                    exp.setAvoid();
                }
            }
        }
    }


    protected List<Entity> collectEntity()
    {
        return world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
    }

    public int getTickMax() { return ticksMax; }
    public int getTriggerID() { return this.trigger.getEntityId(); }
}
