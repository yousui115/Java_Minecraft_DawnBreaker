package yousui115.dawnbreaker.event;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.capability.player.CapabilityFaithHandler;
import yousui115.dawnbreaker.capability.player.IFaithHandler;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.util.DBItems;

public class EventAnvil
{
    /**
     * ■金床コンテナ情報(入力スロット1or2(もしくは両方)にputして、outputはまだpickupしてない)
     * @param event
     */
    @SubscribeEvent
    public void onAnvilChange(AnvilUpdateEvent event)
    {
        //■left:DB(ダメージ有り)  +  right:meridama ならば処理する
        if (event.getLeft().getItem() instanceof ItemDawnbreaker &&
            event.getLeft().getItemDamage() != 0 &&
            event.getRight() != null &&
            event.getRight().getItem() == DBItems.MERIDAMA)
        {
            //■修理コストは常に1(メリディアの恩恵)
            event.setCost(1);
            event.setMaterialCost(1);

            //■修理量は最大値まで。(メリディアの恩恵)
            event.setOutput(event.getLeft().copy());
            event.getOutput().setItemDamage(0);
        }
    }

    /**
     * ■金床コンテナ情報(output をスロットから pickup した)
     * @param event
     */
    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event)
    {
        //■left:DB(ダメージ有り)  +  right:meridama ならば処理する
        if (event.getItemInput().getItem() instanceof ItemDawnbreaker &&
            event.getItemInput().getItemDamage() != 0 &&
            event.getIngredientInput() != null &&
            event.getIngredientInput().getItem() == DBItems.MERIDAMA)
        {
            if (event.getEntityPlayer() != null &&
                event.getEntityPlayer().hasCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null) == true)
            {
                //■修理回数カウントアップ
                IFaithHandler faith = event.getEntityPlayer().getCapability(CapabilityFaithHandler.FAITH_HANDLER_CAPABILITY, null);
                faith.addRepairDBCount();
            }
        }
    }

}
