package yousui115.dawnbreaker.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.util.DBUtils;

public class EnchantmentBOD extends EnchantmentDamage
{

    public EnchantmentBOD(Enchantment.Rarity rarityIn, int damageTypeIn, EntityEquipmentSlot... slots)
    {
        super(rarityIn, 0, slots);
    }

    /**
     * ■Returns the minimal value of enchantability needed on the enchantment level passed.
     *   Enchantability : たしか数値が高いと良いエンチャントが付く、とかそんなの
     */
    @Override
    public int getMinEnchantability(int enchantmentLevel) { return Integer.MAX_VALUE - 2; }

    /**
     * ■Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    @Override
    public int getMaxEnchantability(int enchantmentLevel) { return Integer.MAX_VALUE - 1; }

    /**
     * ■レベル(下限)
     */
    @Override
    public int getMinLevel() { return 1; }

    /**
     * ■レベル(上限)
     */
    @Override
    public int getMaxLevel() { return 1; }

    /**
     * ■クリーチャーのタイプで追加ダメージとか
     */
//    @Override
//    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
//    {
//        float damage = 0f;
//
//        if (creatureType == EnumCreatureAttribute.UNDEAD)
//        {
//            damage = 3f;
//        }
//
//        return damage;
//    }

    /**
     * ■Return the name of key in translation table of this enchantment.
     */
    @Override
    public String getName() { return "enchantment.damage." + this.name; }

    public String getTransName() { return I18n.translateToLocal(this.getName()); }

    /**
     * ■Returns the correct traslated name of the enchantment and the level in roman numbers.
     */
    @Override
    public String getTranslatedName(int level)
    {
        return TextFormatting.DARK_GRAY + getTransName();
    }

    /**
     * ■渡されたエンチャントと共存出来るか否か
     */
    @Override
    public boolean canApplyTogether(Enchantment ench) { return false; }

    /**
     * ■付呪可能アイテムか否か
     */
    @Override
    public boolean canApply(ItemStack stack)
    {
        return this.canApplyAtEnchantingTable(stack);
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return stack.getItem() instanceof ItemDawnbreaker;
    }

    /**
     * ■ダメージを与えると呼ばれる
     *   ※offhandに持って、右ストレートで殴っても影響するので注意
     */
    @Override
    public void onEntityDamaged(EntityLivingBase userIn, Entity targetIn, int levelIn)
    {
        //■プレイヤーがメインハンドにドーンブレイカーを持ってる そして 信仰心がある
        if (userIn instanceof EntityPlayer &&
            userIn.getHeldItemMainhand().getItem() instanceof ItemDawnbreaker &&
            userIn.hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
        {
            IFaithHandler hdlFaith = userIn.getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);

            //■闇を照らす炎（全てのMOB）
            targetIn.setFire(4);

            //■追加ダメージ（アンデッドのみ）
            if (DBUtils.isUndead(targetIn) == true)
            {
                EntityCreature undead = (EntityCreature)targetIn;

                //■追加ダメージ（基礎）
                float damage = 0.01f * (float)hdlFaith.getUndeadKillCount();

                //■修理特典
                if (ItemDawnbreaker.RepairOpt.canCritical(hdlFaith.getRepairDBCount(), undead.world.rand) == true)
                {
                    undead.setHealth(0.1f);
                }
                else
                {
                    damage *= ItemDawnbreaker.RepairOpt.magDamage(hdlFaith.getRepairDBCount());
                }
//                if (ItemDawnbreaker.RepairOpt.CRITICAL.canAction(hdlFaith.getRepairDBCount()) == true &&
//                    undead.world.rand.nextFloat() < 0.001f)
//                {
//                    undead.setHealth(0.1f);
//                }
//                else if (ItemDawnbreaker.RepairOpt.DAMAGEx3.canAction(hdlFaith.getRepairDBCount()) == true)
//                {
//                    damage *= 3f;
//                }
//                else if (ItemDawnbreaker.RepairOpt.DAMAGEx2.canAction(hdlFaith.getRepairDBCount()) == true)
//                {
//                    damage *= 2f;
//                }

                //■ダメージを与える
                undead.hurtResistantTime = 0;
                undead.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)userIn), damage);
            }
        }

        if (targetIn instanceof EntityLiving == false) { return; }
        EntityLiving living = (EntityLiving)targetIn;
    }

    /**
     * Is this enchantment allowed to be enchanted on books via Enchantment Table
     * @return false to disable the vanilla feature
     */
    public boolean isAllowedOnBooks()
    {
        return false;
    }
}
