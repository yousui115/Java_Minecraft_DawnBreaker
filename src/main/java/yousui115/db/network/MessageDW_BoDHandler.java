package yousui115.db.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.db.DB;
import yousui115.db.Util_DB;

public class MessageDW_BoDHandler implements IMessageHandler<MessageDW_BoD, IMessage>
{
    /**
     * ■PacketHandler.INSTANCE.sendToAll()等で送り出したMessageが辿り着く。
     *   NetHandlerPlayerClient.handleSpawnGlobalEntity()をパｋ真似て作成。
     *   Server -> Client
     */
    @Override
    public IMessage onMessage(MessageDW_BoD message, MessageContext ctx)
    {
        //クライアントへ送った際に、EntityPlayerインスタンスはこのように取れる。
        //EntityPlayer player = SamplePacketMod.proxy.getEntityPlayerInstance();
        //サーバーへ送った際に、EntityPlayerインスタンス（EntityPlayerMPインスタンス）はこのように取れる。
        //EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
        //Do something.

        //■クライアントサイドにEntityを登録する。
        EntityPlayer player = DB.proxy.getEntityPlayerInstance();
        if (player == null) { return null; }

        Entity trigger = player.worldObj.getEntityByID(message.getEntityID());
        if (trigger != null)
        {
            Util_DB.setDW_DB_Flag(trigger, message.getValue());
        }


        return null;//本来は返答用IMessageインスタンスを返すのだが、旧来のパケットの使い方をするなら必要ない。
    }
}