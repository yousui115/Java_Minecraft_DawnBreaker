package yousui115.dawnbreaker.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.network.particle.MessageFlame;
import yousui115.dawnbreaker.network.particle.MessageFlameHandler;
import yousui115.dawnbreaker.network.player.MessageFaith;
import yousui115.dawnbreaker.network.player.MessageFaithHandler;
import yousui115.dawnbreaker.network.player.MessageJoinWorld;
import yousui115.dawnbreaker.network.player.MessageJoinWorldHandler;
import yousui115.dawnbreaker.network.player.MsgOpenGUI;
import yousui115.dawnbreaker.network.player.MsgOpenGUIServerHdlr;
import yousui115.dawnbreaker.network.undead.MessageExplode;
import yousui115.dawnbreaker.network.undead.MessageExplodeHandler;
import yousui115.dawnbreaker.network.undead.MessageJoinUndead;
import yousui115.dawnbreaker.network.undead.MessageJoinUndeadHandler;
import yousui115.dawnbreaker.network.undead.MessageMagicExplode;
import yousui115.dawnbreaker.network.undead.MessageMagicExplodeHandler;
import yousui115.dawnbreaker.network.villager.MsgClientFaithHdlr;
import yousui115.dawnbreaker.network.villager.MsgVillagerFaith;
import yousui115.dawnbreaker.network.villager.MsgVillagerFaithHdlr;
import yousui115.dawnbreaker.network.world.MsgWorldFaith;
import yousui115.dawnbreaker.network.world.MsgWorldFaithHdlr;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Dawnbreaker.MOD_ID);

    public static void register()
    {
        //■Server -> Client
        INSTANCE.registerMessage(MessageFaithHandler.class, MessageFaith.class, 0, Side.CLIENT);
        //■Server -> Client
        INSTANCE.registerMessage(MessageMagicExplodeHandler.class, MessageMagicExplode.class, 1, Side.CLIENT);
        //■Client -> Server
        INSTANCE.registerMessage(MessageJoinWorldHandler.class, MessageJoinWorld.class, 2, Side.SERVER);
        //■Server -> Client
        INSTANCE.registerMessage(MessageExplodeHandler.class, MessageExplode.class, 3, Side.CLIENT);
        //■Client -> Server
        INSTANCE.registerMessage(MessageJoinUndeadHandler.class, MessageJoinUndead.class, 4, Side.SERVER);
        //■Server -> Client
        INSTANCE.registerMessage(MessageFlameHandler.class, MessageFlame.class, 5, Side.CLIENT);
        //■Client -> Server
        INSTANCE.registerMessage(MsgVillagerFaithHdlr.class, MsgVillagerFaith.class, 6, Side.SERVER);
        //■Server -> Client
        INSTANCE.registerMessage(MsgWorldFaithHdlr.class, MsgWorldFaith.class, 7, Side.CLIENT);
        //■Server -> Client
        INSTANCE.registerMessage(MsgClientFaithHdlr.class, MsgVillagerFaith.class, 8, Side.CLIENT);
        //■Client -> Server
        INSTANCE.registerMessage(MsgOpenGUIServerHdlr.class, MsgOpenGUI.class, 9, Side.SERVER);
    }
}
