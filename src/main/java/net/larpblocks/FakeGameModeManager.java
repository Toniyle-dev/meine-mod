package net.larpblocks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Tracks whether "fake creative" is currently active.
 * This NEVER touches the real game mode - it is a purely client-side flag
 * that only changes how things render locally and how we handle input.
 * The server is never told about this in any way.
 */
public class FakeGameModeManager {
    private boolean active = false;

    public boolean isFakeCreativeActive() {
        return active;
    }

    public void enableFakeCreative() {
        active = true;
        feedback("§a[LARP] Fake-Creative aktiviert - rein clientseitig, der Server merkt nichts davon.");
        LarpBlocksMod.openFakeCreativeScreen();
    }

    public void disableFakeCreative() {
        active = false;
        LarpBlocksMod.FAKE_INVENTORY.clear();
        feedback("§c[LARP] Zurueck zu Survival. Fake-Items entfernt. Platzierte Ghost-Bloecke bleiben stehen (mit .clearblocks entfernen).");
    }

    /**
     * Displays a message only in the local chat window. This does NOT send
     * anything over the network - it's the same mechanism vanilla uses for
     * client-only feedback (e.g. command usage errors).
     */
    private void feedback(String msg) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(msg), false);
        }
    }
}
