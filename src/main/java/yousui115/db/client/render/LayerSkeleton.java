package yousui115.db.client.render;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import yousui115.db.Util_DB;

public class LayerSkeleton implements LayerRenderer<EntitySkeleton>
{
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final RenderSkeleton skeletonRenderer;
    private final ModelSkeleton skeletonModel = new ModelSkeleton(2.0F, true);

    public LayerSkeleton(RenderSkeleton skeletonRendererIn)
    {
        this.skeletonRenderer = skeletonRendererIn;
    }

    public void doRenderLayer(EntitySkeleton entitySkeleton, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (Util_DB.isAvoid(entitySkeleton))
        {
            boolean flag = entitySkeleton.isInvisible();
            GlStateManager.depthMask(!flag);
            this.skeletonRenderer.bindTexture(LIGHTNING_TEXTURE);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f = (float)entitySkeleton.ticksExisted + partialTicks;
            GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            float f1 = 0.5F;
            GlStateManager.color(f1, f1, f1, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(1, 1);
            this.skeletonModel.setModelAttributes(this.skeletonRenderer.getMainModel());
            this.skeletonModel.render(entitySkeleton, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(flag);
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}