package yousui115.dawnbreaker.capability.player;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yousui115.dawnbreaker.Dawnbreaker;

public class CapabilityFaithHandler
{
    @CapabilityInject(IFaithHandler.class)
    public static Capability<IFaithHandler> FAITH_HANDLER_CAPABILITY = null;

    public final static ResourceLocation KYE = new ResourceLocation(Dawnbreaker.MOD_ID, "faithdata");


    public static void register()
    {
        CapabilityManager.INSTANCE.register(IFaithHandler.class, new Capability.IStorage<IFaithHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IFaithHandler> capability, IFaithHandler instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();

                System.out.println("writeNBT");

                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IFaithHandler> capability, IFaithHandler instance, EnumFacing side, NBTBase base)
            {
                System.out.println("readNBT");
            }
        }, FaithHandler::new);
    }

}
