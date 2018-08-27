package yousui115.dawnbreaker.capability.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FaithHandler implements IFaithHandler, ICapabilitySerializable<NBTTagCompound>
{
    //TODO 下記メンバを保持するクラスを一つ作るべきか (FaithDataやらなんやら)
    private int countUndeadKill;        //上位桁
    private int countUndeadKill_hide;   //下一桁

    private int countRepairDB;

    private boolean isDirty;

    /**
     * ■コンストラクタ
     */
    public FaithHandler()
    {
        countUndeadKill = 0;
        countUndeadKill_hide = 0;
        countRepairDB = 0;

        isDirty = false;
    }

    @Override
    public int getUndeadKillCount()
    {
        return countUndeadKill;
    }
    @Override
    public int getUndeadKillCount_hide()
    {
        return countUndeadKill_hide;
    }

    @Override
    public void addUndeadKillCount()
    {
        countUndeadKill++;
        countUndeadKill_hide++;

        //■「初回10体討伐」or「100討伐」
        if ((countUndeadKill == 10 && countUndeadKill_hide == 10) || countUndeadKill_hide >= 100)
        {
            countUndeadKill_hide = 0;
        }

        countUndeadKill = MathHelper.clamp(countUndeadKill, 0, getUndeadKillCount_Max());

        isDirty = true;
    }

    //■このメソッドはMessageFaithHandlerでのみ使用する！
    @SideOnly(Side.CLIENT)
    @Override
    public void setUndeadKillCount(int countIn)
    {
        countUndeadKill = MathHelper.clamp(countIn, 0, getUndeadKillCount_Max());
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void setUndeadKillCount_hide(int hideIn)
    {
        countUndeadKill_hide = MathHelper.clamp(hideIn, 0, 99);

    }
    @Override
    public int getRepairDBCount()
    {
        return countRepairDB;
    }

    @Override
    public void addRepairDBCount()
    {
        countRepairDB++;
        countRepairDB = MathHelper.clamp(countRepairDB, 0, getRepairDBCount_Max());

        isDirty = true;
    }

    //■このメソッドはMessageFaithHandlerでのみ使用する！
    @Override
    public void setRepairDBCount(int countIn)
    {
        countRepairDB = MathHelper.clamp(countIn, 0, getRepairDBCount_Max());
    }

    /**
     * ■
     */
    public void copy(IFaithHandler faithIn)
    {
        countUndeadKill = faithIn.getUndeadKillCount();
        countUndeadKill_hide = faithIn.getUndeadKillCount_hide();
        countRepairDB = faithIn.getRepairDBCount();
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
        nbt.setInteger("UndeadKill_hide", countUndeadKill_hide);
        nbt.setInteger("RepairDB",   countRepairDB);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        //↓こっちを使うのが正解っぽい？わかんなーい
        //CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY.readNBT(this, null, nbt);

        countUndeadKill = nbt.getInteger("UndeadKill");
        countUndeadKill_hide = nbt.getInteger("UndeadKill_hide");
        countRepairDB   = nbt.getInteger("RepairDB");
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
