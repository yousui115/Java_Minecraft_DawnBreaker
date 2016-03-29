package yousui115.db.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPigZombie;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yousui115.db.DB;
import yousui115.db.client.render.LayerPigZombie;
import yousui115.db.client.render.LayerSkeleton;
import yousui115.db.client.render.LayerZombie;
import yousui115.db.client.render.RenderDB;
import yousui115.db.client.render.RenderDBExplode;
import yousui115.db.common.CommonProxy;
import yousui115.db.entity.EntityDB;
import yousui115.db.entity.EntityDBExplode;

public class ClientProxy extends CommonProxy
{
    protected ModelResourceLocation mrESword[] = new ModelResourceLocation[2];


    @Override
    public void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityDBExplode.class, new RenderDBExplode(getRenderManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityDB.class, new RenderDB(getRenderManager(), getRenderItem()));

        //■ゾンビー
        RenderZombie zombie = (RenderZombie)getRenderManager().entityRenderMap.get(EntityZombie.class);
//        List<LayerRenderer<EntityZombie>> ooo = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, zombie, 4);
        List<LayerRenderer<EntityZombie>> ooo = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, zombie, 9);
        ooo.add(new LayerZombie(zombie));
//        List<LayerRenderer<EntityZombie>> ppp = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, zombie, 5);
        List<LayerRenderer<EntityZombie>> ppp = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, zombie, 10);
        ppp.add(new LayerZombie(zombie));

        //■スケルトーン
        RenderSkeleton skeleton = (RenderSkeleton)getRenderManager().entityRenderMap.get(EntitySkeleton.class);
//        skeleton.addLayer(new LayerSkeleton(skeleton));
        List<LayerRenderer<EntitySkeleton>> ske = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, skeleton, 4);
        ske.add(new LayerSkeleton(skeleton));


        //■腐豚
        RenderPigZombie pz = (RenderPigZombie)getRenderManager().entityRenderMap.get(EntityPigZombie.class);
//        pz.addLayer(new LayerPigZombie(pz));
        List<LayerRenderer<EntityPigZombie>> pigz = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, pz, 4);
        pigz.add(new LayerPigZombie(pz));

    }

    @Override
    public void registerModels()
    {
        ModelLoader.setCustomModelResourceLocation(DB.itemDB,       0, new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB,       "inventory"));
        ModelLoader.setCustomModelResourceLocation(DB.itemMeridama, 0, new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameMeridama, "inventory"));

        ModelBakery.registerItemVariants(DB.itemDB, new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB,       "inventory"),
                                                    new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB + "L", "inventory"));

        ModelLoader.setCustomMeshDefinition(DB.itemDB, new ItemMeshDefinition(){
            public ModelResourceLocation getModelLocation(ItemStack stack){
                return new ModelResourceLocation(new ResourceLocation(DB.MOD_ID, DB.nameDB), "inventory");
            }
        });

    }

    @Override
    public void test()
    {
        //TODO: 確認用
        IBakedModel modelDB = getRenderItem().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB + "L", "inventory"));
    }
    /**
     * ModelResouceLocation はクライアント側だけなので
     * ResouceLocation に一時的に形を変えお返しする。
     * 受け取り側でModelResouceLocationにキャストし直す。
     */
    @Override
    public ResourceLocation getESwordMRL(int num)
    {
        num = MathHelper.clamp_int(num, 0, mrESword.length - 1);
        return mrESword[num];
    }


    @Override
    public EntityPlayer getEntityPlayerInstance()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public RenderManager getRenderManager()
    {
        return Minecraft.getMinecraft().getRenderManager();
    }

    @Override
    public RenderItem getRenderItem()
    {
        return Minecraft.getMinecraft().getRenderItem();
    }
}
