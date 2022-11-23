package com.dindcrzy.corruptworld.entitycorruption;

import com.dindcrzy.corruptworld.chunkcorrpution.CardinalChunk;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class EntityData implements EntityComponent {
    private final LivingEntity entity;
    // ranges from 0 to 120, changes by +1/-5 per half second
    int corruptionValue = 0;
    int corruptionCount = 0;
    
    public static final String CORRUPTION_DATA_KEY = "corruption_value";
    public static final String CORRUPTION_COUNT_KEY = "corruption_count";
    
    public EntityData(LivingEntity entity) { this.entity = entity; }
    
    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(CORRUPTION_DATA_KEY, NbtElement.INT_TYPE)) {
            corruptionValue = tag.getInt(CORRUPTION_DATA_KEY);
        }
        if (tag.contains(CORRUPTION_COUNT_KEY, NbtElement.INT_TYPE)) {
            corruptionCount = tag.getInt(CORRUPTION_COUNT_KEY);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(CORRUPTION_DATA_KEY, corruptionValue);
        tag.putInt(CORRUPTION_COUNT_KEY, corruptionCount);
    }

    @Override
    public void tick() {
        if (entity.age % 10 == 0) {
            boolean canIncrease = !(entity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()));
            corruptionCount = CardinalChunk.countWorldSlice(
                    entity.world,
                    entity.getBlockPos().add(-3, 0, -3),
                    entity.getBlockPos().add(3, 0, 3));
            if (corruptionCount < 10 || !canIncrease) {
                CardinalEntity.deltaCorruptionValueClamp(entity, -5, 0, 120);
            } else if (corruptionCount > 25 && canIncrease) {
                CardinalEntity.deltaCorruptionValueClamp(entity, 1, 0, 120);
            }
            int v = CardinalEntity.getCorruptionValue(entity);
            if (entity instanceof HostileEntity) { // apply beneficial effects to hostile mobs
                
            } else { // apply harmful effects to passive mobs and players
                if (v >= 40 && entity.getRandom().nextInt(20) == 0) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60));
                }
            }
        }
        CardinalEntity.CORRUPT_DATA.sync(entity);
    }

    @Override
    public int getCorruptionValue() {
        return corruptionValue;
    }

    @Override
    public void setCorruptionValue(int corruptionValue) {
        this.corruptionValue = corruptionValue;
    }
}
