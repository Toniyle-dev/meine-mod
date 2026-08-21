package net.larpblocks.render;

import net.larpblocks.LarpBlocksMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

/**
 * Draws over the vanilla hotbar slots with the contents of FakeInventory,
 * plus a permanent small watermark so it's always obvious - even to the
 * player themselves, e.g. while screen-recording - that this is a local
 * illusion and not real inventory content.
 */
public class FakeHotbarRenderer {

    private static final int SLOT_SIZE = 20;
    private static final int HOTBAR_WIDTH = 9 * SLOT_SIZE;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!LarpBlocksMod.GAME_MODE.isFakeCreativeActive()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth / 2 - HOTBAR_WIDTH / 2;
        int y = screenHeight - 22;

        for (int i = 0; i < 9; i++) {
            ItemStack fakeStack = LarpBlocksMod.FAKE_INVENTORY.getSlot(i);
            int slotX = x + i * SLOT_SIZE + 2;
            int slotY = y + 3;

            // Cover whatever the real hotbar already drew in this slot.
            context.fill(x + i * SLOT_SIZE, y, x + i * SLOT_SIZE + SLOT_SIZE, y + SLOT_SIZE, 0xFF8B8B8B);

            if (!fakeStack.isEmpty()) {
                context.drawItem(fakeStack, slotX, slotY);
            }
        }

        context.drawText(client.textRenderer, Text.literal("§e[LARP] Fake Creative (client-side)"), 4, 4, 0xFFFFFF, true);
    }
}
