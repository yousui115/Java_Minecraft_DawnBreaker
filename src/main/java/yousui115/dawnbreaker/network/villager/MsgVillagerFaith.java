package yousui115.dawnbreaker.network.villager;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgVillagerFaith implements IMessage
{
    private int faith;

    public MsgVillagerFaith(){}
    public MsgVillagerFaith(int faithIn)
    {
        faith = faithIn;
    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        faith = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(faith);
    }

    //=====================================================

    public int getFaith() { return faith; }
}
