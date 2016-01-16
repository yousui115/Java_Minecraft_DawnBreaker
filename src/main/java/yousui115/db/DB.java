package yousui115.db;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import yousui115.db.common.CommonProxy;
import yousui115.db.enchantment.EnchantBoD;
import yousui115.db.entity.EntityDB;
import yousui115.db.entity.EntityDBExplode;
import yousui115.db.event.EntityPropertiesEventHandler;
import yousui115.db.event.EventHooks;
import yousui115.db.item.ItemDB;
import yousui115.db.network.PacketHandler;

@Mod(modid = DB.MOD_ID, version = DB.VERSION)
public class DB
{
    public static final String MOD_ID = "db";
    public static final String MOD_DOMAIN = "yousui115." + MOD_ID;
    public static final String VERSION = "v2.0";

  //■このクラスのインスタンス
    @Mod.Instance(DB.MOD_ID)
    public static DB INSTANCE;

    //■クライアント側とサーバー側で異なるインスタンスを生成
    @SidedProxy(clientSide = MOD_DOMAIN + ".client.ClientProxy",
                serverSide = MOD_DOMAIN + ".common.CommonProxy")
    public static CommonProxy proxy;

    //■アイテムのインスタンス
    public static Item itemDB;
    public static String nameDB = "dawnbreaker";

    public static Item itemMeridama;
    public static String nameMeridama = "meridama";

    //■
    public static Enchantment encBoD;

    //■
    public static ToolMaterial toolMaterial;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //■アイテム生成
        // ▼1.アイテムのインスタンス生成
        itemMeridama = new Item()
                        {
                            public boolean hasEffect(ItemStack stack)
                            {
                                return true;
                            }
                        }
                        .setUnlocalizedName(nameMeridama)
                        .setCreativeTab(CreativeTabs.tabMaterials);

        toolMaterial = EnumHelper.addToolMaterial("DB", 2, 1561, 6.0F, 2.0F, 14).setRepairItem(new ItemStack(itemMeridama));

        itemDB = new ItemDB(toolMaterial)
                        .setUnlocalizedName(nameDB)
                        .setCreativeTab(CreativeTabs.tabCombat)
                        .setNoRepair();

        // ▼2.アイテムの登録
        GameRegistry.registerItem(itemDB, nameDB);
        GameRegistry.registerItem(itemMeridama, nameMeridama);
        // ▼3.テクスチャ・モデル指定JSONファイル名の登録
        proxy.registerModels();

        //■エンチャント生成
        //  Enchantment.getEnchantmentById(id) でも取得可能。
        encBoD = new EnchantBoD(Util_DB.getID_Enc_BoD(), "break_of_dawn", 100);

        //■エンティティ登録
        EntityRegistry.registerModEntity(EntityDBExplode.class, "DBExplode", 1, this, 64, 10, false);
        EntityRegistry.registerModEntity(       EntityDB.class,        "DB", 2, this, 64, 10, false);

        //■パケット登録
        PacketHandler.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        //二箇所に登録するので、先にインスタンスを生成しておく。
        EntityPropertiesEventHandler entityPropertiesEventHandler = new EntityPropertiesEventHandler();
        //Forge Eventの登録。EntityEvent.EntityConstructingとLivingDeathEventとEntityJoinWorldEvent
        MinecraftForge.EVENT_BUS.register(entityPropertiesEventHandler);
        //FML Eventの登録。PlayerRespawnEvent
        FMLCommonHandler.instance().bus().register(entityPropertiesEventHandler);

        MinecraftForge.EVENT_BUS.register(new EventHooks());

        //■Render登録
        proxy.registerRenders();

        //■レシピ
        ItemStack stack = new ItemStack(itemDB, 1, 0);
        stack.addEnchantment(encBoD, encBoD.getMinLevel());
        GameRegistry.addShapelessRecipe(stack,
                Items.iron_sword,
                itemMeridama
        );
    }
}
