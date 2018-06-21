package yousui115.dawnbreaker.capability.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class FaithHandler implements IFaithHandler, ICapabilitySerializable<NBTTagCompound>
{
    //TODO 下記メンバを保持するクラスを一つ作るべきか (FaithDataやらなんやら)
    private int countUndeadKill;

    private int countRepRairDB;

    private boolean isDirty;

    /**
     * ■コンストラクタ
     */
    public FaithHandler()
    {
        countUndeadKill = 0;
        countRepRairDB = 0;

        isDirty = false;
    }

    @Override
    public int getUndeadKillCount()
    {
        return countUndeadKill;
    }

    @Override
    public void addUndeadKillCount()
    {
        countUndeadKill++;
        countUndeadKill = MathHelper.clamp(countUndeadKill, 0, Integer.MAX_VALUE - 2);

        isDirty = true;
    }

    //■このメソッドはMessageFaithHandlerでのみ使用する！
    @Override
    public void setUndeadKillCount(int countIn)
    {
        countUndeadKill = MathHelper.clamp(countIn, 0, Integer.MAX_VALUE - 2);
    }

    @Override
    public int getRepairDBCount()
    {
        return countRepRairDB;
    }

    @Override
    public void addRepairDBCount()
    {
        countRepRairDB++;
        countRepRairDB = MathHelper.clamp(countRepRairDB, 0, Integer.MAX_VALUE - 2);

        isDirty = true;
    }

    //■このメソッドはMessageFaithHandlerでのみ使用する！
    @Override
    public void setRepairDBCount(int countIn)
    {
        countRepRairDB = MathHelper.clamp(countIn, 0, Integer.MAX_VALUE - 2);
    }

    /**
     * ■
     */
    public void copy(IFaithHandler faithIn)
    {
        countUndeadKill = faithIn.getUndeadKillCount();
        countRepRairDB = faithIn.getRepairDBCount();
    }


    public boolean isDirty() { return isDirty; }
    public void resetDirty() { isDirty = false; }

    //===============================================================

    @Override
    public NBTTagCompound serializeNBT()
    {
        //TODO 何故か2回連続で呼ばれる

        //↓こっちを使うのが正解っぽい？わかんなーい
        //NBTTagCompound nbt = (NBTTagCompound)CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY.writeNBT(this, null);

        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("UndeadKill", countUndeadKill);
        nbt.setInteger("RepairDB",   countRepRairDB);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        //↓こっちを使うのが正解っぽい？わかんなーい
        //CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY.readNBT(this, null, nbt);

        countUndeadKill = nbt.getInteger("UndeadKill");
        countRepRairDB   = nbt.getInteger("RepairDB");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY)
        {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY)
        {
            return (T) this;
        }

        return null;
    }
}
