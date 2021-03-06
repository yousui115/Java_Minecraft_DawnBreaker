package yousui115.dawnbreaker.network.undead;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.undead.CapabilityUndeadHandler;
import yousui115.dawnbreaker.capability.undead.IUndeadHandler;

public class MessageExplodeHandler implements IMessageHandler<MessageExplode, IMessage>
{

    /**
     * ■Server -> Client
     */
    @Override
    public IMessage onMessage(MessageExplode message, MessageContext ctx)
    {
        //■爆心地であるEntityを掴む
        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return null; }
        Entity entity = player.world.getEntityByID(message.getEntityID());

        //■アンデッドきゃぱを持っている。
        if (entity instanceof EntityCreature &&
            entity.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY,null) == true)
        {
            IUndeadHandler hdlU = (IUndeadHandler)entity.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);
            hdlU.setTickAvoid(message.getTickAvoid());
            hdlU.setHasTargetPlayer(message.hasTargetPlayer());

//            System.out.println("Client : " + entity.getEntityId() + " : " + message.getTickAvoid());
        }

        return null;
    }

}
