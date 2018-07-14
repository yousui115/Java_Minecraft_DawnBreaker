package yousui115.dawnbreaker.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.block.BlockDestroyWeb;

public class DBBlocks
{
    public static BlockDestroyWeb DESTROY_WEB;

    public static void init(RegistryEvent.Register<Block> eventIn)
    {
        create();

        register(eventIn);
    }

    /**
     *
     */
    public static void create()
    {
        DESTROY_WEB = new BlockDestroyWeb(Material.GROUND, MapColor.FOLIAGE);
        DESTROY_WEB
                .setBlockUnbreakable()
                .setHardness(-1)
                .setLightLevel(0)
//                .setTickRandomly(true)
                .setRegistryName(Dawnbreaker.MOD_ID, "block_destroy_web")
                .setUnlocalizedName("block_destroy_web");
    }

    /**
     *
     * @param eventIn
     */
    public static void register(RegistryEvent.Register<Block> eventIn)
    {
        eventIn.getRegistry().register(DESTROY_WEB);

    }
}
