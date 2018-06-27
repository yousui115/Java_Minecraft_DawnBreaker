package yousui115.dawnbreaker.network.undead;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yousui115.dawnbreaker.capability.undead.IUndeadHandler;

public class MessageExplode implements IMessage
{
    private int entityID;
    private int tickAvoid;
    private boolean hasTargetPlayer;

    public MessageExplode() {}
    public MessageExplode(Entity entityIn, IUndeadHandler hdlUndeadIn)
    {
        entityID = entityIn.getEntityId();
        tickAvoid = hdlUndeadIn.getTickAvoid();
        hasTargetPlayer = hdlUndeadIn.hasTargetPlayer();
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
