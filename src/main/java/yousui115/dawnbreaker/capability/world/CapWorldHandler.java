package yousui115.dawnbreaker.capability.world;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yousui115.dawnbreaker.Dawnbreaker;

public class CapWorldHandler
{
    @CapabilityInject(IWorldHandler.class)
    public static Capability<IWorldHandler> WORLD_HANDLER_CAPABILITY = null;

    public final static ResourceLocation KYE = new ResourceLocation(Dawnbreaker.MOD_ID, "worlddata");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IWorldHandler.class, new Capability.IStorage<IWorldHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IWorldHandler> capability, IWorldHandler instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();

                System.out.println("writeNBT");

                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IWorldHandler> capability, IWorldHandler instance, EnumFacing side, NBTBase base)
            {
                System.out.println("readNBT");
            }
        }, WorldHandler::new);
    }
}
