package de.rupturereborn.gui;

import de.rupturereborn.RuptureReborn;
import de.rupturereborn.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int W = 110, H = 20, GAP = 4, COLS = 4;

    public ClickGuiScreen() {
        super(Component.literal(RuptureReborn.NAME));
    }

    @Override
    protected void init() {
        List<Module> mods = RuptureReborn.MODULES.all();
        int totalW = COLS * W + (COLS - 1) * GAP;
        int startX = (width - totalW) / 2;
        int startY = 40;
        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int x = startX + (i % COLS) * (W + GAP);
            int y = startY + (i / COLS) * (H + GAP);
            addRenderableWidget(Button.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
            }).bounds(x, y, W, H).build());
        }
    }

    private static Component label(Module m) {
        return Component.literal((m.isEnabled() ? "§a" : "§c") + m.getName());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g, mx, my, partial);
        g.drawCenteredString(font, RuptureReborn.NAME, width / 2, 15, 0xFF55FFFF);
        super.render(g, mx, my, partial);
        for (var w : renderables) {
            if (w instanceof Button b && b.isMouseOver(mx, my)) {
                for (Module m : RuptureReborn.MODULES.all()) {
                    if (b.getMessage().getString().endsWith(m.getName())) {
                        g.drawCenteredString(font, m.getDescription(), width / 2, height - 20, 0xFFAAAAAA);
                    }
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
