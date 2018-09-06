package yousui115.dawnbreaker.network.player;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgOpenGUI implements IMessage
{
    private int numWorldFaith;

    public MsgOpenGUI(){}
    public MsgOpenGUI(int numWorldFaithIn)
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

    //==================================================

    public int getNumWorldFaith() { return numWorldFaith; }
}
