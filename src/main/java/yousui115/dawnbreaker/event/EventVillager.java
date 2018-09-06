package yousui115.dawnbreaker.event;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotMerchantResult;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.FaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.capability.villager.CapVillagerHandler;
import yousui115.dawnbreaker.capability.villager.IVillagerHandler;
import yousui115.dawnbreaker.capability.villager.VillagerHandler;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.villager.MsgVillagerFaith;
import yousui115.dawnbreaker.network.world.MsgWorldFaith;
import yousui115.dawnbreaker.util.DBItems;
import yousui115.dawnbreaker.util.DBUtils;

public class EventVillager
{
    /**
     * ■キャパビリティの追加
     * @param event
     */
    @SubscribeEvent
    public void attachCapVillager(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityVillager)
        {
            //■きゃぱびりてぃ の追加
            event.addCapability(CapVillagerHandler.KYE, new VillagerHandler());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void pickupSellItemPre(GuiScreenEvent.MouseInputEvent.Pre event)
    {
        //■村人の取引GUIを開いてる時のみ
        if (event.getGui().getClass() != GuiMerchant.class) { return; }
        GuiMerchant merchant = (GuiMerchant)event.getGui();

        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        FaithHandler hdlF = (FaithHandler)player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
        if (hdlF == null) { return; }
        hdlF.sumToolUses = -1;

        //private Slot hoveredSlot;
        Slot hoveredSlot = (Slot)ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, merchant, 6);

        //■「受け取りアイテムスロット上でマウスホバー」 かつ 「左クリック」
        if (hoveredSlot instanceof SlotMerchantResult &&
            hoveredSlot.getStack() != ItemStack.EMPTY &&
            Mouse.getEventButton() == 0)
        {
            int sumToolUses = 0;

            MerchantRecipeList merchantrecipelist = merchant.getMerchant().getRecipes(player);
            for (MerchantRecipe merchantRecipe : merchantrecipelist)
            {
                sumToolUses += merchantRecipe.getToolUses();
            }

            hdlF.sumToolUses = sumToolUses;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void pickupSellItemPost(GuiScreenEvent.MouseInputEvent.Post event)
    {
        //■村人の取引GUIを開いてる時のみ
        if (event.getGui().getClass() != GuiMerchant.class) { return; }
        GuiMerchant merchant = (GuiMerchant)event.getGui();

        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        FaithHandler hdlF = (FaithHandler)player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
        if (hdlF == null) { return; }

        //■Preにて、取引回数総計を記憶している
        if (hdlF.sumToolUses != -1)
        {
            int sumToolUses = 0;

            MerchantRecipeList merchantrecipelist = merchant.getMerchant().getRecipes(player);
            for (MerchantRecipe merchantRecipe : merchantrecipelist)
            {
                sumToolUses += merchantRecipe.getToolUses();
            }

            //■取引回数
            int num = sumToolUses - hdlF.sumToolUses;

            PacketHandler.INSTANCE.sendToServer(new MsgVillagerFaith(num));

            //■リセット
            hdlF.sumToolUses = -1;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void drawStringGUI(GuiContainerEvent.DrawForeground event)
    {
        //■村人の取引GUIを開いてる時のみ
        if (event.getGuiContainer().getClass() != GuiMerchant.class) { return; }

        EntityPlayer player = Dawnbreaker.proxy.getPlayer();
        if (player == null) { return; }
        IFaithHandler hdlF = player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
        if (hdlF == null) { return; }

        //■文字列描画
        Minecraft.getMinecraft().fontRenderer.drawString("Faith : " + hdlF.getFaithV() + "/10", 115, 0, 0x000000);

    }

    /**
     * ■村人に対して右クリックした。
     * @param event
     */
    @SubscribeEvent
    public void interactVillager(PlayerInteractEvent.EntityInteract event)
    {
        //■サーバのみ
        if (event.getEntity().world.isRemote == true) { return; }

        //■村人のみ
        if (event.getTarget() instanceof EntityVillager == false) { return; }
        EntityVillager villager = (EntityVillager)event.getTarget();
        IVillagerHandler hdlV = villager.getCapability(CapVillagerHandler.CAP_VILLAGE, null);
        if (hdlV == null) { return; }

        int def = 0;

        //■信仰値10未満の生きてる緑村人に、メリ玉をメインハンドに持って右クリックした
        if (event.getHand() == EnumHand.MAIN_HAND &&
            villager.isEntityAlive() == true &&
            villager.getProfessionForge() != null &&
            villager.getProfessionForge().getRegistryName().getResourcePath().indexOf("nitwit") != -1 &&
            hdlV.getFaith() != 10 &&
            DBUtils.isEnmptyStack(event.getEntityPlayer().getHeldItemMainhand()) == false &&
            event.getEntityPlayer().getHeldItemMainhand().getItem() == DBItems.MERIDAMA)
        {
            def = hdlV.addFaith(Integer.MAX_VALUE);
            event.getEntityPlayer().getHeldItemMainhand().shrink(1);
            villager.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
        }

        //■世界信仰値の更新
        setWorldFaith(villager, def);

        //■サーバの村人の信仰値を、クライアントのプレイヤーきゃぱに送る
        //  Server -> Client
        PacketHandler.INSTANCE.sendTo(new MsgVillagerFaith(hdlV.getFaith()), (EntityPlayerMP)event.getEntityPlayer());
    }

    /**
     * ■村人にプレイヤーの攻撃がHitした(ダメージ処理前)
     * @param event
     */
    @SubscribeEvent
    public void damagedVillager(LivingAttackEvent event)
    {
        //※ダメージ処理前のターゲットが来る

        //■サーバーのみ
        if (event.getEntityLiving().world.isRemote == true) { return; }

        //■対象：村人
        if (event.getEntityLiving() instanceof EntityVillager == true &&
            event.getEntityLiving().isEntityAlive() == true)
        {
            //■村人きゃぱ
            EntityVillager villager = (EntityVillager)event.getEntityLiving();
            IVillagerHandler hdlV = villager.getCapability(CapVillagerHandler.CAP_VILLAGE, null);
            if (hdlV == null) { return; }

            //■ダメージソースの元凶が、プレイヤーであった
            Entity sourceEntity = event.getSource().getTrueSource();
            if (sourceEntity instanceof EntityPlayer == true)
            {
                //■信仰値の低下
                int def = hdlV.addFaith(-1);

                //■世界信仰値の更新
                setWorldFaith(villager, def);
//                //■
//                World overworld = DimensionManager.getWorld(0);
//                IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
//                if (hdlW != null)
//                {
//                    hdlW.addNumWorldFaith(def);
//                }
//
//                //■更新された世界信仰値を送信
//                //  Server -> Client All
//                PacketHandler.INSTANCE.sendToAll(new MsgWorldFaith(hdlW.getNumWorldFaith()));
//
//                //■トレード中
//                if (villager.isTrading() == true)
//                {
//                    //■サーバのみでの処理なので、MPでオーケーのはず。
//                    EntityPlayerMP player = (EntityPlayerMP) villager.getCustomer();
//                    //■カスタマーに最新の信仰値を送信
//                    //  Server -> Client
//                    PacketHandler.INSTANCE.sendTo(new MsgVillagerFaith(hdlV.getFaith()), player);
//                }
            }
        }
    }

    /**
     * ■村人が死亡した
     * @param event
     */
    @SubscribeEvent
    public void deathVillager(LivingDeathEvent event)
    {
        //■対象：村人
        if (event.getEntityLiving() instanceof EntityVillager == false) { return; }
        EntityVillager villager = (EntityVillager)event.getEntityLiving();

        //■サーバーのみ
        if (villager.world.isRemote == true) { return; }

        //■村人きゃぱ
        IVillagerHandler hdlV = villager.getCapability(CapVillagerHandler.CAP_VILLAGE, null);
        if (hdlV == null) { return; }

        //■培った信仰値がパー
        int def = hdlV.addFaith(Integer.MIN_VALUE);

        setWorldFaith(villager, def);

//        World overworld = DimensionManager.getWorld(0);
//        IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
//        if (hdlW == null) { return; }
//
//        //■更新された世界信仰値を送信
//        //  Server -> Client All
//        PacketHandler.INSTANCE.sendToAll(new MsgWorldFaith(hdlW.getNumWorldFaith()));
//
//        //■トレード中
//        if (villager.isTrading() == true)
//        {
//            //■サーバのみでの処理なので、MPでオーケーのはず。
//            EntityPlayerMP player = (EntityPlayerMP) villager.getCustomer();
//            //■カスタマーに最新の信仰値を送信
//            //  Server -> Client
//            PacketHandler.INSTANCE.sendTo(new MsgVillagerFaith(hdlV.getFaith()), player);
//        }

    }


    /**
     * ■世界信仰値を設定し、各クライアントにデータを送る<br>
     * 　Server -> Client All
     */
    private void setWorldFaith(EntityVillager villagerIn, int defIn)
    {
        //■念のため
        if (villagerIn.world.isRemote == true) { return; }

        //■世界信仰値の増減
        World overworld = DimensionManager.getWorld(0);
        IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
        if (hdlW != null)
        {
            hdlW.addNumWorldFaith(defIn);
        }

        //■更新された世界信仰値を送信
        //  Server -> Client All
        PacketHandler.INSTANCE.sendToAll(new MsgWorldFaith(hdlW.getNumWorldFaith()));

        //■トレード中
        if (villagerIn.isTrading() == true)
        {
            //■サーバのみでの処理なので、MPでオーケーのはず。
            EntityPlayerMP player = (EntityPlayerMP) villagerIn.getCustomer();

            IVillagerHandler hdlV = villagerIn.getCapability(CapVillagerHandler.CAP_VILLAGE, null);
            if (hdlV != null)
            {
                //■カスタマーに最新の信仰値を送信
                //  Server -> Client
                PacketHandler.INSTANCE.sendTo(new MsgVillagerFaith(hdlV.getFaith()), player);
            }
        }
    }
}
