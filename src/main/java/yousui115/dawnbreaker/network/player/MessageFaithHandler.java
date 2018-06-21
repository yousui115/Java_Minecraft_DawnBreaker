package yousui115.dawnbreaker.network.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.FaithHandler;

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
            FaithHandler faith = (FaithHandler)player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
            if (faith == null) { return null; }

            faith.setUndeadKillCount(messageIn.getCountUndeadKill());
            faith.setRepairDBCount(messageIn.getCountRepairDB());
        }

        return null;
    }

}
