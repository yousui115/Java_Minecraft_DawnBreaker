package yousui115.dawnbreaker.network.undead;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.undead.CapabilityExplodeHandler;
import yousui115.dawnbreaker.capability.undead.IExplodeHandler;

public class MessageExplodeHandler implements IMessageHandler<MessageExplode, IMessage>
{

    /**
     * ■Server -> Client
     */
    @Override
    public IMessage onMessage(MessageExplode message, MessageContext ctx)
    {
        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return null; }

        Entity entity = player.world.getEntityByID(message.getEntityID());

        if (entity instanceof EntityCreature &&
            entity.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY,null) == true)
        {
            IExplodeHandler explode = (IExplodeHandler)entity.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);
            explode.setTickAvoid(message.getTickAvoid());
            explode.setTargetPlayer(message.hasTargetPlayer());

//            System.out.println("Client : " + entity.getEntityId() + " : " + message.getTickAvoid());
        }

        return null;
    }

}
