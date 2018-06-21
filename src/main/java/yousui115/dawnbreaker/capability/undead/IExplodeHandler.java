package yousui115.dawnbreaker.capability.undead;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IExplodeHandler
{
    /**
     * ■ドーンブレイカーで斬られた
     */
    void setChanceExplode(boolean isChanceIn);
    boolean hasChanceExplode();

    /**
     * ■逃走時間（残り時間）
     */
    void setTickAvoid(int tickIn);
    int getTickAvoid();
    void setAvoid();
    void resetAvoid();

    void setTargetPlayer(Entity target);
    @SideOnly(Side.CLIENT)
    void setTargetPlayer(boolean hasTargetPlayer);
    boolean hasTargetPlayer();

    /**
     * ■
     * @return
     */
    boolean isDirty();
    void resetDirty();
}
