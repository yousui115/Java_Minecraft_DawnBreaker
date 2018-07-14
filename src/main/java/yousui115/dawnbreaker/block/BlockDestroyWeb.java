package yousui115.dawnbreaker.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.dawnbreaker.network.PacketHandler;
import yousui115.dawnbreaker.network.particle.MessageFlame;

public class BlockDestroyWeb extends Block
{
    //■ぷろぱてぃ
    public static final int MIN = 0;
    public static final int MAX = 9;
    public static final PropertyInteger GENERATION = PropertyInteger.create("generation", MIN, MAX);

    //■調査対象面
    public static final List<EnumFacing> FACES = Lists.newArrayList(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP, EnumFacing.DOWN);

    //■連続破壊対象マテリアル
    public static final List<Material> MATERIALS = Lists.newArrayList(Material.LEAVES, Material.WEB);

    /**
     * ■こんすとらくた
     * @param materialIn
     */
    public BlockDestroyWeb(Material blockMaterialIn, MapColor blockMapColorIn)
    {
        super(blockMaterialIn, blockMapColorIn);
    }

    /**
     * ■チック処理
     */
    @Override
    public void updateTick(World worldIn, BlockPos posIn, IBlockState stateIn, Random randIn)
    {
        //■
        int generation = (Integer)stateIn.getProperties().get(GENERATION) + 1;

        if (generation <= MAX)
        {
            boolean suc = false;

            for (EnumFacing face : FACES)
            {
                //■対象ブロックの位置
                BlockPos targetPos = posIn.offset(face, 1);
                //■対象ブロックのState
                IBlockState targetState = worldIn.getBlockState(targetPos);

                suc |= replaceBlock(worldIn, targetPos, targetState, generation);
            }

            if (suc == true)
            {
                soundFlame(worldIn, posIn, generation);
            }
        }

        //■最後は自分を処分
        worldIn.setBlockToAir(posIn);
    }

    /**
     * ■
     */
    public boolean replaceBlock(World worldIn, BlockPos targetPosIn, IBlockState targetStateIn, int generation)
    {
        //■世代チェック + 対象ブロックの選定
        if (MathHelper.clamp(generation, MIN, MAX) == generation &&
            targetStateIn == null ||
            MATERIALS.contains(targetStateIn.getMaterial()) == false) { return false; }

        //■世代ブロック
        IBlockState nextState = this.getDefaultState().withProperty(GENERATION, generation);

        //■設置
        worldIn.setBlockState(targetPosIn, nextState);

        //■予約
        worldIn.updateBlockTick(targetPosIn, this, this.delay(), -1);

        //■パーティクル
        createFlame(worldIn, targetPosIn, generation);

        return true;
    }

    /**
     *
     * @param targetPosIn
     */
    protected void createFlame(World worldIn, BlockPos targetPosIn, int generation)
    {
        if (worldIn.isRemote == false)
        {
            PacketHandler.INSTANCE.sendToAll(new MessageFlame(targetPosIn, generation));
        }
    }

    /**
     * ■
     */
    public void soundFlame(World worldIn, BlockPos targetPosIn, int generation)
    {
        if (generation % 3 == 0)
        {
            worldIn.playSound((EntityPlayer)null, targetPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 0.3F, 1.0F);
        }
    }

    /**
     * ■
     */
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (worldIn.isRemote == false && entityIn instanceof EntityLivingBase)
        {
            entityIn.setFire(4);
        }
    }

    /**
     * ■
     */
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {GENERATION});
    }

    /**
     * ■
     */
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    /**
     * ■
     */
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    /**
     * ■
     */
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }


//    @Override
//    @SideOnly (Side.CLIENT)
//    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
//    {
//        return new Vec3d(1.0f, 0f, 0f);
//    }

    /**
     * ■
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }


    //==================================================================================


    public int delay() { return 3; }


    public static enum EnumDestroy implements IStringSerializable
    {
        LEAVES("leaves"),
        WEB("web");

        private final String name;

        /**
         * ■こんすとらくた
         * @param nameIn
         */
        private EnumDestroy(String nameIn)
        {
            name = nameIn;
        }

        @Override
        public String getName() { return name; }
    }
}


