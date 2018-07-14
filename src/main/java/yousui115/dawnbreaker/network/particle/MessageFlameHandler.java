package yousui115.dawnbreaker.network.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageFlameHandler implements IMessageHandler<MessageFlame, IMessage>
{

    /**
     * ■Server -> Client
     */
    @Override
    public IMessage onMessage(MessageFlame message, MessageContext ctx)
    {
        World world = Minecraft.getMinecraft().world;
        BlockPos blockpos = message.getBlockPos();

        double d3 = (double)((float)blockpos.getX() + world.rand.nextFloat());
        double d4 = (double)((float)blockpos.getY() + world.rand.nextFloat());
        double d5 = (double)((float)blockpos.getZ() + world.rand.nextFloat());

        world.spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

        return null;
    }

}
