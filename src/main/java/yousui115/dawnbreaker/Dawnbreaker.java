package yousui115.dawnbreaker;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.undead.CapabilityUndeadHandler;
import yousui115.dawnbreaker.client.event.EventBakedModel;
import yousui115.dawnbreaker.entity.EntityDawnbreaker;
import yousui115.dawnbreaker.entity.EntityMagicExplode;
import yousui115.dawnbreaker.event.EventAnvil;
import yousui115.dawnbreaker.event.EventBlock;
import yousui115.dawnbreaker.event.EventEntityPlayer;
import yousui115.dawnbreaker.event.EventUndead;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.proxy.CommonProxy;
import yousui115.dawnbreaker.util.DBBlocks;
import yousui115.dawnbreaker.util.DBEnchs;
import yousui115.dawnbreaker.util.DBItems;

@Mod(modid = Dawnbreaker.MOD_ID, name = Dawnbreaker.MOD_NAME, version = Dawnbreaker.MOD_VERSION)
@EventBusSubscriber
public class Dawnbreaker
{
    public static final String MOD_ID = "yousui115.dawnbreaker";
//    public static final String MOD_DOMAIN = MOD_ID;

    public static final String MOD_NAME = "Dawnbreaker";
    public static final String MOD_VERSION = "M1122_F2611_a7";

    @SidedProxy(clientSide = MOD_ID + ".proxy.ClientProxy",
                serverSide = MOD_ID + ".proxy.CommonProxy")
    public static CommonProxy proxy;

    private static Logger logger;

    public static boolean isJar;

    /**
     * ■前処理
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        String strSource = event.getSourceFile().getPath();
        isJar = strSource.endsWith(".jar");

        logger = event.getModLog();

        //■Capabilityの登録
        CapabilityFaithHandler.register();
        CapabilityUndeadHandler.register();

        //■パケット登録
        PacketHandler.register();

        //■Renderの生成 と Entity <-> Render の関連性登録
        proxy.registerRenderer();

        if (event.getSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(new EventBakedModel());
        }

    }

    /**
     * ■本処理
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //■Eventの登録
        MinecraftForge.EVENT_BUS.register(new EventEntityPlayer());
        MinecraftForge.EVENT_BUS.register(new EventUndead());
        MinecraftForge.EVENT_BUS.register(new EventAnvil());
        MinecraftForge.EVENT_BUS.register(new EventBlock());

        proxy.registerLeyer();

        //■からー
        proxy.registerItemColor();
        proxy.registerBlockColor();
    }

    /**
     * ■後処理
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    //=================================================================================

    /**
     * ■アイテムの登録
     * @param eventIn
     */
    @SubscribeEvent
    protected static void registerItem(RegistryEvent.Register<Item> eventIn)
    {
        //■アイテムの生成と登録
        DBItems.init(eventIn);
    }

    /**
     *
     * @param event
     */
    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> eventIn)
    {
        DBBlocks.init(eventIn);
    }

    /**
     * ■エンチャントの登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerEnchantments(RegistryEvent.Register<Enchantment> eventIn)
    {
        DBEnchs.init(eventIn);
    }

    /**
     * ■モデルの登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerItemModel(ModelRegistryEvent eventIn)
    {
        proxy.registerItemModel();
    }

    /**
     * ■エンティティの登録
     * @param event
     */
    @SubscribeEvent
    public static void registerEntity(final RegistryEvent.Register<EntityEntry> event)
    {
        //■Dawnbreaker
        event.getRegistry().register(
                EntityEntryBuilder.create()
                    .entity(EntityDawnbreaker.class)
                    .id(new ResourceLocation(MOD_ID, "entity_dawnbreaker"), 1)
                    .name("entity_dawnbreaker")
                    .tracker(50, 5, false)
                    .build()
            );

        //■爆発
        event.getRegistry().register(
                EntityEntryBuilder.create()
                    .entity(EntityMagicExplode.class)
                    .id(new ResourceLocation(MOD_ID, "entity_magicexplode"), 2)
                    .name("entity_magicexplode")
                    .tracker(50, 5, false)
                    .build()
            );
    }

    /**
     * ■レシピの登録
     * @param event
     */
    @SubscribeEvent
    protected static void registerRecipe(RegistryEvent.Register<IRecipe> event)
    {
        ItemStack stack = new ItemStack(DBItems.DAWNBREAKER);
        stack.addEnchantment(DBEnchs.ENCH_BOD, DBEnchs.ENCH_BOD.getMinLevel());
        IRecipe recipe = new ShapelessOreRecipe(new ResourceLocation(MOD_ID, "recipe_dawnbreaker"),
                                                stack,
                                                new ItemStack(DBItems.MERIDAMA),
                                                new ItemStack(Items.IRON_SWORD));
        recipe.setRegistryName(new ResourceLocation(MOD_ID, "recipe_dawnbreaker"));
        event.getRegistry().register(recipe);
    }
}
