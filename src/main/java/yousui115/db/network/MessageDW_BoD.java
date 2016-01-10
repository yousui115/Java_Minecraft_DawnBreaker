package yousui115.db.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDW_BoD implements IMessage
{
    private int entityID;       //自分自身のEntityID
    private int idDW;
    private int value;

    /**
     * ■コンストラクタ(必須！)
     */
    public MessageDW_BoD(){}

    /**
     * ■コンストラクタ
     * @param magic
     */
    public MessageDW_BoD(Entity targetIn, int idIn, int valueIn)
    {
        this.entityID = targetIn.getEntityId();
        this.idDW = idIn;
        this.value = valueIn;
    }

    /**
     * ■IMessageのメソッド。ByteBufからデータを読み取る。
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.idDW = buf.readInt();
        this.value = buf.readInt();
    }

    /**
     * ■IMessageのメソッド。ByteBufにデータを書き込む。
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityID);
        buf.writeInt(this.idDW);
        buf.writeInt(this.value);
    }

    @SideOnly(Side.CLIENT)
    public int getEntityID() { return this.entityID; }
    @SideOnly(Side.CLIENT)
    public int getID_DW() { return this.idDW; }
    @SideOnly(Side.CLIENT)
    public int getValue() { return this.value; }
}
