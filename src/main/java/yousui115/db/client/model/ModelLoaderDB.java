package yousui115.db.client.model;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import yousui115.db.DB;

public class ModelLoaderDB implements ICustomModelLoader
{

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        if (modelLocation.getResourceDomain().equals(DB.MOD_ID.toLowerCase()))
        {
            return modelLocation.getResourcePath().equals("models/item/" + DB.nameDB);
        }

        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception
    {
        return new ModelDB();
    }

}
