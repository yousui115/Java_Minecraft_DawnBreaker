package yousui115.dawnbreaker.network.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.FaithHandler;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;

public class MessageFaithHandler implements IMessageHandler<MessageFaith, IMessage>
{
    /**
     * ■ Server -> Client
     */
    @Override
    public IMessage onMessage(MessageFaith messageIn, MessageContext ctxIn)
    {
        //■クライアントサイドにEntityを登録する。
        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return null; }

        //■
        if (player.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
        {
            FaithHandler hdlFaith = (FaithHandler)player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
            if (hdlFaith == null) { return null; }

            hdlFaith.setUndeadKillCount(messageIn.getCountUndeadKill());
            hdlFaith.setUndeadKillCount_hide(messageIn.getCountUndeadKill_hide());
            hdlFaith.setRepairDBCount(messageIn.getCountRepairDB());

            IWorldHandler hdlW = player.world.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
            if (hdlW == null) { return null; }

            hdlW.setNumWorldFaith(messageIn.getNumWorldFaith());
        }

        return null;
    }

}
