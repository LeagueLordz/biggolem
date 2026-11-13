package com.example.biggolem.mixin.client;

import com.example.biggolem.GolemScaleAccess;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntityRenderer.class)
public class IronGolemRendererMixin {

    @Inject(method = "setupTransforms(Lnet/minecraft/entity/passive/IronGolemEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V",
            at = @At("TAIL"))
    private void biggolem_scaleModel(IronGolemEntity entity, MatrixStack matrices, float animationProgress,
                                     float bodyYaw, float tickDelta, CallbackInfo ci) {
        float s = ((GolemScaleAccess) entity).biggolem_getScale();
        if (s != 1.0f) {
            matrices.scale(s, s, s);
        }
    }
}
