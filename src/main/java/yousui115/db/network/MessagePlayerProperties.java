package yousui115.db.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import yousui115.db.common.ExtendedPlayerProperties;

public class MessagePlayerProperties implements IMessage
{
    public NBTTagCompound data;

    public MessagePlayerProperties(){}

    public MessagePlayerProperties(EntityPlayer entityPlayer) {
        this.data = new NBTTagCompound();
        //EntityPlayerからIExtendedEntityPropertiesを取得。
        ExtendedPlayerProperties.get(entityPlayer).saveNBTData(data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }
}