package yousui115.dawnbreaker.event;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.util.DBBlocks;
import yousui115.dawnbreaker.util.DBUtils;

public class EventBlock
{
    @SubscribeEvent
    public void destroyweb(BlockEvent.BreakEvent eventIn)
    {
        //■Dawnbreakerで特定のブロックを破壊すると連鎖する
        if (eventIn.getPlayer() != null &&
            DBUtils.isDBwithBoD(eventIn.getPlayer().getHeldItemMainhand()) == true &&
            DBBlocks.DESTROY_WEB.replaceBlock(eventIn.getWorld(), eventIn.getPos(), eventIn.getState(), 0) == true)
        {
            //■
            DBBlocks.DESTROY_WEB.soundFlame(eventIn.getWorld(), eventIn.getPos(), 0);

            //■以降の処理を実行させない。
            eventIn.setCanceled(true);
        }
    }
}
