package com.dindcrzy.corruptworld;

public interface IChunkData {
    default void setCorruptionData(byte[] b) {}
    default byte[] getCorruptionData() { return new byte[]{}; }
}
