package com.dindcrzy.corruptworld;

public interface IWorld {
    default void setCorruption(boolean v, int x, int z) {}
    default boolean getCorruption(int x, int z) {return false;}
}
