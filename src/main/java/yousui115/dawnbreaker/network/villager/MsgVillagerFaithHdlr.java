package yousui115.dawnbreaker.network.villager;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yousui115.dawnbreaker.capability.villager.CapVillagerHandler;
import yousui115.dawnbreaker.capability.villager.IVillagerHandler;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.world.MsgWorldFaith;

public class MsgVillagerFaithHdlr implements IMessageHandler<MsgVillagerFaith, IMessage>
{

    /**
     * ■Client -> Server
     */
    @Override
    public IMessage onMessage(MsgVillagerFaith message, MessageContext ctx)
    {
        //■メッセージを飛ばしてきたプレイヤー（のサーバー実体）
        EntityPlayer player = ctx.getServerHandler().player;
        if (player == null) { return null; }

        //■村人交渉GUIを開いているはず。
        Container gui = player.openContainer;
        if (gui instanceof ContainerMerchant)
        {
            //■開いてた
            ContainerMerchant container = (ContainerMerchant)gui;

            //■村人を取得
            IMerchant merchant = (IMerchant)ObfuscationReflectionHelper.getPrivateValue(ContainerMerchant.class, container, 0);
            if (merchant instanceof EntityVillager == false || ((EntityVillager)merchant).isEntityAlive() == false) { return null; }

            //■村人の信仰度を変更。
            EntityVillager villager = (EntityVillager)merchant;
            IVillagerHandler hdlV = (IVillagerHandler)villager.getCapability(CapVillagerHandler.CAP_VILLAGE, null);
            if (hdlV == null) { return null; }

            //■信仰値増減
            int def = hdlV.addFaith(message.getFaith());

            World overworld = DimensionManager.getWorld(0);
            IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
            if (hdlW != null)
            {
                hdlW.addNumWorldFaith(def);
            }

            //■更新された世界信仰値を送信
            //  Server -> Client All
            PacketHandler.INSTANCE.sendToAll(new MsgWorldFaith(hdlW.getNumWorldFaith()));

            //■カスタマーに最新の信仰値を送信
            //  Server -> Client
            return new MsgVillagerFaith(hdlV.getFaith());
        }
        return null;
    }

}
