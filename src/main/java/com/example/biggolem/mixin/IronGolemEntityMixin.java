package com.example.biggolem.mixin;

import com.example.biggolem.GolemScaleAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends GolemEntity implements GolemScaleAccess {

    @Unique
    private static final TrackedData<Float> BIGGOLEM_SCALE =
            DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Unique
    private float biggolem_lastScale = 1.0f;

    protected IronGolemEntityMixin(EntityType<? extends GolemEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void biggolem_initScale(CallbackInfo ci) {
        this.dataTracker.startTracking(BIGGOLEM_SCALE, 1.0f);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void biggolem_saveScale(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("BigGolemScale", this.biggolem_getScale());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void biggolem_loadScale(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("BigGolemScale")) {
            this.biggolem_setScale(nbt.getFloat("BigGolemScale"));
        }
    }

    // Client par tracked data aane ke baad hitbox dobara calculate karne ke liye
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void biggolem_checkScale(CallbackInfo ci) {
        float s = this.biggolem_getScale();
        if (s != this.biggolem_lastScale) {
            this.biggolem_lastScale = s;
            this.calculateDimensions();
        }
    }

    // LivingEntity.getDimensions() isi factor se hitbox scale karta hai
    @Override
    public float getScaleFactor() {
        return super.getScaleFactor() * this.biggolem_getScale();
    }

    @Override
    public float biggolem_getScale() {
        return this.dataTracker.get(BIGGOLEM_SCALE);
    }

    @Override
    public void biggolem_setScale(float scale) {
        this.dataTracker.set(BIGGOLEM_SCALE, scale);
        this.biggolem_lastScale = scale;
        this.calculateDimensions();
    }
}
