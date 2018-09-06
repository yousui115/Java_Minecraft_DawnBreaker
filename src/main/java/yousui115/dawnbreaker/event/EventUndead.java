package yousui115.dawnbreaker.event;

import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.capability.undead.CapabilityUndeadHandler;
import yousui115.dawnbreaker.capability.undead.IUndeadHandler;
import yousui115.dawnbreaker.capability.undead.UndeadHandler;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.IWorldHandler;
import yousui115.dawnbreaker.entity.EntityMagicExplode;
import yousui115.dawnbreaker.entity.ai.EntityAIAvoidPlayer;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.undead.MessageExplode;
import yousui115.dawnbreaker.network.undead.MessageJoinUndead;
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
//        if (event.getObject() instanceof EntityCreature == false) { return; }
        if (DBUtils.isUndead(event.getObject()) == false) { return; }

        //■きゃぱびりてぃ の追加
        event.addCapability(CapabilityUndeadHandler.KYE, new UndeadHandler());
    }

    /**
     * ■AIの植え付け と きゃぱびりてぃの同期
     * @param event
     */
    @SubscribeEvent
    public void joinWorldUndead(EntityJoinWorldEvent event)
    {
        //■対象：あんでっど
//        if (event.getEntity() instanceof EntityCreature == false) { return; }
        if (DBUtils.isUndead(event.getEntity()) == false) { return; }
        EntityCreature creature = (EntityCreature)event.getEntity();

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
     * ■ターゲットを定めた時に呼ばれる
     * @param event
     */
    @SubscribeEvent
    public void setAttackTarget(LivingSetAttackTargetEvent event)
    {
        if (event.getEntityLiving() instanceof EntityCreature == false) { return; }
        EntityCreature creature = (EntityCreature)event.getEntityLiving();
        //■サーバーのみ
        if (creature.world.isRemote) { return; }
        if (creature.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null) == false) { return; }
        //■
        IUndeadHandler hdlUndead = (IUndeadHandler)creature.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);

        hdlUndead.setHasTargetPlayer(creature.getAttackTarget());
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
        if (creature.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null) == true)
        {
            IUndeadHandler hdlUndead = (IUndeadHandler)creature.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);
            if (hdlUndead.isDirty() == true)
            {
//                System.out.println("Server(Tick) : " + creature.getEntityId() + " : " + explode.getTickAvoid());
                PacketHandler.INSTANCE.sendToAll(new MessageExplode(creature, hdlUndead));
                hdlUndead.resetDirty();
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
        if (DBUtils.isUndead(event.getTarget()) == true)
        {
            EntityCreature undead = (EntityCreature)event.getTarget();

            //■プレイヤー
            EntityPlayer player = event.getEntityPlayer();
            if (player == null) { return; }

            //■メインハンドにドーンブレイカーを持ってる。
            ItemStack mainStack = player.getHeldItemMainhand();
            if (DBUtils.isDBwithBoD(mainStack) == true)
            {
                //■爆発するチャンスを与えよう。
                if (undead.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null) == true)
                {
                    IUndeadHandler hdlUndead = (IUndeadHandler)undead.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);

                    hdlUndead.setChanceExplode(true);
                }

                //■ついでにslow効果
                if (player.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
                {
                    IFaithHandler hdlFaith = player.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

                    if (ItemDawnbreaker.RepairOpt.SLOW.canAction(hdlFaith.getRepairDBCount()) == true)
                    {
                        undead.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30 * 20, 0));
                    }
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
        if (DBUtils.isUndead(event.getEntityLiving()) == false) { return; }
        EntityCreature undead = (EntityCreature)event.getEntityLiving();

        //■サーバーのみ
        if (undead.world.isRemote == true) { return; }

        IFaithHandler hdlF = null;

        //■アンデッド討伐数のカウントアップ
        if (event.getSource().getTrueSource() instanceof EntityPlayer &&
            event.getSource().getTrueSource().hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
        {
            hdlF = event.getSource().getTrueSource().getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
            hdlF.addUndeadKillCount();

            //■討伐数が一定値に至ったので、メリ玉進呈
            if (hdlF.getUndeadKillCount_hide() == 0)
            {
                World world = undead.world;
                double posY = MathHelper.clamp(event.getEntityLiving().posY, 0d, 255d);   //奈落・天上対策
                BlockPos bp = new BlockPos(event.getEntityLiving().posX, posY, event.getEntityLiving().posZ);

                //MEMO 初手110匹を一か所で一斉に討伐しても、貰えるメリ玉は一つ。のはず。
                //MEMO 例えば、ゾンビの足がチェストに埋まった状態でメリ玉付与が起こると、当然中身は消え、メリ玉が残る。はず。

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

        //■爆発チャンス！ + ドロップ増加
        if (hdlF != null && undead.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null) == true)
        {
            IUndeadHandler hdlUndead = (IUndeadHandler)undead.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);

            //■「爆発する権利をもってる」 かつ 「生物の手で死亡」 かつ「50%の確率」
            if (hdlUndead.hasChanceExplode() == true &&
                event.getSource().getTrueSource() instanceof EntityLivingBase)
            {
                int worldFaith = 0;
                int worldFaithMax = 1000;   //適当な値

                World overworld = DimensionManager.getWorld(0);
                if (overworld != null)
                {
                    IWorldHandler hdlW = overworld.getCapability(CapWorldHandler.WORLD_HANDLER_CAPABILITY, null);
                    if (hdlW != null)
                    {
                        worldFaith = hdlW.getNumWorldFaith();
                        worldFaithMax = hdlW.getNumWorldFaithDispMax();
                    }
                }

                //■世界信仰値の蓄積比率
                float ratio = (float)worldFaith / (float)worldFaithMax;

                if (undead.getRNG().nextFloat() <= 0.6f * ratio + 0.2f)
                {
                    boolean isSlownessExp = ItemDawnbreaker.RepairOpt.SLOW_EXPLODE.canAction(hdlF.getRepairDBCount());

                    //■生成と追加
                    EntityMagicExplode explode = new EntityMagicExplode(undead.world, undead, isSlownessExp, ratio);
                    undead.world.spawnEntity(explode);
                }
            }

            //■ドロップ
            if (event.getSource() != null &&
                event.getSource().getImmediateSource() instanceof EntityPlayer &&
                ((EntityPlayer)event.getSource().getImmediateSource()).getHeldItemMainhand().getItem() instanceof ItemDawnbreaker)
            {
                int chance = ItemDawnbreaker.RepairOpt.countDrop(hdlF.getRepairDBCount()) - 1;
                if (chance <= 0) { return; }

                int lootLevel = 1;

                //■ドロップ処理(privateメソッドの実行)
                try
                {
                    //■下ごしらえ
                    Class[] args = { boolean.class, int.class, DamageSource.class };
                    Class<EntityLiving> c = EntityLiving.class;

                    //■ターゲットメソッド
                    String mName = Dawnbreaker.isJar == true ? "func_184610_a" : "dropLoot";
                    Method m = c.getDeclaredMethod(mName, args);

                    //■アクセシビリティ
                    m.setAccessible(true);

                    for (int idx = 0; idx < chance; idx++)
                    {
                        //■アクセス！
                        m.invoke((EntityLiving)event.getEntityLiving(), true, lootLevel, event.getSource());
                    }

                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
