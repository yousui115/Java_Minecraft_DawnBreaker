package yousui115.dawnbreaker.item;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.dawnbreaker.entity.EntityDawnbreaker;
import yousui115.dawnbreaker.util.DBEnchs;
import yousui115.dawnbreaker.util.DBItems;

public class ItemDawnbreaker extends ItemSword
{
    //■高速破壊対象マテリアル
    public static final List<Material> MATERIALS = Lists.newArrayList(Material.LEAVES, Material.WEB);

    /**
     * ■コンストラクタ
     * @param material
     */
    public ItemDawnbreaker(ToolMaterial material)
    {
        super(material);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {

        Material material = state.getMaterial();

        if (MATERIALS.contains(material) == true)
        {
            return 200.0F;
        }
        else
        {
            return super.getDestroySpeed(stack, state);
        }
    }

    /**
     * ■
     */
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
    {
        Material material = state.getMaterial();

        int damage = 2;
        if ((double)state.getBlockHardness(worldIn, pos) == 0.0D)
        {
            damage = 0;
        }
        else if (MATERIALS.contains(material) == true)
        {
            damage = 1;
        }

        stack.damageItem(damage, entityLiving);

        return true;
    }

    //■Eventへ移動
//    @Override
//    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
//    {
//        //■アタッカーがプレイヤーなら
//        if (target != null && attacker instanceof EntityPlayer)
//        {
//            //※ダメージ処理を終えたEntityが来る。
//
//            //■直接トドメを刺した。
//            if (target.getHealth() <= 0.0f)
//            {
//                //■キルカウントアップは、ここでのみ行う。
//                if (attacker.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
//                {
//                    FaithHandler faith = (FaithHandler)attacker.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
//                    faith.addUndeadKillCount();
//                }
//            }
//        }
//
//        return super.hitEntity(stack, target, attacker);
//    }

    /**
     * ■ツールチップ(Eventへ移動)
     */
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(ItemStack stackIn, @Nullable World worldIn, List<String> tooltipIn, ITooltipFlag flagIn)
//    {
//        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
//        if (player == null) return;
//
//        FaithHandler faith = (FaithHandler)player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
//
//        tooltipIn.add("Undead Kill : " + faith.getUndeadKillCount());
//
//        tooltipIn.add("Repair Count : " + faith.getRepairDBCount());
//    }

    /**
     * ■エンチャントエフェクト
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) { return super.hasEffect(stack); }

    /**
     * ■クリエイティブタブへの登録
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        //■指定のタブのみ処理する
        if (this.isInCreativeTab(tab))
        {
            ItemStack stack = new ItemStack(DBItems.DAWNBREAKER);
            stack.addEnchantment(DBEnchs.ENCH_BOD, DBEnchs.ENCH_BOD.getMinLevel());

            items.add(stack);
        }
    }

    /**
     * ■エンチャント可能か否か
     */
    @Override
    public boolean isEnchantable(ItemStack stack) { return false; }

//    @Override
//    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
//    {
////        return !(this instanceof ItemSword);
//        return true;
//    }

    /* ======================================== FORGE START =====================================*/

    /**
     * ■EntityItemではなく、独自のEntityにしてドロップ(したい:true したくない:false)
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack) { return true; }

    /**
     * ■独自のEntityを返す
     *  @param location 本来出現するはずのEntityItem
     *  @param itemstack EntityItemに内包されている、このItemIDのItemStack
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        BlockPos pos = new BlockPos(location.posX, location.posY - 1, location.posZ);

        EntityDawnbreaker dawnbreaker = new EntityDawnbreaker(location.world, pos, location.rotationYaw);
        dawnbreaker.setEntityItemStack(itemstack);

        return dawnbreaker;
    }

    /**
     * ■修理回数に応じた特典
     *
     * 　さぁ、さらにアンデッドを浄化するのです、定命の者よ。
     *
     */
    public enum RepairOpt
    {
        NONE(0),
        DAMAGEx2(1),
        SLOW(2),
        DROPx2(3),
        DAMAGEx3(5),
        SLOW_EXPLODE(6),
        DROPx4(7),
        CRITICAL(8);

        private final int count;

        private RepairOpt(int countRepair)
        {
            count  = countRepair;
        }

        public boolean canAction(int countRepair)
        {
            return count <= countRepair;
        }
    }
}