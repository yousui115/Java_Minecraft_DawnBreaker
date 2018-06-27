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

    /**
     * ■Returns the correct traslated name of the enchantment and the level in roman numbers.
     */
    @Override
    public String getTranslatedName(int level)
    {
        //return TextFormatting.YELLOW + StatCollector.translateToLocal(this.getName());
        return TextFormatting.YELLOW + I18n.translateToLocal(this.getName());
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

            //■聖なる炎 (1 + 修理回数/10 second)
            targetIn.setFire(1 + hdlFaith.getRepairDBCount()/10);

            //■対象がアンデッド
            if (targetIn instanceof EntityCreature &&
                DBUtils.isUndead((EntityCreature)targetIn) == true)
            {
                targetIn.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)userIn), 0.01f * (float)hdlFaith.getUndeadKillCount());
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
