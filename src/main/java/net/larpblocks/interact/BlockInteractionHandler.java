package net.larpblocks.interact;

import net.larpblocks.LarpBlocksMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Both callbacks below only ever act on world.isClient == true, and only
 * while fake-creative is on. Returning ActionResult.SUCCESS from the client
 * side is the standard Fabric API way of saying "handled, don't send the
 * normal interaction packet to the server" - so real placing/breaking
 * networking never happens for these actions.
 */
public class BlockInteractionHandler {

    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!world.isClient) return ActionResult.PASS;
        if (!LarpBlocksMod.GAME_MODE.isFakeCreativeActive()) return ActionResult.PASS;

        ItemStack heldStack = player.getStackInHand(hand);
        if (!(heldStack.getItem() instanceof BlockItem blockItem)) return ActionResult.PASS;

        BlockPos targetPos = hitResult.getBlockPos().offset(hitResult.getSide());
        BlockState state = blockItem.getBlock().getDefaultState();

        LarpBlocksMod.GHOST_BLOCKS.place(targetPos, state);

        return ActionResult.SUCCESS;
    }

    public static ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (!world.isClient) return ActionResult.PASS;
        if (!LarpBlocksMod.GAME_MODE.isFakeCreativeActive()) return ActionResult.PASS;

        if (LarpBlocksMod.GHOST_BLOCKS.has(pos)) {
            LarpBlocksMod.GHOST_BLOCKS.remove(pos);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
