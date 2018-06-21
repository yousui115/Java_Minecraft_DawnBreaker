package yousui115.dawnbreaker.network.player;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yousui115.dawnbreaker.capability.player.IFaithHandler;

public class MessageFaith implements IMessage
{
    private int countUndeadKill;
    private int countRepairDB;


    public MessageFaith(){}
    public MessageFaith(IFaithHandler faith)
    {
        countUndeadKill = faith.getUndeadKillCount();
        countRepairDB = faith.getRepairDBCount();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        countUndeadKill = buf.readInt();
        countRepairDB = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(countUndeadKill);
        buf.writeInt(countRepairDB);
    }


    public int getCountUndeadKill() { return countUndeadKill; }
    public int getCountRepairDB() { return countRepairDB; }
}
