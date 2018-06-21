package yousui115.dawnbreaker.capability.undead;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExplodeHandler implements IExplodeHandler, ICapabilitySerializable<NBTTagCompound>
{
    private boolean isChanceExplode;

    private int tickAvoid;
    private static final int TICKMAX_AVOID = 200;

    private boolean hasTargetPlayer;

    private boolean isDirty;

    public ExplodeHandler()
    {
        isChanceExplode = false;
        tickAvoid = 0;

        isDirty = false;
    }

    @Override
    public void setChanceExplode(boolean isChanceIn)
    {
        isChanceExplode = isChanceIn;
    }

    @Override
    public boolean hasChanceExplode() { return isChanceExplode; }

    @Override
    public void setTickAvoid(int tickIn)
    {
        tickAvoid = MathHelper.clamp(tickIn, 0, TICKMAX_AVOID);
    }

    @Override
    public int getTickAvoid() { return tickAvoid; }

    @Override
    public void setAvoid()
    {
        tickAvoid = TICKMAX_AVOID;
        isDirty = true;
    }

    @Override
    public void resetAvoid()
    {
        tickAvoid = 0;
        isDirty = true;
    }


    @Override
    public void setTargetPlayer(Entity target)
    {
        if (hasTargetPlayer != target instanceof EntityPlayer)
        {
            hasTargetPlayer = target instanceof EntityPlayer;
            isDirty = true;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setTargetPlayer(boolean hasTargetPlayerIn)
    {
        hasTargetPlayer = hasTargetPlayerIn;
    }

    @Override
    public boolean hasTargetPlayer()
    {
        return hasTargetPlayer;
    }

    @Override
    public boolean isDirty() { return isDirty; }
    @Override
    public void resetDirty() { isDirty = false; }

    //==========================================================

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setBoolean("ExplodeChance", isChanceExplode);
        nbt.setInteger("TickAvoid",   tickAvoid);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        isChanceExplode = nbt.getBoolean("ExplodeChance");
        tickAvoid   = nbt.getInteger("TickAvoid");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY)
        {
            return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityExplodeHandler.EXPLODE_HANDLER_CAPABILITY)
        {
            return (T) this;
        }
        return null;
    }
}
