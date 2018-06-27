package yousui115.dawnbreaker.network.undead;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.capability.undead.CapabilityUndeadHandler;
import yousui115.dawnbreaker.capability.undead.IUndeadHandler;

public class MessageJoinUndeadHandler implements IMessageHandler<MessageJoinUndead, MessageExplode>
{

    /**
     * ■Client -> Server
     */
    @Override
    public MessageExplode onMessage(MessageJoinUndead message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().player;
        if (player == null) { return null; }

        Entity entity = player.world.getEntityByID(message.getEntityID());

        if (entity instanceof EntityCreature &&
            entity.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null) == true)
        {
            IUndeadHandler hdlUndead = entity.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);

            return new MessageExplode(entity, hdlUndead);
        }

        return null;
    }

}
