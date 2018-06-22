package yousui115.dawnbreaker.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.capability.undead.CapabilityExplodeHandler;
import yousui115.dawnbreaker.capability.undead.ExplodeHandler;
import yousui115.dawnbreaker.capability.undead.IExplodeHandler;
import yousui115.dawnbreaker.entity.EntityMagicExplode;
import yousui115.dawnbreaker.entity.ai.EntityAIAvoidPlayer;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.undead.MessageExplode;
import yousui115.dawnbreaker.network.undead.MessageJoinUndead;
import yousui115.dawnbreaker.network.undead.MessageMagicExplode;
import yousui115.dawnbreaker.util.DBItems;
import yousui115.dawnbreaker.util.DBUtils;

public class EventUndead
{
    /**
     * ■キャパビリティの追加
     * @param event
     */
    @SubscribeEvent
    public void attackCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        //■対象：アンデッド
        if (event.getObject() instanceof EntityCreature == false) { return; }
        if (DBUtils.isUndead((EntityCreature)event.getObject()) == false) { return; }

        //■きゃぱびりてぃ の追加
        event.addCapability(CapabilityExplodeHandler.KYE, new ExplodeHandler());
    }

    /**
     * ■AIの植え付け と きゃぱびりてぃの同期
     * @param event
     */
    @SubscribeEvent
    public void joinWorldUndead(EntityJoinWorldEvent event)
    {
        //■対象：あんでっど
        if (event.getEntity() instanceof EntityCreature == false) { return; }
        EntityCreature creature = (EntityCreature)event.getEntity();

        //■アンデッドのみ
//      if (DBUtils.isUndead(creature) == false) { return; }
        if (creature.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == false) { return; }
        IExplodeHandler explode = creature.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);


        //■くらいあんと
        if (event.getWorld().isRemote)
        {
            // ▼きゃぱびりてぃの同期催促(JoinWorld時は強制同期)
            PacketHandler.INSTANCE.sendToServer(new MessageJoinUndead(creature));
        }
        //■さーばー
        else
        {
            // ▼AIの植え付け
            boolean hasAI = false;
            for (EntityAITasks.EntityAITaskEntry entry : creature.tasks.taskEntries)
            {
                if (entry.action instanceof EntityAIAvoidPlayer)
                {
                    hasAI = true;
                    break;
                }
            }
            if (hasAI == false)
            {
                creature.tasks.addTask(0, new EntityAIAvoidPlayer(creature, EntityPlayer.class, 6.0F, 1.0D, 1.2D));
            }
        }
    }


    /**
     * ■
     * @param event
     */
    @SubscribeEvent
    public void on(LivingSetAttackTargetEvent event)
    {
        if (event.getEntityLiving() instanceof EntityCreature == false) { return; }
        EntityCreature creature = (EntityCreature)event.getEntityLiving();
        //■サーバーのみ
        if (creature.world.isRemote) { return; }
        if (creature.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == false) { return; }
        //■
        IExplodeHandler explode = (IExplodeHandler)creature.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);

        explode.setTargetPlayer(creature.getAttackTarget());
    }

    /**
     * ■
     * @param event
     */
    @SubscribeEvent
    public void onUpdateTick(LivingUpdateEvent event)
    {
        if (event.getEntityLiving() instanceof EntityCreature == false) { return; }
        EntityCreature creature = (EntityCreature)event.getEntityLiving();

        //■サーバーのみ
        if (creature.world.isRemote) { return; }

        //■
        if (creature.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == true)
        {
            IExplodeHandler explode = (IExplodeHandler)creature.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);
            if (explode.isDirty() == true)
            {
//                System.out.println("Server(Tick) : " + creature.getEntityId() + " : " + explode.getTickAvoid());
                PacketHandler.INSTANCE.sendToAll(new MessageExplode(creature, explode));
                explode.resetDirty();
            }
        }
    }

    /**
     * ■Undeadへドーンブレイカーの攻撃がHitした(ダメージ処理前)
     * @param event
     */
    @SubscribeEvent
    public void damagedUndead(AttackEntityEvent event)
    {
        //※ダメージ処理前のターゲットが来る

        //メモ:メインにブレイカーを持って、オフハンドの弓を使っても、このメソッドは呼ばれない

        //■サーバーのみ
        if (event.getEntityPlayer().world.isRemote == true) { return; }

        //■対象：アンデッド
        if (event.getTarget() != null && event.getTarget() instanceof EntityCreature)
        {
            EntityCreature undead = (EntityCreature)event.getTarget();
            if (DBUtils.isUndead(undead) == false) { return; }

            //■プレイヤー
            EntityPlayer player = event.getEntityPlayer();
            if (player == null) { return; }

            //■メインハンドにドーンブレイカーを持ってる。
            ItemStack mainStack = player.getHeldItemMainhand();
            if (DBUtils.isEnmptyStack(mainStack) == false && mainStack.getItem() instanceof ItemDawnbreaker)
            {
                //■爆発するチャンスを与えよう。
                if (undead.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == true)
                {
                    IExplodeHandler exp = (IExplodeHandler)undead.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);

                    exp.setChanceExplode(true);
                }
            }
        }
    }

    /**
     * ■Undeadが死亡した
     * @param event
     */
    @SubscribeEvent
    public void deathUndead(LivingDeathEvent event)
    {
        //■対象：アンデッド
        if (event.getEntityLiving() instanceof EntityCreature == false) { return; }
        EntityCreature undead = (EntityCreature)event.getEntityLiving();
        if (DBUtils.isUndead(undead) == false) { return; }

        //■サーバーのみ
        if (undead.world.isRemote == true) { return; }

        //■アンデッド討伐数のカウントアップ
        if (event.getSource().getTrueSource() instanceof EntityPlayer &&
            event.getSource().getTrueSource().hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
        {
            IFaithHandler faith = event.getSource().getTrueSource().getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
            faith.addUndeadKillCount();

            //■討伐数が一定値に至ったので、メリ玉進呈
            if (faith.getUndeadKillCount() % 100 == 10)
            {
                World world = undead.world;
                double posY = MathHelper.clamp(event.getEntityLiving().posY, 0d, 255d);   //奈落・天上対策
                BlockPos bp = new BlockPos(event.getEntityLiving().posX, posY, event.getEntityLiving().posZ);

                //■謙虚なチェスト生成(容赦ない設置)
                world.setBlockState(bp, Blocks.CHEST.getDefaultState());

                //■チェストインベントリ へアクセス
                TileEntity inv = world.getTileEntity(bp);

                //■メリ玉をそっと置いていく
                if (inv != null && inv instanceof TileEntityChest)
                {
                    ((TileEntityChest)inv).setInventorySlotContents(13, new ItemStack(DBItems.MERIDAMA, 1));
                }
            }
        }

        //■爆発チャンス！
        if (undead.hasCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null) == true)
        {
            IExplodeHandler exp = (IExplodeHandler)undead.getCapability(CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY, null);

            //■「爆発する権利をもってる」 かつ 「生物の手で死亡」 かつ「50%の確率」
            if (exp.hasChanceExplode() == true &&
                event.getSource().getTrueSource() instanceof EntityLivingBase &&
                undead.getRNG().nextFloat() >= 0.5f)
            {
                //■生成と追加
                EntityMagicExplode explode = new EntityMagicExplode(undead.world, undead);
                undead.world.addWeatherEffect(explode);

                //Server -> Client(All)
                PacketHandler.INSTANCE.sendToAll(new MessageMagicExplode(explode));
            }
        }
    }
}
