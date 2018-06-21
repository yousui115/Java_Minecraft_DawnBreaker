package yousui115.dawnbreaker.util;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;

public class DBUtils
{
    public static boolean isUndead(@Nullable EntityCreature creatureIn)
    {
        if (creatureIn == null) { return false; }

        if (creatureIn.isEntityUndead() == true)
        {
            return true;
        }
        //else if ... システム的にアンデッドじゃない奴とか

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
