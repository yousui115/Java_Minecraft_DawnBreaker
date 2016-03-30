package yousui115.db.common;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CommonProxy
{
    public ResourceLocation modelRL_DB;
    public ResourceLocation modelRL_DBL;

    public void registerRenders(){}
    public void registerModels(){}
    public EntityPlayer getPlayer(){ return null; }
    public RenderManager getRenderManager(){ return null; }
    public RenderItem getRenderItem(){ return null; }
}
