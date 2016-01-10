package yousui115.db.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yousui115.db.DB;

public class PacketHandler
{
    /*MOD固有のSimpleNetworkWrapperを取得。
    * 文字列は他のMODと被らないようにMOD_IDを指定しておくと良い*/
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(DB.MOD_ID);

    public static void init() {
        /*Messageクラスの登録。
        * 第一引数：IMessageHandlerクラス
        * 第二引数：送るMessageクラス
        * 第三引数：登録番号。255個まで
        * 第四引数：ClientとServerのどちらに送るか。送り先*/
        INSTANCE.registerMessage(MessagePlayerPropertiesHandler.class, MessagePlayerProperties.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MessagePlayerJoinInAnoucementHandler.class, MessagePlayerJoinInAnnouncement.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessageMagicHandler.class, MessageMagic.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(MessageDW_BoDHandler.class, MessageDW_BoD.class, 3, Side.CLIENT);
    }
}