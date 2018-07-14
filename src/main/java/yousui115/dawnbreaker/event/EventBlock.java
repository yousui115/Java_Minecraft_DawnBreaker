package yousui115.dawnbreaker.event;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.item.ItemDawnbreaker;
import yousui115.dawnbreaker.util.DBBlocks;

public class EventBlock
{
    @SubscribeEvent
    public void destroyweb(BlockEvent.BreakEvent eventIn)
    {
        if (eventIn.getPlayer() != null &&
            eventIn.getPlayer().getHeldItemMainhand().getItem() instanceof ItemDawnbreaker &&
            DBBlocks.DESTROY_WEB.replaceBlock(eventIn.getWorld(), eventIn.getPos(), eventIn.getState(), 0) == true)
        {
            //■
            DBBlocks.DESTROY_WEB.soundFlame(eventIn.getWorld(), eventIn.getPos(), 0);

            //■以降の処理を実行させない。
            eventIn.setCanceled(true);
        }
    }
}
