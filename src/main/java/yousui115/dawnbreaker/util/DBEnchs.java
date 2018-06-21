package yousui115.dawnbreaker.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.enchantment.EnchantmentBOD;

public class DBEnchs
{
    public static EnchantmentBOD ENCH_BOD;

    public static void init(RegistryEvent.Register<Enchantment> eventIn)
    {
        create();

        register(eventIn);

    }

    protected static void create()
    {
        ENCH_BOD = new EnchantmentBOD(Enchantment.Rarity.VERY_RARE, 0, EntityEquipmentSlot.MAINHAND);
        ENCH_BOD.setRegistryName(Dawnbreaker.MOD_ID, "break_of_dawn")
                .setName("break_of_dawn");
    }

    protected static void register(RegistryEvent.Register<Enchantment> eventIn)
    {
        eventIn.getRegistry().registerAll(ENCH_BOD);
    }
}
