package yousui115.db.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import yousui115.db.DB;
import yousui115.db.entity.EntityDBExplode;

public class RenderDBExplode extends Render
{
  //■りそーすろけーしょん
    protected static final ResourceLocation resource = new ResourceLocation(DB.MOD_ID, "textures/entity/magic.png");

    //■てせれーたー
    protected static Tessellator tessellator = Tessellator.getInstance();

    //■わーるどれんだらー
    protected static WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    private static double[][] dVec = {  {-1f, 1f, 1f},        //0
                                        {-1f,-1f, 1f},        //1
                                        { 1f,-1f, 1f},        //2
                                        { 1f, 1f, 1f},        //3
                                        {-1f, 1f,-1f},        //4
                                        {-1f,-1f,-1f},        //5
                                        { 1f,-1f,-1f},        //6
                                        { 1f, 1f,-1f}};       //7

    private static int[][] nVecPos = {  {0, 1, 2, 3},
                                        {3, 2, 6, 7},
                                        {0, 3, 7, 4},
                                        {1, 0, 4, 5},
                                        {2, 1, 5, 6},
                                        {4, 7, 6, 5}};

    public RenderDBExplode(RenderManager renderManager)
    {
        super(renderManager);
    }

    /**
     * ■描画更新処理
     */
    @Override
    public void doRender(Entity entity, double dX, double dY, double dZ, float f, float f1)
    {
        //■描画の前処理
        EntityDBExplode entityMagic = this.preDraw(entity);
        if (entityMagic == null) { return; }

        //■爆発エフェクト拡大率の算出
        float fSizeOfst = (float)entityMagic.ticksExisted;
        float fSizeOfstMax = (float)entityMagic.getTickMax();
        float fSize = (10f / fSizeOfstMax) * fSizeOfst;

        //■座標系の調整
        // ▼行列のコピー
        GlStateManager.pushMatrix();

        //■回転、位置の調整(FILOなので注意)
        // ▼3.位置
        GlStateManager.translate(dX, dY, dZ);
        // ▼2.回転(Y軸)
        GlStateManager.rotate(entity.rotationYaw, 0f, 1f, 0f);
        // ▼1.拡大率
        GlStateManager.scale(fSize, fSize, fSize);

        //■描画モード
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        //■拡大率
        for(int nScale = 0; nScale < 4; nScale++)
        {
            double dScale = (0.2d * (double)nScale) + 1.0d;
            for(int idx = 0; idx < nVecPos.length; idx++)
            {
                worldrenderer.pos(dVec[nVecPos[idx][0]][0] * dScale, dVec[nVecPos[idx][0]][1] * dScale, dVec[nVecPos[idx][0]][2] * dScale).tex(0d, 0d).normal(0f, 1f, 0f).endVertex();
                worldrenderer.pos(dVec[nVecPos[idx][1]][0] * dScale, dVec[nVecPos[idx][1]][1] * dScale, dVec[nVecPos[idx][1]][2] * dScale).tex(0d, 1d).normal(0f, 1f, 0f).endVertex();
                worldrenderer.pos(dVec[nVecPos[idx][2]][0] * dScale, dVec[nVecPos[idx][2]][1] * dScale, dVec[nVecPos[idx][2]][2] * dScale).tex(1d, 1d).normal(0f, 1f, 0f).endVertex();
                worldrenderer.pos(dVec[nVecPos[idx][3]][0] * dScale, dVec[nVecPos[idx][3]][1] * dScale, dVec[nVecPos[idx][3]][2] * dScale).tex(1d, 0d).normal(0f, 1f, 0f).endVertex();
            }
        }

        //■描画
        tessellator.draw();

        //■座標系の後始末
        // ▼行列の削除
        GlStateManager.popMatrix();

        //■描画の後始末
        this.postDraw();
    }


    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.resource;
    }

    /* ======================================== イカ、自作 =====================================*/

    /**
     * ■描画 前処理<br>
     *   1.エンティティチェック<br>
     *   2.画像のバインド<br>
     *   3.GlStateManagerの内部設定<br>
     *   4.頂点カラーの設定<br>
     * @param entityIn
     */
    protected EntityDBExplode preDraw(Entity entityIn)
    {
        //■エンティティチェック
        if (!(entityIn instanceof EntityDBExplode)) { return null; }
        EntityDBExplode entityMagic = (EntityDBExplode)entityIn;

//        //■色の取得
//        EntityExplode.EnumColorType colorType = entityMagic.getColorType();

        //■描画準備
        // ▼画像のバインド
        this.bindEntityTexture(entityIn);

        // ▼テクスチャの貼り付け ON
        GlStateManager.enableTexture2D();

        // ▼ライティング OFF
        //GlStateManager.enableLighting();
        GlStateManager.disableLighting();

        // ▼陰影処理の設定(なめらか)
        //GlStateManager.shadeModel(GL11.GL_SMOOTH);

        // ▼ブレンドモード ON
        GlStateManager.enableBlend();
        // ▼加算+アルファ
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        //GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
        //GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        // ▼アルファ
        GlStateManager.disableAlpha();

        // ▼指定のテクスチャユニットとBrightnessX,Y
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 150f, 150f);

        // ▼法線の再スケーリング(?) ON
        GlStateManager.enableRescaleNormal();

        // ▼頂点カラー
        //GlStateManager.color(colorType.R, colorType.G, colorType.B, colorType.A);
        GlStateManager.color(0.4f, 0.4f, 1.0f, 0.6f);

        // ▼カリング(指定面を表示しない)
        GlStateManager.disableCull();
//        GlStateManager.cullFace(GL11.GL_NONE);
//        GlStateManager.cullFace(GL11.GL_FRONT_AND_BACK);

        return entityMagic;
    }

    /**
     * ■描画 後処理
     */
    protected void postDraw()
    {
        //■描画後始末
        //  注意:設定した全てを逆に設定し直すのはNG
        //       disableTexture2D()なんてしたら描画がえらい事に！
        // ▼法線の再スケーリング(?) OFF
        GlStateManager.disableRescaleNormal();

        // ▼指定のテクスチャユニットとBrightnessX,Y(値を上げれば明るく見える！)
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0f, 0f);

        // ▼アルファ
        GlStateManager.enableAlpha();

        // ▼ブレンドモード OFF
        GlStateManager.disableBlend();

        // ▼陰影処理の設定(フラット:一面同じ色)
        //GlStateManager.shadeModel(GL11.GL_FLAT);

        // ▼ライティング ON
        GlStateManager.enableLighting();
        //GlStateManager.disableLighting();

        GlStateManager.enableCull();


    }
}
