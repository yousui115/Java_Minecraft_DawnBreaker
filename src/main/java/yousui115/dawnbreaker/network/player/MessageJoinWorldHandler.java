package yousui115.dawnbreaker.network.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;

public class MessageJoinWorldHandler implements IMessageHandler<MessageJoinWorld, MessageFaith>
{

    @Override
    public MessageFaith onMessage(MessageJoinWorld message, MessageContext ctx)
    {
        //■UUIDを取得
        String uuidString = message.getUUID();

        //■
        EntityPlayer player = ctx.getServerHandler().player;

        //■取得したPlayerが同一UUIDを持つか判定
        if (player.getGameProfile().getId().toString().equals(uuidString))
        {
            //■Server -> Client
            if (player.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
            {
                IFaithHandler faith = player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

                return new MessageFaith(faith);
            }
        }

        //■UUIDが違っていた場合、同期処理を呼ばない
        return null;
    }

}
