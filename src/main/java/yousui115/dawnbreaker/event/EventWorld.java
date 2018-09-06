package yousui115.dawnbreaker.event;

import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.capability.world.CapWorldHandler;
import yousui115.dawnbreaker.capability.world.WorldHandler;

public class EventWorld
{
    @SubscribeEvent
    public void worldCapWorld(AttachCapabilitiesEvent<World> event)
    {
        event.addCapability(CapWorldHandler.KYE, new WorldHandler());
    }
}
