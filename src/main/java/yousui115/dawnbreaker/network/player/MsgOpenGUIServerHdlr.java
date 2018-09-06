package yousui115.dawnbreaker.network.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;
import yousui115.dawnbreaker.network.world.MsgWorldFaith;

public class MsgOpenGUIServerHdlr  implements IMessageHandler<MsgOpenGUI, MsgWorldFaith>
{

    /**
     * ■Client -> Server
     */
    @Override
    public MsgWorldFaith onMessage(MsgOpenGUI message, MessageContext ctx)
    {
        //■サーバ側の自キャラ
        EntityPlayerMP player = ctx.getServerHandler().player;

        //■クライアントワールドの世界信仰値
        int worldFaithC = message.getNumWorldFaith();

        //■サーバワールドの世界信仰値
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null) { return null; }
        IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
        if (hdlW == null) { return null; }
        int worldFaithS = hdlW.getNumWorldFaith();

        if (worldFaithC != worldFaithS)
        {
            return new MsgWorldFaith(worldFaithS);
        }
        return null;
    }

}
