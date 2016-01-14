package yousui115.db.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import yousui115.db.Util_DB;
import yousui115.db.common.ExtendedPlayerProperties;

public class EnchantBoD extends EnchantmentDamage
{

    public EnchantBoD(int enchID, String enchName, int enchWeight)
    {
        super(enchID, new ResourceLocation(enchName), enchWeight, 1);
        this.setName(enchName);
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
    @Override
    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) { return 0f; }

    /**
     * ■Return the name of key in translation table of this enchantment.
     */
    @Override
    public String getName() { return "enchantment.damage." + this.name; }

    /**
     * ■Returns the correct traslated name of the enchantment and the level in roman numbers.
     */
    public String getTranslatedName(int level)
    {
        return EnumChatFormatting.YELLOW + StatCollector.translateToLocal(this.getName());

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
        return stack.getItem() instanceof ItemSword;
    }

    /**
     * ■ダメージを与えると呼ばれる
     */
    @Override
    public void onEntityDamaged(EntityLivingBase userIn, Entity targetIn, int levelIn)
    {
        //■無条件で火属性ダメージ(短)
        targetIn.setFire(4);

        if (Util_DB.isUndead(targetIn))
        {
            //■Undeadには、爆発する権利を与えてやろう(拒否権は無い)
            Util_DB.setExplodeChance(targetIn);

            //■修理回数による、Undeadへのダメージ補正
            if (userIn instanceof EntityPlayer)
            {
                int count = ExtendedPlayerProperties.get((EntityPlayer)userIn).getCountRepairAnvil();
                targetIn.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)userIn), 0.5f * count);
            }
        }
    }

}
