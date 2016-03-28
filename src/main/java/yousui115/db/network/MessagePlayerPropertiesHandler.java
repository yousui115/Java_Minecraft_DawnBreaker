package yousui115.db.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.db.DB;
import yousui115.db.Util_DB;

public class MessagePlayerPropertiesHandler implements IMessageHandler<MessagePlayerProperties, IMessage>
{
    @Override
    public IMessage onMessage(MessagePlayerProperties message, MessageContext ctx)
    {
        //Client側にIExtendedEntityPropertiesを渡す。
//        ExtendedPlayerProperties.get(DB.proxy.getEntityPlayerInstance()).loadNBTData(message.data);
        Util_DB.setNBTData(DB.proxy.getEntityPlayerInstance(), message.data);
        //REPLYは送らないので、nullを返す。
        return null;
    }
}