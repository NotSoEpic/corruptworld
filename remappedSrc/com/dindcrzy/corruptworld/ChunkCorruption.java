package com.dindcrzy.corruptworld;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

import java.util.Arrays;
import java.util.BitSet;

public class ChunkCorruption {
    public BitSet data;
    public ChunkPos pos;
    
    public static Identifier PACKET_ID = CorruptWorld.ModId("chunk_corruption");
    
    // if a chunk has neither, it has zero corruption
    // if it has data, it is partially corrupted
    // if it has full, it is fully corrupted
    public static String CORRUPTION_DATA_KEY = "corruption_data";
    public static String CORRUPTION_FULL_KEY = "corruption_full";
    
    // needs to resynchronise
    public boolean dirty = true;
    
    public ChunkCorruption(ChunkPos pos) {
        this(new BitSet(256), pos);
    }
    
    public ChunkCorruption(BitSet data, ChunkPos pos) {
        this.pos = pos;
        this.data = data;
    }
    
    public ChunkCorruption(NbtCompound nbt) {
        this.pos = new ChunkPos(nbt.getInt("xPos"), nbt.getInt("zPos"));
        this.data = new BitSet(256);
        if (nbt.contains(CORRUPTION_FULL_KEY, NbtElement.BYTE_TYPE) && nbt.getBoolean(CORRUPTION_FULL_KEY)) {
            data.set(0, 255, true);
        } else if (nbt.contains(CORRUPTION_DATA_KEY, NbtElement.BYTE_ARRAY_TYPE)) {
            byte[] list = nbt.getByteArray(CORRUPTION_DATA_KEY);
            for (int i = 0; i < list.length; i++) {
                byte b = list[i];
                data.set(i * 8, ((b & 0x01) != 0));
                data.set(i*8+1, ((b & 0x02) != 0));
                data.set(i*8+2, ((b & 0x04) != 0));
                data.set(i*8+3, ((b & 0x08) != 0));
                data.set(i*8+4, ((b & 0x10) != 0));
                data.set(i*8+5, ((b & 0x20) != 0));
                data.set(i*8+6, ((b & 0x40) != 0));
                data.set(i*8+7, ((b & 0x80) != 0));
            }
            CorruptWorld.LOGGER.info("CC from nbt: " + data);
        }
    }

    public void writeNbt(NbtCompound nbt) {
        if (isFull()) {
            nbt.putByte(CORRUPTION_FULL_KEY, (byte)1);
        } else if (!isEmpty()) {
            byte[] bytes = data.toByteArray();
            nbt.putByteArray(CORRUPTION_DATA_KEY, bytes);
        }
    }
    
    public ChunkCorruption(PacketByteBuf buf) {
        this.pos = new ChunkPos(buf.readInt(), buf.readInt());
        this.data = new BitSet(256);
        if (!buf.readBoolean()) { // isnt empty
            if (buf.readBoolean()) {// is full
                data.set(0, 255, true);
            } else {
                data = BitSet.valueOf(buf.readByteArray());
            }
        }
    }

    public PacketByteBuf writePacket() {

        // packet is either
        // x,z,true
        // x,z,false,true
        // x,z,false,false,byte[32]

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
        boolean empty = isEmpty();
        buf.writeBoolean(empty);
        if (empty) {
            return buf;
        }
        boolean full = isFull();
        buf.writeBoolean(full);
        if (full) {
            return buf;
        }
        byte[] bytes = data.toByteArray();
        buf.writeByteArray(bytes);
        return buf;
    }

    @Override
    public String toString() {
        return "ChunkCorruption{" +
                "data=" + data +
                ", pos=" + pos +
                ", dirty=" + dirty +
                '}';
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public boolean isFull() {
        return data.cardinality() >= 256;
    }
    
    public boolean getRel(int x, int z) {
        int i = z * 16 + x;
        if (i >= 0 && i < 256) {
            return data.get(i);
        }
        return false;
    }
    
    public boolean getAbs(int x, int z) {
        return getRel(x - pos.getStartX(), z - pos.getStartZ());
    }
    
    // returns true if a value actually changed
    public boolean setRel(boolean v, int x, int z) {
        int i = z * 16 + x;
        boolean changed = false;
        if (i >= 0 && i < 256) {
            changed = data.get(i) != v;
            this.dirty |= changed;
            data.set(i, v);
        }
        return changed;
    }

    // returns true if a value actually changed
    public boolean setAbs(boolean v, int x, int z) {
        return setRel(v, x - pos.getStartX(), z - pos.getStartZ());
    }
}
