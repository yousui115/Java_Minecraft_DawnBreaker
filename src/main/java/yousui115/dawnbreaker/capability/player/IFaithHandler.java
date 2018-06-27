package yousui115.dawnbreaker.capability.player;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFaithHandler
{
    /**
     * ■アンデッド浄化数 関連
     */
    int getUndeadKillCount();
    int getUndeadKillCount_hide();

    void addUndeadKillCount();

    @SideOnly(Side.CLIENT)
    void setUndeadKillCount(int countIn);
    @SideOnly(Side.CLIENT)
    void setUndeadKillCount_hide(int hideIn);

    default int getUndeadKillCount_Max() { return 2000; }
    default int getCountNext() { return getUndeadKillCount() < 10 ? 10 - getUndeadKillCount() : 100 - getUndeadKillCount_hide(); }
    /**
     * ■DB修理回数 関連
     */
    int getRepairDBCount();

    void addRepairDBCount();

    void setRepairDBCount(int countIn);

    default int getRepairDBCount_Max() { return 20; }

    //TODO
    // 洗脳した村の数

    /**
     * ■コピー
     */
    void copy(IFaithHandler faithIn);
}
