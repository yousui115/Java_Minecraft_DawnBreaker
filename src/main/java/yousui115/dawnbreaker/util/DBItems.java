package yousui115.dawnbreaker.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.item.ItemDawnbreaker;

public class DBItems
{
    public static Item DAWNBREAKER;
    public static ResourceLocation RL_DAWNBREAKER = new ResourceLocation(Dawnbreaker.MOD_ID, "dawnbreaker");

    public static Item MERIDAMA;

    /**
     * ■
     */
    public static void init(RegistryEvent.Register<Item> eventIn)
    {
        create();

        register(eventIn);
    }

    /**
     * ■
     */
    protected static void create()
    {
        DAWNBREAKER = new ItemDawnbreaker(ToolMaterial.IRON)
                .setCreativeTab(CreativeTabs.COMBAT)
                .setHasSubtypes(false)
                .setMaxDamage(ToolMaterial.DIAMOND.getMaxUses())
                .setMaxStackSize(1)
                .setNoRepair()
                .setRegistryName(RL_DAWNBREAKER)
                .setUnlocalizedName(RL_DAWNBREAKER.getResourcePath());

        MERIDAMA = new Item()
                .setCreativeTab(CreativeTabs.MISC)
                .setHasSubtypes(false)
                .setRegistryName(Dawnbreaker.MOD_ID, "meridama")
                .setUnlocalizedName("meridama");
    }

    /**
     * ■
     */
    protected static void register(RegistryEvent.Register<Item> eventIn)
    {
        eventIn.getRegistry().registerAll(DAWNBREAKER, MERIDAMA);

    }
}
