package yousui115.dawnbreaker.event;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.FaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.player.MessageFaith;
import yousui115.dawnbreaker.network.player.MessageJoinWorld;
import yousui115.dawnbreaker.util.DBUtils;

public class EventEntityPlayer
{
//    public static final UUID UUID_ATTACK_SPEED_FROM_FAITH_POWER = UUID.nameUUIDFromBytes("UUID_ATTACK_SPEED_FROM_FAITH_POWER".getBytes());
//    public static final AttributeModifier MODIFIER_ATTACK_SPEED_FROM_FAITH_POWER = (new AttributeModifier(UUID_ATTACK_SPEED_FROM_FAITH_POWER, "Attack speed from faith power", 10d, 0)).setSaved(false);

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
                FaithHandler hdlFaith = (FaithHandler)event.player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
                if (hdlFaith.isDirty() == true)
                {
                    PacketHandler.INSTANCE.sendTo(new MessageFaith(hdlFaith), (EntityPlayerMP) event.player);
                    hdlFaith.resetDirty();
                }
            }
        }
    }

//    @SubscribeEvent
//    public void onAttackSpeed(TickEvent.PlayerTickEvent event)
//    {
//        if (event.phase == Phase.START)
//        {
//            IAttributeInstance attri = event.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
//            if (attri.hasModifier(MODIFIER_ATTACK_SPEED_FROM_FAITH_POWER) == true)
//            {
//                attri.removeModifier(MODIFIER_ATTACK_SPEED_FROM_FAITH_POWER);
//            }
//
//            FaithHandler faith = null;
//            if (event.player.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
//            {
//                faith = (FaithHandler)event.player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
//            }
//
//            if (event.player.getHeldItemMainhand().getItem() instanceof ItemDawnbreaker &&
//                faith.getRepairDBCount() >= 0)
//            {
//                attri.applyModifier(MODIFIER_ATTACK_SPEED_FROM_FAITH_POWER);
//            }
//        }
//    }

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
                IFaithHandler hdlFaith_old = (IFaithHandler)oldPlayer.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

                //■new
                IFaithHandler hdlFaith_new = (IFaithHandler)newPlayer.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

                //■old -> new
                hdlFaith_new.copy(hdlFaith_old);
            }
        }
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
        if (DBUtils.isEnmptyStack(stack) == true || stack.getItem() instanceof ItemDawnbreaker == false) { return; }

        //■ツールチップ文字列一覧
        List<String> list = event.getToolTip();

        if (event.getEntityPlayer() != null &&
            event.getEntityPlayer().hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
        {
            IFaithHandler hdlFaith = event.getEntityPlayer().getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

            for (int idx = 0; idx < list.size(); idx++)
            {
                String str = list.get(idx);
                if (str.indexOf("Break") != -1)
                {
                    list.add(idx + 1, TextFormatting.DARK_AQUA + "Undead Kill : " + hdlFaith.getUndeadKillCount());
                    list.add(idx + 2, TextFormatting.GRAY + " next : " + hdlFaith.getCountNext());
                    list.add(idx + 3, TextFormatting.DARK_AQUA + "Repair Count : " + hdlFaith.getRepairDBCount());
                }
            }
        }
    }
}
