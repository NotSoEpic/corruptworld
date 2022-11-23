package com.dindcrzy.corruptworld.entitycorruption;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;

public interface EntityComponent extends Component, AutoSyncedComponent, CommonTickingComponent {
    int getCorruptionValue();
    void setCorruptionValue(int i);
}
