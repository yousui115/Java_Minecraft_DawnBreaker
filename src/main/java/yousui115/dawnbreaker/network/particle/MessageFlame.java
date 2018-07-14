package yousui115.dawnbreaker.network.particle;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageFlame implements IMessage
{
    private BlockPos pos;
    private int generation;

    public MessageFlame(){}
    public MessageFlame(BlockPos posIn, int generationIn)
    {
        pos = new BlockPos(posIn);
        generation = generationIn;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        generation = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(generation);
    }

    public BlockPos getBlockPos() { return pos; }
    public int getGeneration() { return generation; }
}
