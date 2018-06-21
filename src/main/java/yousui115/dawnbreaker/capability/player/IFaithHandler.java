package yousui115.dawnbreaker.capability.player;

public interface IFaithHandler
{
    /**
     * ■アンデッド浄化数 関連
     */
    int getUndeadKillCount();

    void addUndeadKillCount();

    void setUndeadKillCount(int countIn);

    /**
     * ■DB修理回数 関連
     */
    int getRepairDBCount();

    void addRepairDBCount();

    void setRepairDBCount(int countIn);

    //TODO
    // 洗脳した村の数

    /**
     * ■コピー
     */
    void copy(IFaithHandler faithIn);
}
