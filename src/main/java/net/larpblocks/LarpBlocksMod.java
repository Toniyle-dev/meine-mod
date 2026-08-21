package net.larpblocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.larpblocks.gui.FakeCreativeScreen;
import net.larpblocks.interact.BlockInteractionHandler;
import net.larpblocks.render.FakeHotbarRenderer;
import net.larpblocks.render.GhostBlockRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class LarpBlocksMod implements ClientModInitializer {

    public static final FakeGameModeManager GAME_MODE = new FakeGameModeManager();
    public static final GhostBlockManager GHOST_BLOCKS = new GhostBlockManager();
    public static final FakeInventory FAKE_INVENTORY = new FakeInventory();

    // Reopens the fake creative picker any time, as long as fake-creative is on.
    // We use a dedicated key instead of hijacking the real inventory key (E) -
    // that keeps this robust without needing mixins into vanilla input handling.
    private static final KeyBinding OPEN_PICKER_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.larpblocks.open_picker",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.larpblocks"
    ));

    @Override
    public void onInitializeClient() {

        // 1) Intercept ".creative" / ".survival" / ".clearblocks" chat messages
        //    so they are NEVER sent to the server.
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            String trimmed = message.trim();

            if (trimmed.equalsIgnoreCase(".creative")) {
                GAME_MODE.enableFakeCreative();
                return false;
            }

            if (trimmed.equalsIgnoreCase(".survival")) {
                GAME_MODE.disableFakeCreative();
                return false;
            }

            if (trimmed.equalsIgnoreCase(".clearblocks")) {
                GHOST_BLOCKS.clear();
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§c[LARP] Alle Ghost-Bloecke entfernt."), false);
                }
                return false;
            }

            return true; // everything else: send normally, unchanged
        });

        // 2) Render ghost blocks on top of the world - never touches real chunk data.
        WorldRenderEvents.AFTER_TRANSLUCENT.register(GhostBlockRenderer::render);

        // 3) Draw the fake hotbar overlay while fake-creative is active.
        HudRenderCallback.EVENT.register(FakeHotbarRenderer::render);

        // 4) Place/remove ghost blocks on right-click/left-click instead of
        //    sending real interaction packets, while fake-creative is active.
        UseBlockCallback.EVENT.register(BlockInteractionHandler::onUseBlock);
        AttackBlockCallback.EVENT.register(BlockInteractionHandler::onAttackBlock);

        // 5) Dedicated key to reopen the picker.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_PICKER_KEY.wasPressed()) {
                if (GAME_MODE.isFakeCreativeActive() && client.currentScreen == null) {
                    openFakeCreativeScreen();
                }
            }
        });
    }

    public static void openFakeCreativeScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new FakeCreativeScreen());
    }
}
