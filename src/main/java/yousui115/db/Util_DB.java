package yousui115.db;

import java.util.Date;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

/**
 * DB = Dawn Breaker だよ。DataBaseじゃないよ。
 * @author yousui
 *
 */
public class Util_DB
{
    //■昔、こんな感じの記述をして「安直だね」と言われた記憶がある。知るか。
    public static Random rnd = new Random((new Date()).getTime());

    /**
     * ■対象はUndeadである
     *   (Mod追加MOBにUndead属性が無くても、ここで指定してやればUndeadという事に出来る)
     * @param target
     * @return
     */
    public static boolean isUndead(Entity target)
    {
        if (target instanceof EntityLivingBase &&
            ((EntityLivingBase)target).isEntityUndead())
        {
            return true;
        }

        return false;
    }

    //■マスク

    //■データウォッチャー
    private static int id_dw_BoD = 29;
    public static void setID_DW_BoD(int id) { id_dw_BoD = id; }
    public static int  getID_DW_BoD() { return id_dw_BoD; }

    public static final int MASK_DW_DB_AVOID = 0x01;
    public static final int MASK_DW_DB_UEC   = 0x02;
    public static int getDW_DB_Flag(Entity target)
    {
        //int n = 0;
        byte n = 0;
        try
        {
            //n = target.getDataWatcher().getWatchableObjectInt(id_dw_BoD);
            n = target.getDataManager().get(DB.DP_DB_FLAGS);
        }
        catch (Throwable e) {}
        return (int)n;
    }
    private static boolean getDW_DB_Flag(Entity target, int mask) { return (getDW_DB_Flag(target) & mask) == mask; }
    public static boolean hasExplodeChance(Entity target) { return getDW_DB_Flag(target, MASK_DW_DB_UEC); }
    public static boolean isAvoid(Entity target) { return getDW_DB_Flag(target, MASK_DW_DB_AVOID); }

    public static void setDW_DB_Flag(Entity target, int mask)
    {
        try
        {
            //int n = target.getDataWatcher().getWatchableObjectInt(id_dw_BoD);
            byte n = target.getDataManager().get(DB.DP_DB_FLAGS);
            n |= mask;
            //target.getDataWatcher().updateObject(id_dw_BoD, Integer.valueOf(n));
            target.getDataManager().set(DB.DP_DB_FLAGS, n);
        }
        catch (Throwable e)
        {
            //target.getDataWatcher().addObject(id_dw_BoD, Integer.valueOf(mask));
            //入らないはずー
        }
    }
    public static void setExplodeChance(Entity target) { setDW_DB_Flag(target, MASK_DW_DB_UEC); }
    public static void setAvoid(Entity target) { setDW_DB_Flag(target, MASK_DW_DB_AVOID); }


    //■エンチャントID
    private static int id_enc_BoD = 200;
    public static void setID_Enc_BoD(int id) { id_enc_BoD = id; }
    public static int  getID_Enc_BoD() { return id_enc_BoD; }

    /* ======================================= */

    public final static String EXT_PROP_NAME = "UndeadKillData";

    public final static String KEY_CK_U = "countKill_Undead";

    public final static String KEY_RA = "countRepairAnvil";

    public static int getCountKill_Undead(NBTTagCompound entityData)
    {
        NBTTagCompound nbt = (NBTTagCompound)entityData.getTag(EXT_PROP_NAME);
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            entityData.setTag(EXT_PROP_NAME, nbt);
        }
        if (!nbt.hasKey(KEY_CK_U))
        {
            nbt.setInteger(KEY_CK_U, 0);
        }
        return nbt.getInteger(KEY_CK_U);
    }
    public static int getCountKill_Undead(EntityPlayer playerIn)
    {
//         NBTTagCompound nbt = (NBTTagCompound)playerIn.getEntityData().getTag(EXT_PROP_NAME);
//         return nbt == null ? 0 : nbt.getInteger(KEY_CK_U);
        return getCountKill_Undead(playerIn.getEntityData());
    }
    public static void addCountKill_Undead(EntityPlayer playerIn, int add)
    {
        setCountKill_Undead(playerIn, addCount(add, Integer.MAX_VALUE - 1, getCountKill_Undead(playerIn), true) );
    }
    protected static void setCountKill_Undead(EntityPlayer playerIn, int countIn)
    {
        NBTTagCompound entityData = playerIn.getEntityData();
        NBTTagCompound nbt = (NBTTagCompound)entityData.getTag(EXT_PROP_NAME);
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            entityData.setTag(EXT_PROP_NAME, nbt);
        }
        nbt.setInteger(KEY_CK_U, countIn);
    }


    public static int getCountRepairAnvil(NBTTagCompound entityData)
    {
        NBTTagCompound nbt = (NBTTagCompound)entityData.getTag(EXT_PROP_NAME);
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            entityData.setTag(EXT_PROP_NAME, nbt);
        }
        if (!nbt.hasKey(KEY_RA))
        {
            nbt.setInteger(KEY_RA, 0);
        }

        return nbt.getInteger(KEY_RA);
    }
    public static int getCountRepairAnvil(EntityPlayer playerIn)
    {
//        NBTTagCompound nbt = (NBTTagCompound)playerIn.getEntityData().getTag(EXT_PROP_NAME);
//        return nbt.getInteger(KEY_RA);
        return getCountRepairAnvil(playerIn.getEntityData());
    }
    public static void addCountRepairAnvil(EntityPlayer playerIn, int add)
    {
        setCountRepairAnvil(playerIn, addCount(add, Integer.MAX_VALUE - 1, getCountRepairAnvil(playerIn), true) );
    }

    protected static void setCountRepairAnvil(EntityPlayer playerIn, int countIn)
    {
//        NBTTagCompound nbt = (NBTTagCompound)playerIn.getEntityData().getTag(EXT_PROP_NAME);
        NBTTagCompound entityData = playerIn.getEntityData();
        NBTTagCompound nbt = (NBTTagCompound)entityData.getTag(EXT_PROP_NAME);
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            entityData.setTag(EXT_PROP_NAME, nbt);
        }

        nbt.setInteger(KEY_RA, countIn);
    }

    //■桁あふれ防止
    private static int addCount(int exp, int limit, int count, boolean isPositive)
    {
        int num = count + (limit - count > exp ? exp : limit - count);
        return isPositive ? MathHelper.clamp_int(num, 0, limit) : num;
    }

    /* ========================================== */
    public static void getNBTData(EntityPlayer playerIn, NBTTagCompound compound)
    {
        NBTTagCompound nbt = new NBTTagCompound();

        //■
        nbt.setInteger(KEY_CK_U, getCountKill_Undead(playerIn));
        nbt.setInteger(KEY_RA, getCountRepairAnvil(playerIn));

        compound.setTag(EXT_PROP_NAME, nbt);
    }

    public static void setNBTData(EntityPlayer playerIn, NBTTagCompound compound)
    {
        NBTTagCompound nbt = playerIn.getEntityData();
        NBTTagCompound nbt2 = new NBTTagCompound();

        nbt2.setInteger(KEY_CK_U, getCountKill_Undead(compound));
        nbt2.setInteger(KEY_RA, getCountRepairAnvil(compound));

        nbt.setTag(EXT_PROP_NAME, nbt2);
    }
}
