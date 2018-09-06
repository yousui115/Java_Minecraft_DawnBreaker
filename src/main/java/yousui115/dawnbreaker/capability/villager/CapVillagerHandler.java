package yousui115.dawnbreaker.capability.villager;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yousui115.dawnbreaker.Dawnbreaker;

public class CapVillagerHandler
{
    @CapabilityInject(IVillagerHandler.class)
    public static Capability<IVillagerHandler> CAP_VILLAGE = null;

    public final static ResourceLocation KYE = new ResourceLocation(Dawnbreaker.MOD_ID, "villagedata");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IVillagerHandler.class, new Capability.IStorage<IVillagerHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IVillagerHandler> capability, IVillagerHandler instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();

                System.out.println("writeNBT");

                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IVillagerHandler> capability, IVillagerHandler instance, EnumFacing side, NBTBase base)
            {
                System.out.println("readNBT");
            }
        }, VillagerHandler::new);
    }
}
