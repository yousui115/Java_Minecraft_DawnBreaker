package yousui115.dawnbreaker.capability.undead;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IUndeadHandler
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

    void setHasTargetPlayer(Entity target);
    @SideOnly(Side.CLIENT)
    void setHasTargetPlayer(boolean hasTargetPlayer);
    boolean hasTargetPlayer();

    /**
     * ■
     * @return
     */
    boolean isDirty();
    void resetDirty();
}
