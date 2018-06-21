package yousui115.dawnbreaker.capability.undead;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yousui115.dawnbreaker.Dawnbreaker;

public class CapabilityExplodeHandler
{
    @CapabilityInject(IExplodeHandler.class)
    public static Capability<IExplodeHandler> EXPLODE_HANDLER_CAPABILITY = null;

    public final static ResourceLocation KYE = new ResourceLocation(Dawnbreaker.MOD_ID, "ExplodeData");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IExplodeHandler.class, new Capability.IStorage<IExplodeHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IExplodeHandler> capability, IExplodeHandler instance, EnumFacing side)
            {
                NBTTagList nbtTagList = new NBTTagList();

                System.out.println("writeNBT");

                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IExplodeHandler> capability, IExplodeHandler instance, EnumFacing side, NBTBase base)
            {
                System.out.println("readNBT");
            }
        }, ExplodeHandler::new);
    }

}
