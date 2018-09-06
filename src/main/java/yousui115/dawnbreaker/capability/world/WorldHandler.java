package yousui115.dawnbreaker.capability.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class WorldHandler implements IWorldHandler, ICapabilitySerializable<NBTTagCompound>
{

    //■世界中から集められた信仰心
    //  (0 <-> MAX_VALUE)
    private int numWorldFaith;
    private final int numWorldFaithMax = Integer.MAX_VALUE;
    private final int numWorldFaithDispMax = 1000;


    /**
     * ■コンストラクタ
     */
    public WorldHandler()
    {
        numWorldFaith = 0;
    }

    /**
     * ■
     */
    @Override
    public void addNumWorldFaith(int numIn)
    {
        //■加算する場合は、桁あふれ対策が必要。
        //  逆に、MIN_VALUEが来ても、numWorldFaithの最小値は常に0なので、桁あふれする事はないよ。
        if (numIn > 0)
        {
            //■最大値までの差分
            int capa = numWorldFaithMax - numWorldFaith;

            //capa(差分)の方が多ければ、numInで桁あふれが起きないので、numInを加算するよ。
            //逆に、numInの方が多ければ、そのまま入れると桁あふれするので、capaを加算するよ。
            numIn = capa > numIn ? numIn : capa;
        }

        setNumWorldFaith(numWorldFaith + numIn);
    }

    @Override
    public void setNumWorldFaith(int numIn)
    {
        numWorldFaith = MathHelper.clamp(numIn, 0,  numWorldFaithMax);
    }
    @Override
    public int getNumWorldFaith()
    {
        return numWorldFaith;
    }

    @Override
    public int getNumWorldFaithDisp() { return numWorldFaith < numWorldFaithDispMax ? numWorldFaith : numWorldFaithDispMax; }

    @Override
    public int getNumWorldFaithDispMax() { return numWorldFaithDispMax; }

    //=================================================================

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("WorldFaith", getNumWorldFaith());

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        setNumWorldFaith(nbt.getInteger("WorldFaith"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapWorldHandler.WORLD_HANDLER_CAPABILITY)
        {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapWorldHandler.WORLD_HANDLER_CAPABILITY)
        {
            return (T) this;
        }

        return null;
    }


}
