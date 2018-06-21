package yousui115.dawnbreaker.network.player;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageJoinWorld implements IMessage
{
    private String uuid;

    public MessageJoinWorld() {}
    public MessageJoinWorld(EntityPlayer player)
    {
        this.uuid = player.getGameProfile().getId().toString();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.uuid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.uuid);
    }

    public String getUUID() { return uuid; }
}
