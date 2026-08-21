package net.larpblocks.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.larpblocks.LarpBlocksMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

/**
 * Draws every entry in GhostBlockManager on top of the world each frame.
 * This never writes to the real ClientWorld chunk data - it's a pure
 * "extra drawing pass" the same way mods like schematic-preview tools work.
 */
public class GhostBlockRenderer {

    public static void render(WorldRenderContext context) {
        if (LarpBlocksMod.GHOST_BLOCKS.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) return;

        Vec3d camPos = context.camera().getPos();

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        for (Map.Entry<BlockPos, BlockState> entry : LarpBlocksMod.GHOST_BLOCKS.getAll().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            matrices.push();
            matrices.translate(pos.getX(), pos.getY(), pos.getZ());

            client.getBlockRenderManager().renderBlock(
                    state,
                    pos,
                    client.world,
                    matrices,
                    consumers.getBuffer(RenderLayer.getTranslucent()),
                    false,
                    client.world.getRandom()
            );

            matrices.pop();
        }

        matrices.pop();
    }
}
