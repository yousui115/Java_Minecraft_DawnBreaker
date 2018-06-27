package yousui115.dawnbreaker.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.dawnbreaker.Dawnbreaker;
import yousui115.dawnbreaker.capability.undead.CapabilityUndeadHandler;
import yousui115.dawnbreaker.capability.undead.IUndeadHandler;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.util.DBUtils;

@SideOnly(Side.CLIENT)
public class LayerUndeadCharge implements LayerRenderer<EntityCreature>
{
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private final RenderLivingBase renderUndead;
    private final ModelBase modelUndead;

    public LayerUndeadCharge(RenderLivingBase renderIn, ModelBase modelIn)
    {
        //■
        renderUndead = renderIn;

        //■
        modelUndead = modelIn;
    }

    /**
     * ■
     */
    @Override
    public void doRenderLayer(EntityCreature creatureIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (renderUndead == null || modelUndead == null) { return; }

        //■ドーンブレイカーを手に持ってる必要がある。
        if (DBUtils.isEnmptyStack(Dawnbreaker.proxy.getPlayer().getHeldItemMainhand()) == true ||
            Dawnbreaker.proxy.getPlayer().getHeldItemMainhand().getItem() instanceof ItemDawnbreaker == false)
        {
            return;
        }

        //■きゃぱびりてぃ
        if (creatureIn.hasCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY,null) == false) { return; }
        IUndeadHandler hdlUndead = creatureIn.getCapability(CapabilityUndeadHandler.UNDEAD_HANDLER_CAPABILITY, null);

        //■装い
        EnumUndeadLayerType layerType = EnumUndeadLayerType.NONE;
        if (hdlUndead.getTickAvoid() > 0)
        {
            //■青
            layerType = EnumUndeadLayerType.AVOID;
        }
        else if (hdlUndead.hasTargetPlayer() == true)
        {
            //■赤
            layerType = EnumUndeadLayerType.MELEE;
        }
        else
        {
            return;
        }


        // ▼
        this.renderUndead.bindTexture(LIGHTNING_TEXTURE);
        // ▼
        GlStateManager.matrixMode(5890);
        // ▼
        GlStateManager.loadIdentity();
        // ▼
        float f = (float)creatureIn.ticksExisted + partialTicks;
        // ▼
        GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
        // ▼
        GlStateManager.matrixMode(5888);
        // ▼
        GlStateManager.enableBlend();
        // ▼
        GlStateManager.color(layerType.r, layerType.g, layerType.b, layerType.a);
        // ▼
        GlStateManager.disableLighting();
        // ▼
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        GlStateManager.disableDepth();

        // ▼
        this.modelUndead.setModelAttributes(renderUndead.getMainModel());
        // ▼
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        // ▼
        this.modelUndead.render(creatureIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        // ▼
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);


        GlStateManager.enableDepth();
        // ▼
        GlStateManager.matrixMode(5890);
        // ▼
        GlStateManager.loadIdentity();
        // ▼
        GlStateManager.matrixMode(5888);
        // ▼
        GlStateManager.enableLighting();
        // ▼
        GlStateManager.disableBlend();
    }

    /**
     * ■
     */
    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }

    protected enum EnumUndeadLayerType
    {
        NONE(0.5f, 0.5f, 0.5f, 0.0f),
        AVOID(0.5f, 0.5f, 0.5f, 1.0f),
        MELEE(1.0f, 0.0f, 0.0f, 1.0f);

        public final float r;
        public final float g;
        public final float b;
        public final float a;

        private EnumUndeadLayerType(float rIn, float gIn, float bIn, float aIn)
        {
            r = rIn;
            g = gIn;
            b = bIn;
            a = aIn;
        }
    }
}