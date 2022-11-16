package com.dindcrzy.corruptworld.chunkcorrpution;

import com.dindcrzy.corruptworld.Helper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public class ChunkData implements ChunkComponent {
    private final Chunk chunk;
    BitSet bits = new BitSet(256);
    boolean dirty = true;
    
    public static final String CORRUPTION_DATA_KEY = "corruption_data";
    public static final String CORRUPTION_FULL_KEY = "corruption_full";
    
    public ChunkData(Chunk chunk) {
        this.chunk = chunk;
    }
    
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.bits = new BitSet(256);
        if (tag.contains(CORRUPTION_FULL_KEY, NbtElement.BYTE_TYPE) && tag.getBoolean(CORRUPTION_FULL_KEY)) {
            bits.set(0, 255, true);
        } else if (tag.contains(CORRUPTION_DATA_KEY, NbtElement.BYTE_ARRAY_TYPE)) {
            byte[] list = tag.getByteArray(CORRUPTION_DATA_KEY);
            for (int i = 0; i < list.length; i++) {
                byte b = list[i];
                bits.set(i * 8, ((b & 0x01) != 0));
                bits.set(i*8+1, ((b & 0x02) != 0));
                bits.set(i*8+2, ((b & 0x04) != 0));
                bits.set(i*8+3, ((b & 0x08) != 0));
                bits.set(i*8+4, ((b & 0x10) != 0));
                bits.set(i*8+5, ((b & 0x20) != 0));
                bits.set(i*8+6, ((b & 0x40) != 0));
                bits.set(i*8+7, ((b & 0x80) != 0));
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (isFull()) {
            tag.putByte(CORRUPTION_FULL_KEY, (byte)1);
        } else if (!isEmpty()) {
            byte[] bytes = bits.toByteArray();
            tag.putByteArray(CORRUPTION_DATA_KEY, bytes);
        }
    }
    
    @Override
    public boolean getAbs(int x, int z) {
        return getRel(x - chunk.getPos().getStartX(), z - chunk.getPos().getStartZ());
    }

    @Override
    public boolean getRel(int x, int z) {
        //int i = z * 16 + x;
        int i = Helper.posToIndex(x, z, 16, 16);
        if (i >= 0 && i < 256) {
            return bits.get(i);
        }
        return false;
    }

    @Override
    public boolean setAbs(int x, int z, boolean v) {
        return setRel(x - chunk.getPos().getStartX(), z - chunk.getPos().getStartZ(), v);
    }

    @Override
    public boolean setRel(int x, int z, boolean v) {
        //int i = z * 16 + x;
        int i = Helper.posToIndex(x, z, 16, 16);
        boolean changed = false;
        if (i >= 0 && i < 256) {
            changed = bits.get(i) != v;
            this.dirty |= changed;
            bits.set(i, v);
        }
        return changed;
    }

    @Override
    public boolean isFull() {
        return bits != null && bits.cardinality() >= 256;
    }

    @Override
    public boolean isEmpty() {
        return bits == null || bits.isEmpty();
    }

    @Override
    public boolean isDirty() {
        return bits != null && dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public BitSet getData() {
        return bits == null ? new BitSet(256) : bits;
    }
}
