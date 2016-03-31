package yousui115.db.entity;

import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.db.DB;
import yousui115.db.item.ItemDB;

import com.google.common.base.Optional;

public class EntityDB extends Entity
{
    float fYOffset;
    float fYawOffset;

    public static final DataParameter<Optional<ItemStack>> DB_ITEMSTACK = EntityDataManager.<Optional<ItemStack>>createKey(EntityDB.class, DataSerializers.OPTIONAL_ITEM_STACK);

    /**
     * ■コンストラクタ(ロード)
     * @param worldIn
     */
    public EntityDB(World worldIn)
    {
        super(worldIn);
    }

    /**
     * ■コンストラクタ(生成)
     * @param worldIn : マイクラ世界
     * @param posIn : 基本的にプレイヤーの足元を指す
     * @param yawIn : プレイヤーのY軸角度 (Y軸回転はPitchにして下さい。嵌ってしまいます
     */
    public EntityDB(World worldIn, BlockPos posIn, float yawIn, short modeIn)
    {
        this(worldIn);

        setLocationAndAngles(posIn.getX() + 0.5, posIn.getY() + 1, posIn.getZ() + 0.5, -yawIn, 0);

        //■とりあえず、空っぽのItemStackを突っ込んでおく
        //this.setEntityItemStack(new ItemStack(Blocks.air, 0));
        this.setEntityItemStack(new ItemStack(Blocks.air, 0));

        //■
        //this.setEntityMode(modeIn);
    }

    /**
     * ■
     */
    @Override
    protected void entityInit()
    {
        //■サイズ設定
        setSize(0.5F, 0.5F);

        //■ItemStack保持用DataWatcher領域の確保(5:ItemStack)
//        this.getDataWatcher().addObjectByDataType(10, 5);
        this.getDataManager().register(DB_ITEMSTACK, Optional.<ItemStack>absent());

        //■突き刺しモードか浮遊モードか(1:short)
//        this.getDataWatcher().addObjectByDataType(11, 1);

        //■火耐性
        this.isImmuneToFire = true;

        //this.noClip = false;
    }

    /**
     * ■
     */
    @Override
    public void onUpdate()
    {
        //■死 ぬ が よ い
//        if (this.getRidingEntity() != null) { this.getRidingEntity().setDead(); dismountRidingEntity(); }
//        if (this.riddenByEntity != null) { this.riddenByEntity.setDead(); this.riddenByEntity = null; }

        //■ItemKFSのItemStackを保持してる事があいでんちちー
        ItemStack stack = this.getEntityItemStack();
        if (stack == null || !(stack.getItem() instanceof ItemDB))
        {
            this.setDead();
            return;
        }

        //■奈落
        if (this.posY < -64.0D)
        {
            //■消滅
            this.setDead();

            //■クライアント側のみ
            if (this.worldObj.isRemote)
            {
                //■悲しいお知らせ
                String s = stack.getItem().getItemStackDisplayName(stack);
                //DB.proxy.getEntityPlayerInstance().addChatMessage(new ChatComponentText(s + " was lost."));
                EntityPlayerSP sp = (EntityPlayerSP)DB.proxy.getPlayer();
                sp.addChatComponentMessage(new TextComponentTranslation(s + " was lost.", new Object[0]));
            }
        }

        //■1tick前の情報を保持
        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        //■初回起動フラグをへし折る
        this.firstUpdate = false;
    }


    /**
     * ■当り判定が仕事するか否か
     */
    @Override
    public boolean canBeCollidedWith()
    {
        //仕事するのでtrue
        return true;
    }

    /**
     * ■貰ったダメージに対しての耐性が(ある:true ない:false)
     */
    @Override
    public boolean isEntityInvulnerable(DamageSource p_180431_1_)
    {
        return true;
    }

    /**
     * ■プレイヤーが右クリックすると呼ばれる
     */
    @Override
    public boolean processInitialInteract(EntityPlayer playerIn, ItemStack stackIn, EnumHand handIn)
    {
        //ItemStack currentItem = playerIn.getCurrentEquippedItem();
        if (stackIn != null) { return false; }

        if (!this.worldObj.isRemote)
        {
            //■素手なので、そのままItemStackを突っ込む
            //playerIn.setCurrentItemOrArmor(0, this.getEntityItemStack());
            playerIn.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, this.getEntityItemStack());
            this.setDead();
        }

        return true;
    }

    //■タグ名
    protected static final String NBTTAG_ITEM = "Item";
    protected static final String NBTTAG_MODE = "Mode";

    /**
     * ■
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        //■ItemStack
        NBTTagCompound tagItem = tagCompund.getCompoundTag(NBTTAG_ITEM);
        this.setEntityItemStack(ItemStack.loadItemStackFromNBT(tagItem));
        //ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);
        ItemStack item = this.getDataManager().get(this.DB_ITEMSTACK).orNull();
        if (item == null || item.stackSize <= 0) this.setDead();

        //■Mode
        //this.setEntityMode(1);
    }

    /**
     * ■
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        //■ItemStack
        if (this.getEntityItemStack() != null)
        {
            tagCompound.setTag(NBTTAG_ITEM, this.getEntityItemStack().writeToNBT(new NBTTagCompound()));
        }

        //■Mode
        //tagCompound.setShort(NBTTAG_MODE, this.getEntityMode());
    }

    /**
     * ■描画距離内に存在しているか
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z)
    {
        //■常に描画で
        return true;
    }


    /**
     * ■ピストンで押されたら呼ばれる
     */
    @Override
    public void moveEntity(double x, double y, double z){}

    /**
     * ■Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     *   ブロックの押し出し処理
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotation2(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_){}

    /**
     * ■Called by portal blocks when an entity is within it.
     */
//    @Override
//    public void setInPortal() {}

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    @Override
    public boolean isInsideOfMaterial(Material materialIn)
    {
        return false;
    }

    /**
     * ■ブロック内からの追い出し
     */
    @Override
    protected boolean pushOutOfBlocks(double par1, double par3, double par5)
    {
        return false;
    }

    /**
     * ■Sets the Entity inside a web block.
     */
    @Override
    public void setInWeb() {}

    /**
     * ■Gets the name of this command sender (usually username, but possibly "Rcon")
     */
//    @Override
//    public String getName()
//    {
//        return this.hasCustomName() ? this.getCustomNameTag() : StatCollector.translateToLocal("item." + this.getEntityItemStack().getUnlocalizedName());
//    }

    /**
     * ■If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * ■Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
//    @Override
//    public void travelToDimension(int dimensionId)
//    {
//        //■移動はしない
//    }
    /* ======================================== イカ、自作 =====================================*/

    /**
     * ■保持してるItemStackを取得
     *   EntityItemを真似て作成。
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    public ItemStack getEntityItemStack()
    {
        //ItemStack itemstack = this.getDataWatcher().getWatchableObjectItemStack(10);
        ItemStack itemstack = this.getDataManager().get(this.DB_ITEMSTACK).orNull();

        return itemstack;
    }

    /**
     * ■受け取ったItemStackを保持
     *   Entityitemを(ry
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(ItemStack stack)
    {

//        this.getDataWatcher().updateObject(10, stack);
//        this.getDataWatcher().setObjectWatched(10);
//        this.getDataManager().set(this.DB_ITEMSTACK, Optional.fromNullable(stack));
        this.getDataManager().set(DB_ITEMSTACK, Optional.fromNullable(stack));
        this.getDataManager().setDirty(DB_ITEMSTACK);

    }

//    public short getEntityMode()
//    {
//        return this.getDataWatcher().getWatchableObjectShort(11);
//    }
//
//    public void setEntityMode(short mode)
//    {
//        this.getDataWatcher().updateObject(11, mode);
//        this.getDataWatcher().setObjectWatched(11);
//    }
}