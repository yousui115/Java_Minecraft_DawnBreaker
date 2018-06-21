package yousui115.dawnbreaker.recipe;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.util.DBUtils;

public class RecipesDawnbreaker extends ShapelessOreRecipe
{
    /**
     * ■　コンストラクタ群
     */
    public RecipesDawnbreaker(ResourceLocation group, ItemStack result, Object... recipe)
    {
        super(group, result, recipe);
    }

    /**
     * ■マッチン、グー！
     */
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        if (super.matches(inv, world) == true)
        {
            //■レシピはあってる。
            for (int i = 0; i < inv.getSizeInventory(); ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);
                if (DBUtils.isEnmptyStack(itemstack) == false
                    && itemstack.getItem() instanceof ItemDawnbreaker)
                {
                    //■出力の水筒は、入力の水筒のコピー品
//                    output = itemstack.copy();
//                    output.setItemDamage(0);


                    break;
                }
            }
            return true;
        }

        return false;
    }
    /**
     * ■ポットは空にして返すよ。
     */
//    @Override
//    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
//    {
//        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
//
//        for (int i = 0; i < nonnulllist.size(); ++i)
//        {
//            ItemStack itemstack = inv.getStackInSlot(i);
//
//            if (itemstack.getItem().hasContainerItem(itemstack))
//            {
//                nonnulllist.set(i, itemstack.getItem().getContainerItem(itemstack));
//            }
//        }
//
//        return nonnulllist;
//
//    }
}
