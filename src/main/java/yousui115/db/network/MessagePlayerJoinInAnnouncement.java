package yousui115.db.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessagePlayerJoinInAnnouncement implements IMessage
{

    private String uuid;

    public MessagePlayerJoinInAnnouncement(){}

    public MessagePlayerJoinInAnnouncement(EntityPlayer player) {
        //PlayerのUUIDを文字列で取得
        this.uuid = player.getGameProfile().getId().toString();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.uuid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.uuid);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}