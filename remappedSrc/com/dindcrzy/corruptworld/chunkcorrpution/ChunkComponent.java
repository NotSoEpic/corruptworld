package com.dindcrzy.corruptworld.chunkcorrpution;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.BitSet;

public interface ChunkComponent extends Component, AutoSyncedComponent {
    boolean getAbs(int x, int z);
    boolean getRel(int x, int z);
    boolean setAbs(int x, int z, boolean v);
    boolean setRel(int x, int z, boolean v);
    boolean isFull();
    boolean isEmpty();
    boolean isDirty();
    void setDirty(boolean b);
    BitSet getData();
}
