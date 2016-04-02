package yousui115.db.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.db.DB;
import yousui115.db.Util_DB;
import yousui115.db.entity.EntityDB;

public class ItemDB extends ItemSword
{

    public ItemDB(ToolMaterial material)
    {
        super(material);
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    public int getColorFromItemStack(ItemStack stack, int renderPass)
//    {
//        //TODO 220fは何を指しているのか要調査
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220f, 220f);
//        return 16777215;
//    }

    /**
     * ■
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        //int n = ExtendedPlayerProperties.get(playerIn).getCountKill_Undead();
        int n = Util_DB.getCountKill_Undead(playerIn);
        tooltip.add("Undead Kill : " + n);

//        if (advanced)
//        {
//            //int i = ExtendedPlayerProperties.get(playerIn).getCountRepairAnvil();
//            int i = Util_DB.getCountRepairAnvil(playerIn);
//            tooltip.add("Repair Count : " + i);
//        }

        int i = Util_DB.getCountRepairAnvil(playerIn);
        tooltip.add("Repair Count : " + i);
    }

    /**
     * ■エンチャントエフェクト
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack)
    {
        //return false;
        return super.hasEffect(stack);
    }


    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        ItemStack stack = new ItemStack(itemIn, 1, 0);
        Enchantment enc = Enchantment.getEnchantmentByID(Util_DB.getID_Enc_BoD());
        if (enc != null && enc == DB.encBoD) { stack.addEnchantment(enc, enc.getMinLevel()); }

        subItems.add(stack);
    }

    /* ======================================== FORGE START =====================================*/

    /**
     * Player, Render pass, and item usage sensitive version of getIconIndex.
     *
     * @param stack The item stack to get the icon for.
     * @param player The player holding the item
     * @param useRemaining The ticks remaining for the active item.
     * @return Null to use default model, or a custom ModelResourceLocation for the stage of use.
     */
//    @SideOnly(Side.CLIENT)
//    @Override
//    public net.minecraft.client.resources.model.ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
//    {
//        return null;
//    }

    /**
     * ■EntityItemではなく、独自のEntityにしてドロップ(したい:true したくない:false)
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack)
    {
        return true;
    }

    /**
     * ■独自のEntityを返す
     *  @param location 本来出現するはずのEntityItem
     *  @param itemstack EntityItemに内包されている、このItemIDのItemStack
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        short mode = 1;
        BlockPos pos = new BlockPos(location.posX, location.posY - 1, location.posZ);
//        if (location.worldObj.getBlockState(pos).getBlock().equals(Blocks.air))
//        {
//            //■足場が無い
//            mode = 1;
//        }
        EntityDB sword = new EntityDB(location.worldObj, pos, location.rotationYaw, mode);
        sword.setEntityItemStack(itemstack);
//        event.entityPlayer.worldObj.spawnEntityInWorld(sword);
//        //entityItem.setDead();
//        entityItem.func_174870_v();

        return sword;
    }

}
