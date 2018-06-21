package yousui115.dawnbreaker.network.undead;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageJoinUndead implements IMessage
{
    private int entityID;

    public MessageJoinUndead(){}
    public MessageJoinUndead(EntityCreature creatureIn)
    {
        entityID = creatureIn.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
    }

    public int getEntityID() { return entityID; }
}
