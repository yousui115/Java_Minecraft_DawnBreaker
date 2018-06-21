package yousui115.dawnbreaker;

import org.apache.logging.log4j.Logger;

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
import yousui115.dawnbreaker.client.event.EventBakedModel;
import yousui115.dawnbreaker.entity.EntityDawnbreaker;
import yousui115.dawnbreaker.entity.EntityMagicExplode;
import yousui115.dawnbreaker.event.EventAnvil;
import yousui115.dawnbreaker.event.EventEntityPlayer;
import yousui115.dawnbreaker.event.EventUndead;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.proxy.CommonProxy;
import yousui115.dawnbreaker.util.DBEnchs;
import yousui115.dawnbreaker.util.DBItems;

@Mod(modid = Dawnbreaker.MOD_ID, name = Dawnbreaker.MOD_NAME, version = Dawnbreaker.MOD_VERSION)
@EventBusSubscriber
public class Dawnbreaker
{
    public static final String MOD_ID = "dawnbreaker";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;

    public static final String MOD_NAME = "Dawnbreaker";
    public static final String MOD_VERSION = "M1122_F2705_v1";

    @SidedProxy(clientSide = MOD_DOMAIN + ".proxy.ClientProxy",
                serverSide = MOD_DOMAIN + ".proxy.CommonProxy")
    public static CommonProxy proxy;

    private static Logger logger;

    /**
     * ■前処理
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        //■Capabilityの登録
        CapabilityFaithHandler.register();

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

        proxy.registerLeyer();
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
                    .id(new ResourceLocation(MOD_ID, "db_dawnbreaker"), 1)
                    .name("db_dawnbreaker")
                    .tracker(50, 5, false)
                    .build()
            );

        //■爆発
        event.getRegistry().register(
                EntityEntryBuilder.create()
                    .entity(EntityMagicExplode.class)
                    .id(new ResourceLocation(MOD_ID, "db_magicexplode"), 2)
                    .name("db_magicexplode")
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
        IRecipe recipe = new ShapelessOreRecipe(new ResourceLocation(MOD_ID, "db_recipe_dawnbreaker"),
                                                stack,
                                                new ItemStack(DBItems.MERIDAMA),
                                                new ItemStack(Items.IRON_SWORD));
        recipe.setRegistryName(new ResourceLocation(MOD_ID, "db_recipe_dawnbreaker"));
        event.getRegistry().register(recipe);
    }
}