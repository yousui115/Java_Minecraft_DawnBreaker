package yousui115.dawnbreaker.network.undead;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.entity.EntityMagicExplode;

public class MessageMagicExplodeHandler implements IMessageHandler<MessageMagicExplode, IMessage>
{
    /**
     * ■ Server -> Client
     */
    @Override
    public IMessage onMessage(MessageMagicExplode message, MessageContext ctx)
    {
        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return null; }

        Entity trigger = player.world.getEntityByID(message.getTriggerID());
        EntityMagicExplode magic = null;
        if (trigger != null)
        {
            magic = new EntityMagicExplode(player.world, trigger, false, 0f);
            magic.setEntityId(message.getEntityID());
//            magic.serverPosX = message.getPosX();
//            magic.serverPosY = message.getPosY();
//            magic.serverPosZ = message.getPosZ();

            player.world.addWeatherEffect(magic);
        }


        return null;
    }

}
