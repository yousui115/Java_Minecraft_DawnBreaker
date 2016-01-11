package yousui115.db.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.db.DB;
import yousui115.db.Util_DB;
import yousui115.db.common.ExtendedPlayerProperties;
import yousui115.db.entity.EntityDBExplode;
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
        if (event.entity.worldObj.isRemote) { return; }

        //■Entityを倒したのはプレイヤー
        EntityPlayer player = getPlayerFromDS(event.source);

        //■そのEntityはUndeadである
        if (player != null && Util_DB.isUndead(event.entityLiving))
        {
            ExtendedPlayerProperties.get(player).addCountKill_Undead();

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
        if (event.entity.worldObj.isRemote) { return; }

        //■生物がトドメを刺さないといけない
        if (event.source.getEntity() == null ||
            !(event.source.getEntity() instanceof EntityLivingBase))
        { return; }

        boolean letsExplode = false;

        //if (canExplode(event.entityLiving))
        if (Util_DB.hasExplodeChance(event.entityLiving))
        {
            letsExplode = true;
        }
        else if (Util_DB.isUndead(event.entityLiving))
        {
            //■一撃でヤれちゃうと爆発しないので、ここで補正

            // ▼1.プレイヤーである(フォロワーとかは無視)
            EntityPlayer player = getPlayerFromDS(event.source);
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
            EntityDBExplode explode = new EntityDBExplode(event.entity.worldObj, event.entityLiving);
            event.entity.worldObj.addWeatherEffect(explode);

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
        if (event.entity.worldObj.isRemote) { return; }

        //■Entityを倒したのはプレイヤー
        EntityPlayer player = getPlayerFromDS(event.source);

        //■そのプレイヤーはX体のアンデットを倒している！素敵！メリ玉あげちゃう！
        if (player != null &&
            Util_DB.isUndead(event.entityLiving) &&
            ExtendedPlayerProperties.get(player).getCountKill_Undead() % 100 == 10)
        {
            World world = event.entityLiving.worldObj;
            double posY = MathHelper.clamp_double(event.entityLiving.posY, 0d, 255d);   //奈落・天上対策
            BlockPos bp = new BlockPos(event.entityLiving.posX, posY, event.entityLiving.posZ);

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
