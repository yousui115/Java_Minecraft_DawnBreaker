package yousui115.dawnbreaker.network.player;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yousui115.dawnbreaker.capability.player.IFaithHandler;

public class MessageFaith implements IMessage
{
    private int countUndeadKill;
    private int countUndeadKill_hide;
    private int countRepairDB;


    public MessageFaith(){}
    public MessageFaith(IFaithHandler hdlFaithIn)
    {
        countUndeadKill = hdlFaithIn.getUndeadKillCount();
        countUndeadKill_hide = hdlFaithIn.getUndeadKillCount_hide();
        countRepairDB = hdlFaithIn.getRepairDBCount();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        countUndeadKill = buf.readInt();
        countUndeadKill_hide = buf.readInt();
        countRepairDB = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(countUndeadKill);
        buf.writeInt(countUndeadKill_hide);
        buf.writeInt(countRepairDB);
    }


    public int getCountUndeadKill() { return countUndeadKill; }
    public int getCountUndeadKill_hide() { return countUndeadKill_hide; }
    public int getCountRepairDB() { return countRepairDB; }
}
