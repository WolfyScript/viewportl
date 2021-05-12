package me.wolfyscript.utilities.api.nms.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import me.wolfyscript.utilities.api.nms.NetworkUtil;
import me.wolfyscript.utilities.api.nms.nbt.NBTCompound;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

/**
 * This Class acts as a Wrapper for the Minecraft ByteBuf to make it able to correctly encode ItemStacks and more.
 * This wrapper makes sure the data is correctly encoded and can be decoded on the client.<br>
 * <p>
 * It can be created via the {@link me.wolfyscript.utilities.api.nms.NetworkUtil}:<br>
 * {@link NetworkUtil#buffer()}<br>
 * {@link NetworkUtil#buffer(ByteBuf)}<br>
 * For example:
 * <pre>
 *     MCByteBuf buf = wolfyUtils.getNMSUtil().getNetworkUtil().buffer();
 *     buf.writeUtf("test");
 *     ...
 * </pre>
 */
public interface MCByteBuf {

    static int getVarIntSize(int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) == 0) {
                return j;
            }
        }
        return 5;
    }

    MCByteBuf writeByteArray(byte[] byteArray);

    byte[] readByteArray();

    byte[] readByteArray(int i);

    MCByteBuf writeVarIntArray(int[] intArray);

    int[] readVarIntArray();

    int[] readVarIntArray(int i);

    MCByteBuf writeLongArray(long[] longArray);

    //BlockPosition e();

    //FriendlyByteBuf a(BlockPosition blockposition);

    //IChatBaseComponent h();

    //FriendlyByteBuf a(IChatBaseComponent ichatbasecomponent);

    <T extends Enum<T>> T readEnum(Class<T> enumType);

    MCByteBuf writeEnum(Enum<?> anEnum);

    int readVarInt();

    long readVarLong();

    MCByteBuf writeUUID(UUID uuid);

    UUID readUUID();

    MCByteBuf writeVarInt(int i);

    MCByteBuf writeVarLong(long i);

    MCByteBuf writeNBT(@Nullable NBTCompound nbttagcompound);

    @Nullable
    NBTCompound readNBT();

    @Nullable
    NBTCompound readAnySizeNBT();

    //@Nullable
    //NBTCompound a(NBTReadLimiter nbtreadlimiter);

    MCByteBuf writeItemStack(ItemStack itemstack);

    ItemStack readItemStack();

    String readUtf(int i);

    MCByteBuf writeUtf(String s);

    MCByteBuf writeUtf(String s, int i);

    NamespacedKey readNamespacedKey();

    MCByteBuf writeNamespacedKey(NamespacedKey namespacedKey);

    Date readDate();

    MCByteBuf writeDate(Date date);

    // MovingObjectPositionBlock r();

    // void a(MovingObjectPositionBlock movingobjectpositionblock);

    int capacity();

    ByteBuf capacity(int i);

    int maxCapacity();

    ByteBufAllocator alloc();

    ByteOrder order();

    ByteBuf order(ByteOrder byteorder);

    ByteBuf unwrap();

    boolean isDirect();

    boolean isReadOnly();

    ByteBuf asReadOnly();

    int readerIndex();

    ByteBuf readerIndex(int i);

    int writerIndex();

    ByteBuf writerIndex(int i);

    ByteBuf setIndex(int i, int j);

    int readableBytes();

    int writableBytes();

    int maxWritableBytes();

    boolean isReadable();

    boolean isReadable(int i);

    boolean isWritable();

    boolean isWritable(int i);

    ByteBuf clear();

    ByteBuf markReaderIndex();

    ByteBuf resetReaderIndex();

    ByteBuf markWriterIndex();

    ByteBuf resetWriterIndex();

    ByteBuf discardReadBytes();

    ByteBuf discardSomeReadBytes();

    ByteBuf ensureWritable(int i);

    int ensureWritable(int i, boolean flag);

    boolean getBoolean(int i);

    byte getByte(int i);

    short getUnsignedByte(int i);

    short getShort(int i);

    short getShortLE(int i);

    int getUnsignedShort(int i);

    int getUnsignedShortLE(int i);

    int getMedium(int i);

    int getMediumLE(int i);

    int getUnsignedMedium(int i);

    int getUnsignedMediumLE(int i);

    int getInt(int i);

    int getIntLE(int i);

    long getUnsignedInt(int i);

    long getUnsignedIntLE(int i);

    long getLong(int i);

    long getLongLE(int i);

    char getChar(int i);

    float getFloat(int i);

    double getDouble(int i);

    ByteBuf getBytes(int i, ByteBuf bytebuf);

    ByteBuf getBytes(int i, ByteBuf bytebuf, int j);

    ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k);

    ByteBuf getBytes(int i, byte[] abyte);

    ByteBuf getBytes(int i, byte[] abyte, int j, int k);

    ByteBuf getBytes(int i, ByteBuffer bytebuffer);

    ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException;

    int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException;

    int getBytes(int i, FileChannel filechannel, long j, int k) throws IOException;

    CharSequence getCharSequence(int i, int j, Charset charset);

    ByteBuf setBoolean(int i, boolean flag);

    ByteBuf setByte(int i, int j);

    ByteBuf setShort(int i, int j);

    ByteBuf setShortLE(int i, int j);

    ByteBuf setMedium(int i, int j);

    ByteBuf setMediumLE(int i, int j);

    ByteBuf setInt(int i, int j);

    ByteBuf setIntLE(int i, int j);

    ByteBuf setLong(int i, long j);

    ByteBuf setLongLE(int i, long j);

    ByteBuf setChar(int i, int j);

    ByteBuf setFloat(int i, float f);

    ByteBuf setDouble(int i, double d0);

    ByteBuf setBytes(int i, ByteBuf bytebuf);

    ByteBuf setBytes(int i, ByteBuf bytebuf, int j);

    ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k);

    ByteBuf setBytes(int i, byte[] abyte);

    ByteBuf setBytes(int i, byte[] abyte, int j, int k);

    ByteBuf setBytes(int i, ByteBuffer bytebuffer);

    int setBytes(int i, InputStream inputstream, int j) throws IOException;

    int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException;

    int setBytes(int i, FileChannel filechannel, long j, int k) throws IOException;

    ByteBuf setZero(int i, int j);

    int setCharSequence(int i, CharSequence charsequence, Charset charset);

    boolean readBoolean();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    short readShortLE();

    int readUnsignedShort();

    int readUnsignedShortLE();

    int readMedium();

    int readMediumLE();

    int readUnsignedMedium();

    int readUnsignedMediumLE();

    int readInt();

    int readIntLE();

    long readUnsignedInt();

    long readUnsignedIntLE();

    long readLong();

    long readLongLE();

    char readChar();

    float readFloat();

    double readDouble();

    ByteBuf readBytes(int i);

    ByteBuf readSlice(int i);

    ByteBuf readRetainedSlice(int i);

    ByteBuf readBytes(ByteBuf bytebuf);

    ByteBuf readBytes(ByteBuf bytebuf, int i);

    ByteBuf readBytes(ByteBuf bytebuf, int i, int j);

    ByteBuf readBytes(byte[] abyte);

    ByteBuf readBytes(byte[] abyte, int i, int j);

    ByteBuf readBytes(ByteBuffer bytebuffer);

    ByteBuf readBytes(OutputStream outputstream, int i) throws IOException;

    int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException;

    CharSequence readCharSequence(int i, Charset charset);

    int readBytes(FileChannel filechannel, long i, int j) throws IOException;

    ByteBuf skipBytes(int i);

    ByteBuf writeBoolean(boolean flag);

    ByteBuf writeByte(int i);

    ByteBuf writeShort(int i);

    ByteBuf writeShortLE(int i);

    ByteBuf writeMedium(int i);

    ByteBuf writeMediumLE(int i);

    ByteBuf writeInt(int i);

    ByteBuf writeIntLE(int i);

    ByteBuf writeLong(long i);

    ByteBuf writeLongLE(long i);

    ByteBuf writeChar(int i);

    ByteBuf writeFloat(float f);

    ByteBuf writeDouble(double d0);

    ByteBuf writeBytes(ByteBuf bytebuf);

    ByteBuf writeBytes(ByteBuf bytebuf, int i);

    ByteBuf writeBytes(ByteBuf bytebuf, int i, int j);

    ByteBuf writeBytes(byte[] abyte);

    ByteBuf writeBytes(byte[] abyte, int i, int j);

    ByteBuf writeBytes(ByteBuffer bytebuffer);

    int writeBytes(InputStream inputstream, int i) throws IOException;

    int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException;

    int writeBytes(FileChannel filechannel, long i, int j) throws IOException;

    ByteBuf writeZero(int i);

    int writeCharSequence(CharSequence charsequence, Charset charset);

    int indexOf(int i, int j, byte b0);

    int bytesBefore(byte b0);

    int bytesBefore(int i, byte b0);

    int bytesBefore(int i, int j, byte b0);

    //int forEachByte(ByteProcessor byteprocessor);

    //int forEachByte(int i, int j, io.netty.util.ByteProcessor byteprocessor);

    //int forEachByteDesc(ByteProcessor byteprocessor);

    //int forEachByteDesc(int i, int j, ByteProcessor byteprocessor);

    ByteBuf copy();

    ByteBuf copy(int i, int j);

    ByteBuf slice();

    ByteBuf retainedSlice();

    ByteBuf slice(int i, int j);

    ByteBuf retainedSlice(int i, int j);

    ByteBuf duplicate();

    ByteBuf retainedDuplicate();

    int nioBufferCount();

    ByteBuffer nioBuffer();

    ByteBuffer nioBuffer(int i, int j);

    ByteBuffer internalNioBuffer(int i, int j);

    ByteBuffer[] nioBuffers();

    ByteBuffer[] nioBuffers(int i, int j);

    boolean hasArray();

    byte[] array();

    int arrayOffset();

    boolean hasMemoryAddress();

    long memoryAddress();

    String toString(Charset charset);

    String toString(int i, int j, Charset charset);

    int hashCode();

    boolean equals(Object object);

    int compareTo(ByteBuf bytebuf);

    String toString();

    ByteBuf retain(int i);

    ByteBuf retain();

    ByteBuf touch();

    ByteBuf touch(Object object);

    int refCnt();

    boolean release();

    boolean release(int i);

}
