package com.dindcrzy.corruptworld.entities.projectiles;

import com.dindcrzy.corruptworld.CorruptWorld;
import com.dindcrzy.corruptworld.entities.ExtraDataTracker;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// Abstract Bezier Projectile Entity
public abstract class ABezierPE extends Entity {
    // todo: use trackeddata to sync client to server
    public static final TrackedData<Vec3d> START = DataTracker.registerData(ABezierPE.class, ExtraDataTracker.VEC3D);
    public static final TrackedData<Vec3d> HANDLE = DataTracker.registerData(ABezierPE.class, ExtraDataTracker.VEC3D);
    public static final TrackedData<Vec3d> END = DataTracker.registerData(ABezierPE.class, ExtraDataTracker.VEC3D);
    public static final TrackedData<Float> SPEED = DataTracker.registerData(ABezierPE.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Float> PROGRESS = DataTracker.registerData(ABezierPE.class, TrackedDataHandlerRegistry.FLOAT);
    private double baseSpeed = 0.05;
    
    public ABezierPE(EntityType<? extends ABezierPE> entityType, World world) {
        super(entityType, world);
    }
    
    public void setPoints(Vec3d start, Vec3d handle, Vec3d end) {
        if (getStart() != start || getHandle() != handle || getEnd() != end) {
            setStart(start);
            setHandle(handle);
            setEnd(end);
            calculateSpeed();
        }
    }
    
    public Vec3d getStart() {
        return dataTracker.get(START);
    }
    public void setStart(Vec3d start) {
        dataTracker.set(START, start);
    }
    public Vec3d getHandle() {
        return dataTracker.get(HANDLE);
    }
    public void setHandle(Vec3d handle) {
        dataTracker.set(HANDLE, handle);
    }
    public Vec3d getEnd() {
        return dataTracker.get(END);
    }
    public void setEnd(Vec3d end) {
        dataTracker.set(END, end);
    }
    public float getProgress() {
        return dataTracker.get(PROGRESS);
    }
    public void setProgress(float progress) {
        dataTracker.set(PROGRESS, progress);
    }
    public void setSpeed(float speed) {
        dataTracker.set(SPEED, speed);
    }
    public double getSpeed() {
        return dataTracker.get(SPEED);
    }
    
    public double getDeltaProgress() { return baseSpeed * getSpeed(); }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(START, Vec3d.ZERO);
        dataTracker.startTracking(HANDLE, Vec3d.ZERO);
        dataTracker.startTracking(END, Vec3d.ZERO);
        dataTracker.startTracking(SPEED, 0f);
        dataTracker.startTracking(PROGRESS, 0f);
    }
    
    private void calculateSpeed() {
        this.baseSpeed = 0.05 / approxCurveLength(getStart(), getHandle(), getEnd());
    }

    // data isnt synced to the client, 
    @Override
    public void tick() {
        super.tick();
        double deltaProgress = getDeltaProgress();
        if (deltaProgress == 0 && !world.isClient()) {
            CorruptWorld.LOGGER.warn("Bezier Projectile " + getName().asString() + " at " + getPos() + " had zero delta progress! removing...");
            discard();
            return;
        }
        float newProgress = (float) (getProgress() + deltaProgress);
        if (this.restartPath()) {
            newProgress = ((newProgress % 1) + 1) % 1;
        }
        if ((newProgress >= 0 && newProgress < 1)) {
            Vec3d newPos = getPointOnCurve(newProgress);
            setPosition(newPos.x, newPos.y, newPos.z);
            setProgress(newProgress);
            setVelocity(newPos.subtract(getPos()));
            velocityDirty = true;
            
            Vec3d nextPosition = getPos().add(getDirection().multiply(getWidth() * .5));
            HitResult hitResult = getEntityCollision(getPos(), nextPosition);
            if (hitResult == null) {
                hitResult = this.world.raycast(new RaycastContext(getPos(), nextPosition, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            }
            if (hitResult != null) {
                onCollision(hitResult);
            }
        } else if (!world.isClient()) {
            discard();
        }
    }
    
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
    }
    
    public boolean restartPath() { return false; }

    protected void onCollision(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            onEntityHit((EntityHitResult)result);
        } else if (result.getType() == HitResult.Type.BLOCK) {
            onBlockHit((BlockHitResult)result);
        }
    }
    
    public Vec3d getDirection() {
        return getPointOnCurve(getProgress()+0.05f).subtract(getPointOnCurve(getProgress())).normalize();
    }
    
    protected void onEntityHit(EntityHitResult result) {
        
    }
    
    protected void onBlockHit(BlockHitResult result) {
        
    }
    
    protected boolean canHit(Entity entity) {
        return !entity.isSpectator() && entity.isAlive() && entity.collides();
    }
    
    public static double approxCurveLength(Vec3d start, Vec3d handle, Vec3d end) {
        float delta = 0.05f;
        float len = 0.0001f; // no div by 0
        for (float i = 0; i < 1; i+=delta) {
            Vec3d a = getPointOnCurve(start, handle, end, i);
            Vec3d b = getPointOnCurve(start, handle, end, i+delta);
            len += a.distanceTo(b);
        }
        return len;
    }
    
    public static Vec3d getPointOnCurve(Vec3d start, Vec3d handle, Vec3d end, float progress) {
        Vec3d ab = start.add(handle.subtract(start).multiply(progress));
        Vec3d bc = handle.add(end.subtract(handle).multiply(progress));
        return ab.add(bc.subtract(ab).multiply(progress));
    }
    
    public Vec3d getPointOnCurve(float progress) {
        return getPointOnCurve(getStart(), getHandle(), getEnd(), progress);
    }

    public static final String START_KEY = "bezier_start";
    public static final String HANDLE_KEY = "bezier_handle";
    public static final String END_KEY = "bezier_end";
    public static final String PROGRESS_KEY = "bezier_progress";
    public static final String SPEED_MUL_KEY = "bezier_speed_multiplier";
    
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.put(START_KEY, toNbtList(START));
        nbt.put(HANDLE_KEY, toNbtList(HANDLE));
        nbt.put(END_KEY, toNbtList(END));
        nbt.putFloat(PROGRESS_KEY, getProgress());
        nbt.putFloat(SPEED_MUL_KEY, speed);
    }
    
    private NbtList toNbtList(TrackedData<Vec3d> data) {
        Vec3d vec = dataTracker.get(data);
        return toNbtList(vec.x, vec.y, vec.z);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        dataTracker.set(START, vec3dFromNbt(nbt, START_KEY, Vec3d.ZERO));
        dataTracker.set(HANDLE, vec3dFromNbt(nbt, HANDLE_KEY, Vec3d.ZERO));
        dataTracker.set(END, vec3dFromNbt(nbt, END_KEY, Vec3d.ZERO));
        dataTracker.set(PROGRESS, floatFromNbt(nbt, PROGRESS_KEY));
        dataTracker.set(SPEED, floatFromNbt(nbt, SPEED_MUL_KEY));
        calculateSpeed();
    }
    
    private static float floatFromNbt(NbtCompound nbt, String key) {
        return nbt.getFloat(key);
    }
    
    private static Vec3d vec3dFromNbt(NbtCompound nbt, String key, Vec3d els) {
        if (nbt.contains(key, NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList(key, NbtElement.DOUBLE_TYPE);
            return new Vec3d(list.getDouble(0), list.getDouble(1), list.getDouble(2));
        }
        return els;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, 0);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (data.getType() == ExtraDataTracker.VEC3D) {
            calculateSpeed();
        }
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
}
