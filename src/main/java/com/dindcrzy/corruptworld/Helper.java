package com.dindcrzy.corruptworld;

import net.minecraft.util.math.Vec3i;

import java.util.BitSet;

public class Helper {
    public static int posToIndex(int x, int y, int w, int h) {
        return x % w + (y % h) * w;
    }
    public static Vec3i indexToPos(int i, int w, int h) {
        return new Vec3i(i % h, Math.floorDiv(i, w), 0);
    }
    
    public static BitSet slice2D(int x1, int y1, int x2, int y2, int w, int h) {
        BitSet set = new BitSet(w * h);
        int lowX = Math.min(x1, x2);
        int lowY = Math.min(y1, y2);
        int width = Math.max(x1, x2) - lowX + 1;
        int height = Math.max(y1, y2) - lowY + 1;
        for (int i = 0; i < height; i++) {
            int bi = posToIndex(lowX, lowY + i, w, h);
            set.set(bi, bi + width, true); // [bi, bi + width)
        }
        return set;
    }
}
