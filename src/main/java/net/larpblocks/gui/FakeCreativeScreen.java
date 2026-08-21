package net.larpblocks.gui;

import net.larpblocks.LarpBlocksMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simplified stand-in for the vanilla creative inventory screen.
 * Picking an item here only ever writes into LarpBlocksMod.FAKE_INVENTORY -
 * it never touches the player's real inventory and sends nothing to the
 * server.
 */
public class FakeCreativeScreen extends Screen {

    private static final int COLUMNS = 9;
    private static final int CELL = 18;
    private static final int GRID_TOP = 40;

    private TextFieldWidget searchField;
    private List<Item> allItems;
    private List<Item> filtered;
    private int scroll = 0;

    public FakeCreativeScreen() {
        super(Text.literal("Fake Creative (client-side only)"));
    }

    @Override
    protected void init() {
        allItems = Registries.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .collect(Collectors.toList());
        filtered = new ArrayList<>(allItems);

        searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 12, 200, 20, Text.literal("Suche"));
        searchField.setChangedListener(this::onSearchChanged);
        this.addSelectableChild(searchField);
        this.setInitialFocus(searchField);
    }

    private void onSearchChanged(String query) {
        String q = query.toLowerCase();
        filtered = allItems.stream()
                .filter(item -> Registries.ITEM.getId(item).getPath().contains(q))
                .collect(Collectors.toList());
        scroll = 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "LARP Fake Creative - nichts hiervon ist echt, nichts wird gesendet",
                this.width / 2, 2, 0xFFFF55);

        searchField.render(context, mouseX, mouseY, delta);

        int gridWidth = COLUMNS * CELL;
        int startX = this.width / 2 - gridWidth / 2;
        int rows = Math.max(1, (this.height - GRID_TOP - 20) / CELL);

        int index = scroll * COLUMNS;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (index >= filtered.size()) break;
                Item item = filtered.get(index);
                int x = startX + col * CELL;
                int y = GRID_TOP + row * CELL;

                context.fill(x, y, x + CELL - 1, y + CELL - 1, 0x55000000);
                context.drawItem(new ItemStack(item), x + 1, y + 1);

                index++;
            }
        }

        context.drawCenteredTextWithShadow(this.textRenderer,
                "Slot: " + (LarpBlocksMod.FAKE_INVENTORY.getSelectedForDisplay() + 1)
                        + "  (Tasten 1-9 wechseln Slot, Klick = 'nehmen', ESC = schliessen)",
                this.width / 2, this.height - 12, 0xAAAAAA);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int gridWidth = COLUMNS * CELL;
            int startX = this.width / 2 - gridWidth / 2;
            int rows = Math.max(1, (this.height - GRID_TOP - 20) / CELL);

            int col = (int) ((mouseX - startX) / CELL);
            int row = (int) ((mouseY - GRID_TOP) / CELL);

            if (col >= 0 && col < COLUMNS && row >= 0 && row < rows) {
                int index = scroll * COLUMNS + row * COLUMNS + col;
                if (index >= 0 && index < filtered.size()) {
                    Item item = filtered.get(index);
                    LarpBlocksMod.FAKE_INVENTORY.setSelectedSlot(new ItemStack(item, 64));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Number row 1-9 picks which fake hotbar slot the next click fills.
        if (keyCode >= 49 && keyCode <= 57) {
            LarpBlocksMod.FAKE_INVENTORY.setSelectedForDisplay(keyCode - 49);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, scroll - (int) Math.signum(verticalAmount));
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
