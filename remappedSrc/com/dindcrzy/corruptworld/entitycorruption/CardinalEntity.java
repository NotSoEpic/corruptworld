package com.dindcrzy.corruptworld.entitycorruption;

import com.dindcrzy.corruptworld.CorruptWorld;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.LivingEntity;

import java.util.Optional;

public class CardinalEntity implements EntityComponentInitializer {
    public static final ComponentKey<EntityComponent> CORRUPT_DATA =
            ComponentRegistry.getOrCreate(CorruptWorld.ModId("corrupt_data"), EntityComponent.class);
    
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CORRUPT_DATA, EntityData::new, RespawnCopyStrategy.LOSSLESS_ONLY); 
        registry.registerFor(LivingEntity.class, CORRUPT_DATA, EntityData::new);
    }
    
    public static int getCorruptionValue(LivingEntity provider) {
        Optional<EntityComponent> optional = CORRUPT_DATA.maybeGet(provider);
        if (optional.isPresent()) {
            EntityComponent entity = optional.get();
            return entity.getCorruptionValue();
        }
        return 0;
    }

    public static void setCorruptionValue(LivingEntity provider, int i) {
        Optional<EntityComponent> optional = CORRUPT_DATA.maybeGet(provider);
        if (optional.isPresent()) {
            EntityComponent entity = optional.get();
            entity.setCorruptionValue(i);
        }
    }
    
    public static void deltaCorruptionValueClamp(LivingEntity provider, int i, int min, int max) {
        Optional<EntityComponent> optional = CORRUPT_DATA.maybeGet(provider);
        if (optional.isPresent()) {
            EntityComponent entity = optional.get();
            entity.setCorruptionValue(Math.min(Math.max(entity.getCorruptionValue() + i, min), max));
        }
    }
}
