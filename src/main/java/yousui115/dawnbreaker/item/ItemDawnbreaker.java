package yousui115.dawnbreaker.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
    /**
     * ■コンストラクタ
     * @param material
     */
    public ItemDawnbreaker(ToolMaterial material)
    {
        super(material);
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

}