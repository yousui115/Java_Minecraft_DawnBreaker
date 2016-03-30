package yousui115.db.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.db.DB;
import yousui115.db.entity.EntityDBExplode;

public class MessageMagicHandler implements IMessageHandler<MessageMagic, IMessage>
{
    /**
     * ■PacketHandler.INSTANCE.sendToAll()等で送り出したMessageが辿り着く。
     *   NetHandlerPlayerClient.handleSpawnGlobalEntity()をパｋ真似て作成。
     *   Server -> Client
     */
    @Override
    public IMessage onMessage(MessageMagic message, MessageContext ctx)
    {
        //クライアントへ送った際に、EntityPlayerインスタンスはこのように取れる。
        //EntityPlayer player = SamplePacketMod.proxy.getEntityPlayerInstance();
        //サーバーへ送った際に、EntityPlayerインスタンス（EntityPlayerMPインスタンス）はこのように取れる。
        //EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        //Do something.

        //■クライアントサイドにEntityを登録する。
        EntityPlayer player = DB.proxy.getPlayer();
        if (player == null) { return null; }

        Entity trigger = player.worldObj.getEntityByID(message.getTriggerID());
        EntityDBExplode magic = null;
        if (trigger != null)
        {
            magic = new EntityDBExplode(player.worldObj, trigger);

            magic.setEntityId(message.getEntityID());
            magic.serverPosX = message.getPosX();
            magic.serverPosY = message.getPosY();
            magic.serverPosZ = message.getPosZ();

            player.worldObj.addWeatherEffect(magic);
        }


        return null;//本来は返答用IMessageインスタンスを返すのだが、旧来のパケットの使い方をするなら必要ない。
    }

}
