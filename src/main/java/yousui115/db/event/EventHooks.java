package yousui115.db.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.db.DB;
import yousui115.db.Util_DB;
import yousui115.db.entity.EntityDBExplode;
import yousui115.db.item.ItemDB;
import yousui115.db.network.MessageMagic;
import yousui115.db.network.MessagePlayerProperties;
import yousui115.db.network.PacketHandler;

public class EventHooks
{
    /**
     * ■倒したUndeadの数を記録する(Server)
     * @param event
     */
    @SubscribeEvent
    public void countKill_Undead(LivingDeathEvent event)
    {
        //■サーバのみ(まぁ、DSからPlayer取れるのはサーバだけなんだけどね)
        if (event.getEntity().worldObj.isRemote) { return; }

        //■Entityを倒したのはプレイヤー
        EntityPlayer player = getPlayerFromDS(event.getSource());

        //■そのEntityはUndeadである
        if (player != null && Util_DB.isUndead(event.getEntityLiving()))
        {
            //ExtendedPlayerProperties.get(player).addCountKill_Undead();
            Util_DB.addCountKill_Undead(player, 1);

            //TODO 1Tickで100体倒したりするとえらい事になりそう。
            PacketHandler.INSTANCE.sendTo(new MessagePlayerProperties(player), (EntityPlayerMP)player);
        }
    }

    /**
     * ■エンチャント「夜明け」で斬られたUndeadは確立で爆発
     * @param event
     */
    @SubscribeEvent
    public void explodeUndead(LivingDeathEvent event)
    {
        //■サーバのみ(まぁ、DSからPlayer取れるのはサーバだけなんだけどね)
        if (event.getEntity().worldObj.isRemote) { return; }

        //■生物がトドメを刺さないといけない
        if (event.getSource().getEntity() == null ||
            !(event.getSource().getEntity() instanceof EntityLivingBase))
        { return; }

        boolean letsExplode = false;

        //if (canExplode(event.entityLiving))
        if (Util_DB.hasExplodeChance(event.getEntityLiving()))
        {
            letsExplode = true;
        }
        else if (Util_DB.isUndead(event.getEntityLiving()))
        {
            //■一撃でヤれちゃうと爆発しないので、ここで補正

            // ▼1.プレイヤーである(フォロワーとかは無視)
            EntityPlayer player = getPlayerFromDS(event.getSource());
            if (player == null) { return; }
            // ▼2.アイテムを持っている
            ItemStack stack = player.inventory.getCurrentItem();
            if (stack == null) { return; }
            // ▼3.それはエンチャントを保持している
            NBTTagList tags = stack.getEnchantmentTagList();
            if (tags == null || tags.hasNoTags()) { return; }
            // ▼4.エンチャントは「夜明け」である
            for (int idx = 0; idx < tags.tagCount(); ++idx)
            {
                int encID = tags.getCompoundTagAt(idx).getShort("id");
                if (Util_DB.getID_Enc_BoD() == encID)
                {
                    letsExplode = true;
                    break;
                }
            }
        }

        //■x割の確立で爆発
        if (letsExplode && Util_DB.rnd.nextFloat() > 0.3f)
        {
            EntityDBExplode explode = new EntityDBExplode(event.getEntity().worldObj, event.getEntityLiving());
            event.getEntity().worldObj.addWeatherEffect(explode);

            //■Server -> Client
            PacketHandler.INSTANCE.sendToAll(new MessageMagic(explode));
        }
    }

    /**
     * ■メリ玉 授与(Server)
     * @param event
     */
    @SubscribeEvent
    public void presentFromMeridia(LivingDropsEvent event)
    {
        //■サーバのみ
        if (event.getEntity().worldObj.isRemote) { return; }

        //■Entityを倒したのはプレイヤー
        EntityPlayer player = getPlayerFromDS(event.getSource());

        //■そのプレイヤーはX体のアンデットを倒している！素敵！メリ玉あげちゃう！
        if (player != null &&
            Util_DB.isUndead(event.getEntityLiving()) &&
            //ExtendedPlayerProperties.get(player).getCountKill_Undead() % 100 == 10)
            Util_DB.getCountKill_Undead(player) % 100 == 10)
        {
            World world = event.getEntityLiving().worldObj;
            double posY = MathHelper.clamp_double(event.getEntityLiving().posY, 0d, 255d);   //奈落・天上対策
            BlockPos bp = new BlockPos(event.getEntityLiving().posX, posY, event.getEntityLiving().posZ);

            //■謙虚なチェスト生成(容赦ない設置)
            world.setBlockState(bp, Blocks.chest.getDefaultState());

            //■チェストインベントリ へアクセス
            TileEntity inv = world.getTileEntity(bp);

            //■メリ玉をそっと置いていく
            if (inv != null && inv instanceof TileEntityChest)
            {
                ((TileEntityChest)inv).setInventorySlotContents(13, new ItemStack(DB.itemMeridama, 1));
            }
        }
    }

    /**
     * ■金床コンテナ情報(入力スロット1or2(もしくは両方)にputして、outputはまだpickupしてない)
     * @param event
     */
    @SubscribeEvent
    public void onAnvilChange(AnvilUpdateEvent event)
    {
        //■left:DB(ダメージ有り)  +  right:meridama ならば処理する
        if (event.getLeft() != null &&
            event.getLeft().getItem() instanceof ItemDB &&
            event.getLeft().getItemDamage() != 0 &&
            event.getRight() != null &&
            event.getRight().getItem().equals(DB.itemMeridama))
        {
            //■修理コストは常に1(メリディアの恩恵)
            event.setCost(1);
            event.setMaterialCost(1);

            //■修理量は最大値まで。(メリディアの恩恵)
            event.setOutput(event.getLeft().copy());
            event.getOutput().setItemDamage(0);
        }
    }

    /**
     * ■金床コンテナ情報(output をスロットから pickup した)
     * @param event
     */
    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event)
    {
        //TODO ※注意！(Forge1671)
        // left   に right
        // right  に output
        // output に left
        // が入っちゃってる！

        //■left:DB(ダメージ有り)  +  right:meridama ならば処理する
        if (event.getOutput() != null &&
            event.getOutput().getItem() instanceof ItemDB &&
            event.getOutput().getItemDamage() != 0 &&
            event.getLeft() != null &&
            event.getLeft().getItem().equals(DB.itemMeridama))
        {
            //ExtendedPlayerProperties.get(event.entityPlayer).addCountRepairAnvil();
            Util_DB.addCountRepairAnvil(event.getEntityPlayer(), 1);
        }
    }


    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event)
    {
        if (event.getEntity() != null)
        {
            event.getEntity().getDataManager().register(DB.DP_DB_FLAGS, Byte.valueOf((byte)0));
        }
    }

    /* =====================================  =========================================== */


    /**
     * ■ダメージソースがプレイヤーからなら、プレイヤーを返す
     * @param source
     * @return
     */
    private static EntityPlayer getPlayerFromDS(DamageSource source)
    {
        if (source.getEntity() != null &&
            source.getEntity() instanceof EntityPlayer)
        {
            return (EntityPlayer)source.getEntity();
        }

        return null;
    }
}
