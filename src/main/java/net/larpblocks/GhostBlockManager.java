package net.larpblocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores "ghost" blocks: positions that should be rendered with a certain
 * BlockState locally, without ever touching the real chunk data. The server
 * and other players never see these - they only exist in this map and in
 * our own render hook.
 */
public class GhostBlockManager {
    // Soft cap so someone can't tank their own FPS by accident.
    private static final int MAX_GHOST_BLOCKS = 20_000;

    private final Map<BlockPos, BlockState> ghostBlocks = new ConcurrentHashMap<>();

    public void place(BlockPos pos, BlockState state) {
        if (ghostBlocks.size() >= MAX_GHOST_BLOCKS) {
            return;
        }
        ghostBlocks.put(pos.toImmutable(), state);
    }

    public void remove(BlockPos pos) {
        ghostBlocks.remove(pos);
    }

    public boolean has(BlockPos pos) {
        return ghostBlocks.containsKey(pos);
    }

    public boolean isEmpty() {
        return ghostBlocks.isEmpty();
    }

    public Map<BlockPos, BlockState> getAll() {
        return ghostBlocks;
    }

    public void clear() {
        ghostBlocks.clear();
    }
}
