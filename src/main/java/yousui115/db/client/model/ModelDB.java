package yousui115.db.client.model;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.db.DB;

import com.google.common.base.Function;

@SideOnly(Side.CLIENT)
public class ModelDB implements IModel
{

    @Override
    public Collection<ResourceLocation> getDependencies() { return Collections.emptyList(); }
    @Override
    public Collection<ResourceLocation> getTextures() { return Collections.emptyList(); }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return new BakedModelDB(bakedTextureGetter, (ModelResourceLocation)DB.proxy.modelRL_DBL);
    }

    @Override
    public IModelState getDefaultState() { return ModelRotation.X0_Y0; }
}
