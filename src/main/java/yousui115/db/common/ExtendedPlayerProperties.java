package yousui115.db.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayerProperties implements IExtendedEntityProperties
{
    /* MOD固有の文字列。EntityPlayerに登録時に使用。
    MOD内で複数のIExtendedEntityPropertiesを使う場合は、別の文字列をそれぞれ割り当てること。*/
     public final static String EXT_PROP_NAME = "UndeadKillData";

     private final static String KEY_CK_U = "countKill_Undead";
     private int countUndeadKill = 0;

     private final static String KEY_RA = "countRepairAnvil";
     private int countRepairAnvil = 0;

     /*EntityPlayerにIExtendedEntityPropertiesを登録。登録文字列はMOD固有のものを割り当てること*/
     public static void register(EntityPlayer player)
     {
         player.registerExtendedProperties(EXT_PROP_NAME, new ExtendedPlayerProperties());
     }

     /*IExtendedEntityPropertiesをEntityPlayerインスタンスから取得する*/
     public static ExtendedPlayerProperties get(EntityPlayer player)
     {
         return (ExtendedPlayerProperties)player.getExtendedProperties(EXT_PROP_NAME);
     }


     @Override
     public void saveNBTData(NBTTagCompound compound)
     {
         NBTTagCompound nbt = new NBTTagCompound();

         //■
         nbt.setInteger(KEY_CK_U, this.countUndeadKill);
         nbt.setInteger(KEY_RA, this.countRepairAnvil);

         compound.setTag(EXT_PROP_NAME, nbt);

         //■custumDataにデータを移植
         NBTTagCompound customNBT;
         if (compound.hasKey("ForgeData"))
         {
             //■ForgeDataTagが既にある。
             customNBT = (NBTTagCompound)compound.getTag("ForgeData");
         }
         else
         {
             //■ForgeDataTagが無い。
             customNBT = new NBTTagCompound();
             compound.setTag("ForgeData", customNBT);
         }
         customNBT.setTag(EXT_PROP_NAME, nbt);
     }

     @Override
     public void loadNBTData(NBTTagCompound compound)
     {
         NBTTagCompound nbt = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);

         this.countUndeadKill = nbt.getInteger(KEY_CK_U);
         this.countRepairAnvil = nbt.getInteger(KEY_RA);
     }

     /*初期化メソッド。今のところ使う必要はない。*/
     @Override
     public void init(Entity entity, World world) {}

     /*以降、各変数のGetterおよびSetter。
     * 使い方としては、EntityPlayerのインスタンスが取得できるメソッド内で、
     * ExtendedPlayerProperties.get(playerインスタンス).setSampleInt(sample)
     * と呼び出す。*/

     /**
      *
      * @return
      */
     public int getCountKill_Undead() { return this.countUndeadKill; }
     private void setCountKill_Undead(int count) { this.countUndeadKill = count; }
     public void addCountKill_Undead()
     {
         setCountKill_Undead( this.addCount(1, Integer.MAX_VALUE - 1, getCountKill_Undead(), true) );
     }

     /**
      *
      * @return
      */
     public int getCountRepairAnvil() { return countRepairAnvil; }
     private void setCountRepairAnvil(int count) { this.countRepairAnvil = count; }
     public void addCountRepairAnvil()
     {
         setCountRepairAnvil( this.addCount(1, Integer.MAX_VALUE - 1, getCountRepairAnvil(), true) );
     }

     //■桁あふれ防止
     private int addCount(int exp, int limit, int count, boolean isPositive)
     {
         int num = count + (limit - count > exp ? exp : limit - count);
         return isPositive ? MathHelper.clamp_int(num, 0, limit) : num;
     }
}
