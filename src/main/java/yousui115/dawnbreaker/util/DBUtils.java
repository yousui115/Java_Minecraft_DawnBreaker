package yousui115.dawnbreaker.util;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yousui115.dawnbreaker.item.ItemDawnbreaker;

public class DBUtils
{
    public static boolean isUndead(@Nullable Entity entityIn)
    {
        if (entityIn instanceof EntityCreature == false) { return false; }

        EntityCreature creature = (EntityCreature)entityIn;

        if (creature.isEntityUndead() == true)
        {
            return true;
        }
        //else if ... システム的にアンデッドじゃない奴とか

        return false;
    }

    /**
     * ■Dawnbreaker(Break of Dawn付き)か否か
     * @param stackIn
     * @return
     */
    public static boolean isDBwithBoD(@Nullable ItemStack stackIn)
    {
        //■このItemStackがDawnbreakerであるか否か
        if (DBUtils.isEnmptyStack(stackIn) == true ||
            stackIn.getItem() instanceof ItemDawnbreaker == false)
        { return false; }

        //■エンチャント「Break of dawn」が付与されているか否か
        NBTTagList enchList = stackIn.getEnchantmentTagList();
        if (enchList.tagCount() != 1) { return false; }

        NBTTagCompound nbttagcompound = enchList.getCompoundTagAt(0);
        int enchID = nbttagcompound.getShort("id");

        //■かんぺこ
        if (DBEnchs.ENCH_BOD == Enchantment.getEnchantmentByID(enchID))
        {
            return true;
        }

        return false;
    }

    /**
     * ■ItemStackが空か否か(null対策含む)
     * @param stackIn
     * @return
     */
    public static boolean isEnmptyStack(@Nullable ItemStack stackIn)
    {
        return stackIn == null || stackIn == ItemStack.EMPTY || stackIn.isEmpty();
    }
}
