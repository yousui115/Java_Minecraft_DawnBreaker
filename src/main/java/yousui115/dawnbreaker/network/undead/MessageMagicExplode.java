package yousui115.dawnbreaker.network.undead;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yousui115.dawnbreaker.entity.EntityMagicExplode;

public class MessageMagicExplode  implements IMessage
{
    private int entityID;
    private int triggerID;

    /**
     * ■コンストラクタ(必須！)
     */
    public MessageMagicExplode(){}

    /**
     * ■コンストラクタ
     * @param magic
     */
    public MessageMagicExplode(EntityMagicExplode explodeIn)
    {
        this.entityID = explodeIn.getEntityId();
        this.triggerID = explodeIn.getTriggerID();
    }
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.triggerID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        buf.writeInt(triggerID);
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID() { return this.entityID; }
    @SideOnly(Side.CLIENT)
    public int getTriggerID() { return this.triggerID; }

}
