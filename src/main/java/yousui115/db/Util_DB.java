package yousui115.db;

import java.util.Date;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

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
    private static boolean getDW_DB_Flag(Entity target, int mask)
    {
        boolean flag = false;
        try
        {
            int n = target.getDataWatcher().getWatchableObjectInt(id_dw_BoD);
            flag = (n & mask) > 0;
        }
        catch (Throwable e) {}
        return flag;
    }
    public static boolean hasExplodeChance(Entity target) { return getDW_DB_Flag(target, MASK_DW_DB_UEC); }
    public static boolean isAvoid(Entity target) { return getDW_DB_Flag(target, MASK_DW_DB_AVOID); }

    public static void setDW_DB_Flag(Entity target, int mask)
    {
        try
        {
            int n = target.getDataWatcher().getWatchableObjectInt(id_dw_BoD);
            n |= mask;
            target.getDataWatcher().updateObject(id_dw_BoD, Integer.valueOf(n));
        }
        catch (Throwable e)
        {
            target.getDataWatcher().addObject(id_dw_BoD, Integer.valueOf(mask));
        }
    }
    public static void setExplodeChance(Entity target) { setDW_DB_Flag(target, MASK_DW_DB_UEC); }
    public static void setAvoid(Entity target) { setDW_DB_Flag(target, MASK_DW_DB_AVOID); }


    //■エンチャントID
    private static int id_enc_BoD = 200;
    public static void setID_Enc_BoD(int id) { id_enc_BoD = id; }
    public static int  getID_Enc_BoD() { return id_enc_BoD; }
}
