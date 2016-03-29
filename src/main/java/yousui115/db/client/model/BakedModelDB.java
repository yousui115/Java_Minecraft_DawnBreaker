package yousui115.db.client.model;

import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;

import yousui115.db.DB;

import com.google.common.base.Function;

/**
 * ■暗闇の中で剣を光らせたいが為に作られたクラス
 *
 */
@SideOnly(Side.CLIENT)
public class BakedModelDB implements IPerspectiveAwareModel
{
    private TextureAtlasSprite stone;

    public IBakedModel model;

    public BakedModelDB(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        stone = bakedTextureGetter.apply(new ResourceLocation("blocks/stone"));
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        return getModelDB().getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return getModelDB().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
//        GlStateManager.enableBlend();
//        GlStateManager.disableAlpha();
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220f, 220f);

        return getModelDB().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return getModelDB().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return getModelDB().getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return getModelDB().getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return getModelDB().getOverrides();
    }

    protected IPerspectiveAwareModel getModelDB()
    {
        IBakedModel model = DB.proxy.getRenderItem().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation(DB.MOD_ID + ":" + DB.nameDB + "L", "inventory"));
        return (IPerspectiveAwareModel)model;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
        return getModelDB().handlePerspective(cameraTransformType);
    }
}
