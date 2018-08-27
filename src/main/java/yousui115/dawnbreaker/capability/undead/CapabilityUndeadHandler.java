package yousui115.dawnbreaker.capability.undead;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yousui115.dawnbreaker.Dawnbreaker;

public class CapabilityUndeadHandler
{
    @CapabilityInject(IUndeadHandler.class)
    public static Capability<IUndeadHandler> UNDEAD_HANDLER_CAPABILITY = null;

    public final static ResourceLocation KYE = new ResourceLocation(Dawnbreaker.MOD_ID, "undeaddata");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IUndeadHandler.class, new Capability.IStorage<IUndeadHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IUndeadHandler> capability, IUndeadHandler instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();

                System.out.println("writeNBT");

                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IUndeadHandler> capability, IUndeadHandler instance, EnumFacing side, NBTBase base)
            {
                System.out.println("readNBT");
            }
        }, UndeadHandler::new);
    }

}
