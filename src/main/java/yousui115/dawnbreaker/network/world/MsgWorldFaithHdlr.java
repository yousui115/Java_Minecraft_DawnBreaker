package yousui115.dawnbreaker.network.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;

public class MsgWorldFaithHdlr implements IMessageHandler<MsgWorldFaith, IMessage>
{

    /**
     * ■ Server -> Client
     */
    @Override
    public IMessage onMessage(MsgWorldFaith message, MessageContext ctx)
    {
        //■クライアントワールドの世界信仰値を更新する
        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return null; }

        IWorldHandler hdlW = player.world.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
        if (hdlW != null)
        {
            hdlW.setNumWorldFaith(message.getNumWorldFaith());
        }
        return null;
    }

}
