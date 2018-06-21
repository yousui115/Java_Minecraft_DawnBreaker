package yousui115.dawnbreaker.proxy;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import yousui115.dawnbreaker.client.render.LayerUndeadCharge;
import yousui115.dawnbreaker.client.render.RenderDawnbreaker;
import yousui115.dawnbreaker.client.render.RenderMagicExplode;
import yousui115.dawnbreaker.entity.EntityDawnbreaker;
import yousui115.dawnbreaker.entity.EntityMagicExplode;
import yousui115.dawnbreaker.util.DBItems;

public class ClientProxy extends CommonProxy
{
    /**
     * ■モデルの登録
     */
    @Override
    public void registerItemModel()
    {
        //■アイテムのモデル　登録
        // ▼Dawnbreaker
//        ModelLoader.setCustomModelResourceLocation(DBItems.DAWNBREAKER, 0, new ModelResourceLocation(DBItems.DAWNBREAKER.getRegistryName(), "inventory"));
        ModelBakery.registerItemVariants(DBItems.DAWNBREAKER, DBItems.RL_DAWNBREAKER);
        ModelLoader.setCustomMeshDefinition(DBItems.DAWNBREAKER, createMeshDefinition(new ResourceLocation[] { DBItems.RL_DAWNBREAKER }));



        // ▼meridama
        ModelLoader.setCustomModelResourceLocation(DBItems.MERIDAMA, 0, new ModelResourceLocation(DBItems.MERIDAMA.getRegistryName(), "inventory"));
    }

    /**
     * ■アイテム色の登録
     */
    @Override
    public void registerItemColor()
    {
        //■
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor()
        {
            @Override
            public int colorMultiplier(ItemStack stack, int layer)
            {
                return 0x000000;
            }
        }, new Item[] { DBItems.MERIDAMA });
    }

    /**
     * ■レンダラーの登録
     */
    @Override
    public void registerRenderer()
    {
        //■Dawnbreaker
        RenderingRegistry.registerEntityRenderingHandler(EntityDawnbreaker.class, new RenderDawnbreaker.Factory());
        //■爆発
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicExplode.class, new RenderMagicExplode.Factory());
    }

    @Override
    public void registerLeyer()
    {
//        Class[] clazzes = {EntityZombie.class, EntitySkeleton.class};

        Set<Class<? extends Entity>> keys = getRenderMgr().entityRenderMap.keySet();

        for (Class clazz : keys)
        {
            try
            {
                Class c = clazz.asSubclass(EntityCreature.class);
            }
            catch (ClassCastException e)
            {
                //■クラス階層が赤の他人だった
//                System.out.println(clazz.getName());
                continue;
            }

            Render render = getRenderMgr().entityRenderMap.get(clazz);
            if (render instanceof RenderLivingBase)
            {
                RenderLivingBase renderLiving = (RenderLivingBase)render;
                if (renderLiving.getMainModel() == null) { continue; }
                ModelBase modelUndead;

                try
                {
                    modelUndead = renderLiving.getMainModel().getClass().newInstance();
                }
                catch (InstantiationException | IllegalAccessException e)
                {
//                    throw new RuntimeException(e);
                    //インスタンスの生成に失敗。次、次。
                    continue;
                }

                LayerRenderer layer = new LayerUndeadCharge(renderLiving, modelUndead);
                renderLiving.addLayer(layer);
            }
        }
    }

    @Override
    public EntityPlayer getPlayer() { return FMLClientHandler.instance().getClient().player; }

    public static RenderManager getRenderMgr() { return FMLClientHandler.instance().getClient().getRenderManager(); }

    public static ItemMeshDefinition createMeshDefinition(final ResourceLocation[] resourcesIn)
    {
        return  new ItemMeshDefinition()
                    {
                        public ModelResourceLocation getModelLocation(ItemStack stackIn)
                        {
                            int lvl = 0;
//                            if (stackIn.getItem() instanceof ItemEX)
//                            {
//                                ItemEX itemEx = (ItemEX)stackIn.getItem();
//                                lvl = itemEx.getEXInfoFromExp(stackIn).level * 2 - 2;
//                            }

                            ResourceLocation resource = resourcesIn[lvl];
                            return new ModelResourceLocation(resource, "inventory");
                        }
                    };
    }
}
