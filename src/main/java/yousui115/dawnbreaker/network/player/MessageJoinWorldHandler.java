package yousui115.dawnbreaker.network.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;

public class MessageJoinWorldHandler implements IMessageHandler<MessageJoinWorld, MessageFaith>
{

    /**
     * ■Client -> Server
     *   同期催促メッセージの受信
     */
    @Override
    public MessageFaith onMessage(MessageJoinWorld message, MessageContext ctx)
    {
        //■UUIDを取得
        String uuidString = message.getUUID();

        //■サーバ側の自キャラ
        EntityPlayer player = ctx.getServerHandler().player;

        //■取得したPlayerが同一UUIDを持つか判定
        if (player.getGameProfile().getId().toString().equals(uuidString))
        {
            //■Server -> Client
            //■プレイヤーきゃぱ
            IFaithHandler hdlFaith = player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
            if (hdlFaith == null) { return null; }

            //■オーバーワールドきゃぱ
            World overworld = DimensionManager.getWorld(0);
            IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
            if (hdlW == null) { return null;}

            //■同期情報の送り返し
            return new MessageFaith(hdlFaith, hdlW.getNumWorldFaith());
        }

        //■UUIDが違っていた場合、同期処理を呼ばない
        return null;
    }

}
