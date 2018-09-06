package yousui115.dawnbreaker.network.world;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgWorldFaith  implements IMessage
{
    private int numWorldFaith;

    public MsgWorldFaith(){}
    public MsgWorldFaith(int numWorldFaithIn)
    {
        numWorldFaith = numWorldFaithIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        numWorldFaith = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(numWorldFaith);
    }

    //=====================================================

    public int getNumWorldFaith() { return numWorldFaith; }

}
