package yousui115.db.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayerProperties implements IExtendedEntityProperties
{
    /* MOD固有の文字列。EntityPlayerに登録時に使用。
    MOD内で複数のIExtendedEntityPropertiesを使う場合は、別の文字列をそれぞれ割り当てること。*/
     public final static String EXT_PROP_NAME = "UndeadKillData";

     private final static String KEY_CK_U = "countKill_Undead";

     private int countUndeadKill = 0;

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
         nbt.setInteger(KEY_CK_U, this.countUndeadKill);

         compound.setTag(EXT_PROP_NAME, nbt);
     }

     @Override
     public void loadNBTData(NBTTagCompound compound)
     {
         NBTTagCompound nbt = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);
         this.countUndeadKill = nbt.getInteger(KEY_CK_U);

     }

     /*初期化メソッド。今のところ使う必要はない。*/
     @Override
     public void init(Entity entity, World world) {}

     /*以降、各変数のGetterおよびSetter。
     * 使い方としては、EntityPlayerのインスタンスが取得できるメソッド内で、
     * ExtendedPlayerProperties.get(playerインスタンス).setSampleInt(sample)
     * と呼び出す。*/

     public int getCountKill_Undead()
     {
         return countUndeadKill;
     }

     private void setCountKill_Undead(int count)
     {
         if (count < 0) { count = 0; }
         this.countUndeadKill = count;
     }

     public void addCountKill_Undead()
     {
         int exp = 1;
         int limit = (Integer.MAX_VALUE - 1) - getCountKill_Undead();
         int count = getCountKill_Undead() + (limit > exp ? exp : limit);   //上限あふれ防止
         setCountKill_Undead(count);
     }


}
