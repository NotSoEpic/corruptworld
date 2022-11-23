package com.dindcrzy.corruptworld.entities;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class ExtraDataTracker {
    public static final TrackedDataHandler<Vec3d> VEC3D = new TrackedDataHandler<>() {
        public void write(PacketByteBuf packetByteBuf, Vec3d pos) {
            packetByteBuf.writeDouble(pos.x);
            packetByteBuf.writeDouble(pos.y);
            packetByteBuf.writeDouble(pos.z);
        }

        public Vec3d read(PacketByteBuf packetByteBuf) {
            return new Vec3d(packetByteBuf.readDouble(), packetByteBuf.readDouble(), packetByteBuf.readDouble());
        }

        public Vec3d copy(Vec3d pos) {
            return pos;
        }
    };
    
    public static void initialize() {
        TrackedDataHandlerRegistry.register(ExtraDataTracker.VEC3D);
    }
}
