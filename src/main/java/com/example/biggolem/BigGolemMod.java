package com.example.biggolem;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import com.mojang.brigadier.context.CommandContext;

public class BigGolemMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // /biggolem <scale>            -> frozen golem (AI off)
            // /biggolem <scale> <noai>     -> noai true/false
            dispatcher.register(CommandManager.literal("biggolem")
                    .requires(src -> src.hasPermissionLevel(2))
                    .then(CommandManager.argument("scale", FloatArgumentType.floatArg(0.1f, 20f))
                            .executes(ctx -> spawn(ctx, true))
                            .then(CommandManager.argument("noai", BoolArgumentType.bool())
                                    .executes(ctx -> spawn(ctx, BoolArgumentType.getBool(ctx, "noai"))))));

            // /golemscale <targets> <scale>  -> existing iron golems ka size badlo
            dispatcher.register(CommandManager.literal("golemscale")
                    .requires(src -> src.hasPermissionLevel(2))
                    .then(CommandManager.argument("targets", EntityArgumentType.entities())
                            .then(CommandManager.argument("scale", FloatArgumentType.floatArg(0.1f, 20f))
                                    .executes(ctx -> {
                                        float scale = FloatArgumentType.getFloat(ctx, "scale");
                                        int count = 0;
                                        for (Entity e : EntityArgumentType.getEntities(ctx, "targets")) {
                                            if (e instanceof IronGolemEntity golem) {
                                                ((GolemScaleAccess) golem).biggolem_setScale(scale);
                                                count++;
                                            }
                                        }
                                        final int n = count;
                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal("Resized " + n + " iron golem(s) to " + scale + "x"), true);
                                        return n;
                                    }))));
        });
    }

    private static int spawn(CommandContext<ServerCommandSource> ctx, boolean noAi) {
        ServerCommandSource src = ctx.getSource();
        ServerWorld world = src.getWorld();
        float scale = FloatArgumentType.getFloat(ctx, "scale");

        IronGolemEntity golem = EntityType.IRON_GOLEM.create(world);
        if (golem == null) {
            return 0;
        }
        Vec3d p = src.getPosition();
        golem.refreshPositionAndAngles(p.x, p.y, p.z, src.getRotation().y, 0.0f);
        ((GolemScaleAccess) golem).biggolem_setScale(scale);
        golem.setAiDisabled(noAi);
        golem.setPersistent();
        world.spawnEntity(golem);

        src.sendFeedback(() -> Text.literal("Spawned iron golem at " + scale + "x size"
                + (noAi ? " (frozen)" : "")), true);
        return 1;
    }
}
