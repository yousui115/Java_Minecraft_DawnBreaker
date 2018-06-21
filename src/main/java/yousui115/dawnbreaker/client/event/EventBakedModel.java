package yousui115.dawnbreaker.client.event;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yousui115.dawnbreaker.client.model.BakedModelDawnbreaker;
import yousui115.dawnbreaker.util.DBItems;

public class EventBakedModel
{
    @SubscribeEvent
    public void onBake(ModelBakeEvent event)
    {
        ModelResourceLocation mrl = new ModelResourceLocation(DBItems.RL_DAWNBREAKER, "inventory");

        IBakedModel model = event.getModelRegistry().getObject(mrl);

        if (model != null && model instanceof BakedItemModel)
        {
            //■ごりおし も ごりおし
            BakedItemModel bakedModel = (BakedItemModel)model;

            event.getModelRegistry().putObject(mrl, new BakedModelDawnbreaker(bakedModel));
        }
    }
}
