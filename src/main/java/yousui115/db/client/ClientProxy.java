package yousui115.db.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPigZombie;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
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
    protected ModelResourceLocation mrESword[] = new ModelResourceLocation[2];

    @Override
    public void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityDBExplode.class, new RenderDBExplode(getRenderManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityDB.class, new RenderDB(getRenderManager(), getRenderItem()));

        //■ゾンビー
        RenderZombie zombie = (RenderZombie)getRenderManager().entityRenderMap.get(EntityZombie.class);
        List<LayerRenderer<EntityZombie>> ooo = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, zombie, 4);
        ooo.add(new LayerZombie(zombie));
        List<LayerRenderer<EntityZombie>> ppp = ObfuscationReflectionHelper.getPrivateValue(RenderZombie.class, zombie, 5);
        ppp.add(new LayerZombie(zombie));

        //■スケルトーン
        RenderSkeleton skeleton = (RenderSkeleton)getRenderManager().entityRenderMap.get(EntitySkeleton.class);
        skeleton.addLayer(new LayerSkeleton(skeleton));

        //■腐豚
        RenderPigZombie pz = (RenderPigZombie)getRenderManager().entityRenderMap.get(EntityPigZombie.class);
        pz.addLayer(new LayerPigZombie(pz));

    }

    @Override
    public void registerModels()
    {
        ModelLoader.setCustomModelResourceLocation(DB.itemDB,       0, new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB,       "inventory"));
        ModelLoader.setCustomModelResourceLocation(DB.itemMeridama, 0, new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameMeridama, "inventory"));
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
