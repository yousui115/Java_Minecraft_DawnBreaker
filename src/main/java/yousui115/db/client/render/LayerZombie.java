package yousui115.db.client.render;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import yousui115.db.Util_DB;

public class LayerZombie implements LayerRenderer<EntityZombie>
{
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final RenderZombie zombieRenderer;
    private final ModelZombie zombieModel = new ModelZombie(2.0F, true);

    public LayerZombie(RenderZombie zombieRendererIn)
    {
        this.zombieRenderer = zombieRendererIn;
    }

    public void doRenderLayer(EntityZombie entityZombie, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (Util_DB.isAvoid(entityZombie))
        {
            boolean flag = entityZombie.isInvisible();
            GlStateManager.depthMask(!flag);
            this.zombieRenderer.bindTexture(LIGHTNING_TEXTURE);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f = (float)entityZombie.ticksExisted + partialTicks;
            GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            float f1 = 0.5F;
            GlStateManager.color(f1, f1, f1, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(1, 1);
            this.zombieModel.setModelAttributes(this.zombieRenderer.getMainModel());
            this.zombieModel.render(entityZombie, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
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