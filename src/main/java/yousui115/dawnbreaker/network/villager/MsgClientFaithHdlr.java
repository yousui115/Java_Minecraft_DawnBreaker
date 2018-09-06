package yousui115.dawnbreaker.network.villager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.FaithHandler;

public class MsgClientFaithHdlr implements IMessageHandler<MsgVillagerFaith, IMessage>
{
    /**
     * ■Server -> Client
     */
    @Override
    public IMessage onMessage(MsgVillagerFaith message, MessageContext ctx)
    {
        //■プレイヤーきゃぱ
        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return null; }
        FaithHandler hdlF = (FaithHandler)player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
        if (hdlF == null) { return null; }

        //■取引中のVillagerの信仰値を一時的に保存する
        //  (GuiMerchantに保持されるIMerchantはNpcMerchantなので、きゃぱ☆を保持出来ない)
        hdlF.setFaithV(message.getFaith());

        return null;
    }

}
