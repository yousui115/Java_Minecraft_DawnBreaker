package yousui115.dawnbreaker.event;

import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.FaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.player.MessageFaith;
import yousui115.dawnbreaker.network.player.MessageJoinWorld;
import yousui115.dawnbreaker.network.player.MsgOpenGUI;
import yousui115.dawnbreaker.util.DBEnchs;
import yousui115.dawnbreaker.util.DBUtils;

public class EventPlayer
{
    /**
     * ■キャパビリティの追加
     * @param event
     */
    @SubscribeEvent
    public void attackCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        //■対象：プレイヤー
        if (event.getObject() instanceof EntityPlayer == false) { return; }

        //■きゃぱびりてぃ の追加
        event.addCapability(CapabilityFaithHandler.KYE, new FaithHandler());
    }

    /**
     * ■信仰心の同期 (常時) (Server -> Client)
     * @param event
     */
    @SubscribeEvent
    public void onUpdateTick(TickEvent.PlayerTickEvent event)
    {
        //■サーバーのみ
        if (event.player.world.isRemote) { return; }

        //■
        if (event.phase == Phase.START)
        {
            //■
            if (event.player.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
            {
                IWorldHandler hdlW = event.player.world.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
                if (hdlW == null) { return; }

                FaithHandler hdlF = (FaithHandler)event.player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
                if (hdlF.isDirty() == true)
                {
                    PacketHandler.INSTANCE.sendTo(new MessageFaith(hdlF, hdlW.getNumWorldFaith()), (EntityPlayerMP) event.player);
                    hdlF.resetDirty();
                }
            }
        }
    }

    /**
     * ■信仰心の同期催促 (JoinWorld) (Client -> Server)
     * @param event
     */
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        //メモ：ゲーム開始時、ディメンジョン移動時、死亡時 etc?

        if (event.getWorld().isRemote &&
            event.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getEntity();
            PacketHandler.INSTANCE.sendToServer(new MessageJoinWorld(player));
        }
    }

    /**
     * ■
     * @param event
     */
    @SubscribeEvent
    public void onClonePlayer(PlayerEvent.Clone event)
    {
        //■死亡時の転生
        if (event.isWasDeath() == true)
        {
            EntityPlayer oldPlayer = event.getOriginal();
            EntityPlayer newPlayer = event.getEntityPlayer();

            if (oldPlayer.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true &&
                newPlayer.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
            {
                //■old
                IFaithHandler hdlF_old = (IFaithHandler)oldPlayer.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

                //■new
                IFaithHandler hdlF_new = (IFaithHandler)newPlayer.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

                //■old -> new
                hdlF_new.copy(hdlF_old);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void openGUI(GuiOpenEvent event)
    {
        //■対象：コンテナGUI
        if (event.getGui() instanceof GuiContainer == false) { return; }

        //■ワールドきゃぱ
        EntityPlayerSP player = (EntityPlayerSP)Dawnbreaker.proxy.getPlayer();
        if (player == null) { return; }
        IWorldHandler hdlW =  player.world.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
        if (hdlW == null) { return; }

        //■クライアント側の世界信仰値を送る(サーバと同値だと送り返されない)
        PacketHandler.INSTANCE.sendToServer(new MsgOpenGUI(hdlW.getNumWorldFaith()));
    }


    /**
     * ■ツールチップの表示
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void tooltip(ItemTooltipEvent event)
    {
        //■ドーンブレイカーのツールチップ
        ItemStack stack = event.getItemStack();
        if (DBUtils.isDBwithBoD(stack) == false) { return; }

        //■ツールチップ文字列一覧
        List<String> listTip = event.getToolTip();

        if (event.getEntityPlayer() != null &&
            event.getEntityPlayer().hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
        {
            //■プレイヤーきゃぱ
            IFaithHandler hdlF = event.getEntityPlayer().getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
            if (hdlF == null) { return; }

            //■ワールドきゃぱ
            IWorldHandler hdlW = event.getEntityPlayer().world.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
            if (hdlW == null) { return; }

            //■ツールチップ内から特定の文字列を探し、そこから追加情報を挿入する
            for (int idx = 0; idx < listTip.size(); idx++)
            {
                String str = listTip.get(idx);
                if (str.indexOf(DBEnchs.ENCH_BOD.getTransName()) != -1)
                {
                    //■灰色の「Break of dawn」の行を削除
                    listTip.remove(idx);
                    //■黄色の「Break of dawn」を追加
                    listTip.add(idx, TextFormatting.YELLOW + DBEnchs.ENCH_BOD.getTransName());
                    listTip.add(idx + 1, TextFormatting.DARK_AQUA + "Undead Kill : " + hdlF.getUndeadKillCount());
                    listTip.add(idx + 2, TextFormatting.GRAY + " next : " + hdlF.getCountNext());
                    listTip.add(idx + 3, TextFormatting.DARK_AQUA + "Repair Count : " + hdlF.getRepairDBCount());
                    listTip.add(idx + 4, TextFormatting.DARK_AQUA + "Faith : " + hdlW.getNumWorldFaithDisp() + " / " + hdlW.getNumWorldFaithDispMax());
                    break;
                }
            }
        }
    }
}
