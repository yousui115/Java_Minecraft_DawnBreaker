package yousui115.dawnbreaker.network.undead;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yousui115.dawnbreaker.capability.undead.IExplodeHandler;

public class MessageExplode implements IMessage
{
    private int entityID;
    private int tickAvoid;
    private boolean hasTargetPlayer;

    public MessageExplode() {}
    public MessageExplode(Entity entityIn, IExplodeHandler explode)
    {
        entityID = entityIn.getEntityId();
        tickAvoid = explode.getTickAvoid();
        hasTargetPlayer = explode.hasTargetPlayer();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityID = buf.readInt();
        tickAvoid = buf.readInt();
        hasTargetPlayer = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        buf.writeInt(tickAvoid);
        buf.writeBoolean(hasTargetPlayer);
    }

    public int getEntityID() { return entityID; }
    public int getTickAvoid() { return tickAvoid; }
    public boolean hasTargetPlayer() { return hasTargetPlayer; }
}
