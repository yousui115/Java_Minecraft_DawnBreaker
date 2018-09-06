package yousui115.dawnbreaker.capability.villager;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class VillagerHandler implements IVillagerHandler, ICapabilitySerializable<NBTTagCompound>
{
    private int faith;
    private int faithMax = 10;

    /**
     * ■コンストラクタ
     */
    public VillagerHandler()
    {
        faith = 0;
    }

    /**
     * ■
     */
    @Override
    public int addFaith(int faithIn)
    {
        if (faithIn > 0)
        {
            //■最大値までの差分
            int capa = faithMax - faith;

            faithIn = capa > faithIn ? faithIn : capa;
        }

        int faithOld = faith;
        setFaith(faith + faithIn);

        //■増減量を返す
        return faith - faithOld;
    }

    @Override
    public void setFaith(int faithIn)
    {
        faith = MathHelper.clamp(faithIn, 0,  faithMax);
    }
    @Override
    public int getFaith()
    {
        return faith;
    }

    //===============================================================

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("Faith", getFaith());

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        setFaith(nbt.getInteger("Faith"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapVillagerHandler.CAP_VILLAGE)
        {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapVillagerHandler.CAP_VILLAGE)
        {
            return (T) this;
        }

        return null;
    }
}
