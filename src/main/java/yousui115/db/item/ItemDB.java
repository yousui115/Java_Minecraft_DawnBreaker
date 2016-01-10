package yousui115.db.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.db.DB;
import yousui115.db.Util_DB;

public class ItemDB extends ItemSword
{

    public ItemDB(ToolMaterial material)
    {
        super(material);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return 16777215;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        ItemStack stack = new ItemStack(itemIn, 1, 0);
        Enchantment enc = Enchantment.getEnchantmentById(Util_DB.getID_Enc_BoD());
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
    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.client.resources.model.ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        return null;
    }

}
