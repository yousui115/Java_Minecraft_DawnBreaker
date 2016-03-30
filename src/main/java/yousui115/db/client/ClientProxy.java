package yousui115.db.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
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
    @Override
    public void registerRenders()
    {
        //■レンダラーの登録
        RenderingRegistry.registerEntityRenderingHandler(EntityDBExplode.class, new RenderDBExplode(getRenderManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityDB.class, new RenderDB(getRenderManager(), getRenderItem()));

        //■レイヤー登録
        // ▼ゾンビー
        RenderZombie render_zmb = (RenderZombie)getRenderManager().entityRenderMap.get(EntityZombie.class);
        List<LayerRenderer<EntityZombie>> listLayer_zmb = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, render_zmb, 9);
        listLayer_zmb.add(new LayerZombie(render_zmb));
        List<LayerRenderer<EntityZombie>> listLayer_zmb2 = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, render_zmb, 10);
        listLayer_zmb2.add(new LayerZombie(render_zmb));

        // ▼スケルトーン
        RenderSkeleton render_ske = (RenderSkeleton)getRenderManager().entityRenderMap.get(EntitySkeleton.class);
        List<LayerRenderer<EntitySkeleton>> listLayer_ske = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, render_ske, 4);
        listLayer_ske.add(new LayerSkeleton(render_ske));

        // ▼腐豚
        RenderPigZombie render_pz = (RenderPigZombie)getRenderManager().entityRenderMap.get(EntityPigZombie.class);
        List<LayerRenderer<EntityPigZombie>> listLayer_pz = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, render_pz, 4);
        listLayer_pz.add(new LayerPigZombie(render_pz));

    }

    @Override
    public void registerModels()
    {
        modelRL_DB  = new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB,       "inventory");
        modelRL_DBL = new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB + "L", "inventory");

        //■モデルリソースロケーションの登録
        ModelLoader.setCustomModelResourceLocation(DB.itemDB,       0, (ModelResourceLocation)modelRL_DB);
        ModelLoader.setCustomModelResourceLocation(DB.itemMeridama, 0, new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameMeridama, "inventory"));

        //■1つのアイテムIDに対して、複数のモデルをリンクさせる。
        ModelBakery.registerItemVariants(DB.itemDB, modelRL_DB, modelRL_DBL);

        //■テクスチャ・モデル指定JSONファイル名の登録。らしいよ。
        ModelLoader.setCustomMeshDefinition(DB.itemDB,
                                            new ItemMeshDefinition()
                                            {
                                                public ModelResourceLocation getModelLocation(ItemStack stack)
                                                {
                                                    return new ModelResourceLocation(new ResourceLocation(DB.MOD_ID, DB.nameDB), "inventory");
                                                }
                                            });
    }

    @Override
    public EntityPlayer getPlayer() { return Minecraft.getMinecraft().thePlayer; }
    @Override
    public RenderManager getRenderManager() { return Minecraft.getMinecraft().getRenderManager(); }
    @Override
    public RenderItem getRenderItem() { return Minecraft.getMinecraft().getRenderItem(); }
}
