package yousui115.dawnbreaker.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
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
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.util.DBItems;
import yousui115.dawnbreaker.util.DBUtils;

public class EntityDawnbreaker extends Entity
{
    float fYOffset;
    float fYawOffset;

    private static final DataParameter<ItemStack> DB_ITEMSTACK = EntityDataManager.<ItemStack>createKey(EntityDawnbreaker.class, DataSerializers.ITEM_STACK);

    /**
     * ■コンストラクタ(ロード)
     * @param worldIn
     */
    public EntityDawnbreaker(World worldIn)
    {
        super(worldIn);
    }

    /**
     * ■コンストラクタ(生成)
     */
    public EntityDawnbreaker(World worldIn, BlockPos posIn, float yawIn)
    {
        this(worldIn);

        setLocationAndAngles(posIn.getX() + 0.5, posIn.getY() + 1, posIn.getZ() + 0.5, -yawIn, 0);

        //■とりあえず、空っぽのItemStackを突っ込んでおく
        this.setEntityItemStack(ItemStack.EMPTY);
    }

    /**
     * ■
     */
    @Override
    protected void entityInit()
    {
        //■サイズ設定
        setSize(0.5F, 0.5F);

        //■ItemStack保持用D
        this.getDataManager().register(DB_ITEMSTACK, ItemStack.EMPTY);

        //■火耐性
        this.isImmuneToFire = true;
    }

    /**
     * ■
     */
    @Override
    public void onUpdate()
    {
        //■Dawnbreakerを保持してる事があいでんちちー
        ItemStack stack = this.getEntityItemStack();
        if (DBUtils.isEnmptyStack(stack) || stack.getItem() != DBItems.DAWNBREAKER)
        {
            this.setDead();
            return;
        }

        //■奈落
        if (this.posY < -64.0D)
        {
            this.setDead();

            //■クライアント側のみ
            if (this.world.isRemote)
            {
                //■悲しいお知らせ
                Dawnbreaker.proxy.getPlayer().sendMessage(new TextComponentTranslation(stack.getDisplayName() + " was lost.", new Object[0]));
            }

            return;
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
        return true;
    }

    /**
     * ■貰ったダメージに対しての耐性が(ある:true ない:false)
     */
    @Override
    public boolean isEntityInvulnerable(DamageSource sourceIn)
    {
        return true;
    }

    /**
     * ■プレイヤーが右クリックすると呼ばれる
     */
    @Override
    public boolean processInitialInteract(EntityPlayer playerIn, EnumHand handIn)
    {
        //■前提条件
        if (handIn != EnumHand.MAIN_HAND &&
            DBUtils.isEnmptyStack(getEntityItemStack()) == true) { return false; }

        //■サーバーのみ
        if (world.isRemote == false)
        {
            //■両手に物持った状態で掴もうとするな
            ItemStack main = playerIn.getHeldItemMainhand();
            ItemStack off = playerIn.getHeldItemOffhand();
            if (DBUtils.isEnmptyStack(main) == false && DBUtils.isEnmptyStack(off) == false)
            {
                return false;
            }

            //■素手なので、そのままItemStackを突っ込む
            EntityEquipmentSlot equipSlot = DBUtils.isEnmptyStack(main) == true ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
            playerIn.setItemStackToSlot(equipSlot, this.getEntityItemStack());

            //■残骸
            setEntityItemStack(ItemStack.EMPTY);

            this.setDead();
        }

        return true;
    }

    //■タグ名
    protected static final String NBTTAG_ITEM = "Item";

    /**
     * ■
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        NBTTagCompound nbttagcompound = tagCompund.getCompoundTag(NBTTAG_ITEM);
        setEntityItemStack(new ItemStack(nbttagcompound));

        if (getEntityItemStack().isEmpty())
        {
            this.setDead();
        }
    }

    /**
     * ■
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        if (this.getEntityItemStack() != null)
        {
            tagCompound.setTag(NBTTAG_ITEM, this.getEntityItemStack().writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * ■描画距離内に存在しているか
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRender3d(double x, double y, double z)
    {
        //■常に描画で
        return true;
    }


    /**
     * ■ピストンで押されたら呼ばれる
     */
    @Override
    public void move(MoverType type, double x, double y, double z){}

    /**
     * ■Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     *   ブロックの押し出し処理
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport){}


    /**
     * ■Called by portal blocks when an entity is within it.
     */
    @Override
    public void setPortal(BlockPos pos) {}

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
     * ■If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canBeAttackedWithItem()
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



    //=============================================================================


    /**
     * ■保持してるItemStackを取得
     *   EntityItemを真似て作成。
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    public ItemStack getEntityItemStack()
    {
        ItemStack itemstack = this.getDataManager().get(this.DB_ITEMSTACK);
        return itemstack;
    }

    /**
     * ■受け取ったItemStackを保持
     *   Entityitemを(ry
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(ItemStack stack)
    {
        this.getDataManager().set(DB_ITEMSTACK, stack);
        this.getDataManager().setDirty(DB_ITEMSTACK);
    }
}