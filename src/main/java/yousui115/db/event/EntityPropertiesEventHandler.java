package yousui115.db.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import yousui115.db.Util_DB;
import yousui115.db.network.MessagePlayerJoinInAnnouncement;
import yousui115.db.network.MessagePlayerProperties;
import yousui115.db.network.PacketHandler;

public class EntityPropertiesEventHandler
{

    /*IExtendedEntityPropertiesを登録する処理を呼び出す*/
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event)
    {
        if (event.getEntity() instanceof EntityPlayer)
        {
            //ExtendedPlayerProperties.register((EntityPlayer)event.entity);

        }
    }

    @SubscribeEvent
    /*ワールドに入った時に呼ばれるイベント。ここでIExtendedEntityPropertiesを読み込む処理を呼び出す*/
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getEntity();
            PacketHandler.INSTANCE.sendToServer(new MessagePlayerJoinInAnnouncement(player));
        }
    }

   @SubscribeEvent
    //Dimension移動時や、リスポーン時に呼ばれるイベント。古いインスタンスと新しいインスタンスの両方を参照できる。
    public void onClonePlayer(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
    {
       //TODO いらないかも
        //死亡時に呼ばれてるかどうか
        if (event.isWasDeath())
        {
            //古いカスタムデータ
//            IExtendedEntityProperties oldEntityProperties = event.original.getExtendedProperties(ExtendedPlayerProperties.EXT_PROP_NAME);
            //新しいカスタムデータ
//            IExtendedEntityProperties newEntityProperties = event.entityPlayer.getExtendedProperties(ExtendedPlayerProperties.EXT_PROP_NAME);
            NBTTagCompound playerData = new NBTTagCompound();
            //データの吸い出し
//            oldEntityProperties.saveNBTData(playerData);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger(Util_DB.KEY_CK_U, Util_DB.getCountKill_Undead(event.getOriginal()));
            nbt.setInteger(Util_DB.KEY_RA, Util_DB.getCountRepairAnvil(event.getOriginal()));
            playerData.setTag(Util_DB.EXT_PROP_NAME, nbt);

            //データの書き込み
//            newEntityProperties.loadNBTData(playerData);
//            event.entityPlayer.getEntityData();
//            NBTTagCompound nbt = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);
//            this.countUndeadKill = nbt.getInteger(KEY_CK_U);
//            this.countRepairAnvil = nbt.getInteger(KEY_RA);
        }
    }

    /**
     * ■
     * @param event
     */
    @SubscribeEvent
    /*リスポーン時に呼ばれるイベント。Serverとの同期を取る*/
    public void respawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        if (!event.player.worldObj.isRemote)
        {
            //■Server -> Client
            PacketHandler.INSTANCE.sendTo(new MessagePlayerProperties(event.player), (EntityPlayerMP)event.player);
        }
    }
}
