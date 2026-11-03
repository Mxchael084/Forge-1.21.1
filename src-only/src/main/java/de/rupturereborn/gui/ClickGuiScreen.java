package de.rupturereborn.gui;

import com.mojang.blaze3d.platform.InputConstants;
import de.rupturereborn.RuptureReborn;
import de.rupturereborn.module.Module;
import de.rupturereborn.module.impl.Gui;
import de.rupturereborn.module.setting.BoolSetting;
import de.rupturereborn.module.setting.Setting;
import de.rupturereborn.module.setting.SliderSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/** Zwei (bzw. drei) verschiebbare Panels mit Modulen; Rechtsklick auf ein Modul öffnet dessen Einstellungen. */
public class ClickGuiScreen extends Screen {
    private static final int PW = 110, HEAD = 16, ROW = 15;
    private static final int SW = 140;           // Breite des Einstellungs-Panels

    private static final int PURPLE = 0xFF8A2BFF;
    private static final int PURPLE_DARK = 0xFF5B2DA8;
    private static final int GREY = 0xFF4A4A4A;
    private static final int GREY_HOVER = 0xFF5E5E5E;
    private static final int BORDER = 0xFF1C1C1C;
    private static final int HEADER = 0xFF2E2E2E;
    private static final int TRACK = 0xFF2A2A2A;
    private static final int WHITE = 0xFFFFFFFF;

    private static class Panel {
        final String title;
        final List<Module> modules = new ArrayList<>();
        int x, y;
        boolean collapsed;
        Panel(String title, int x, int y, String... names) {
            this.title = title; this.x = x; this.y = y;
            for (String n : names) {
                Module m = RuptureReborn.MODULES.byName(n);
                if (m != null) modules.add(m);
            }
        }
        int height() { return HEAD + (collapsed ? 0 : modules.size() * ROW); }
    }

    private static List<Panel> panels;

    // Einstellungs-Panel
    private static int sx = 270, sy = 20;
    private Module selected;
    private boolean listeningForKey;

    // Drag-Zustand
    private Panel dragPanel;
    private boolean dragSettings;
    private int dragOffX, dragOffY;
    private SliderSetting dragSlider;

    public ClickGuiScreen() {
        super(Component.literal(RuptureReborn.NAME));
        if (panels == null) {
            panels = new ArrayList<>();
            panels.add(new Panel("b", 10, 20, "FastPlace", "FullBright", "Hud", "NameTags", "AutoArmor", "ChestStealer", "Fly", "Speed"));
            panels.add(new Panel("a", 130, 20, "KillAura", "Gui", "Reach", "SelfDestruct", "AutoClick", "Aim", "Strafe", "WTap", "STap", "Velocity"));
            panels.add(new Panel("c", 250, 20, "Sprint", "Step", "Glow", "Parkour", "AutoRespawn", "Triggerbot", "AutoHeal"));
        }
    }

    // ---------- Rendering ----------

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        Gui gui = RuptureReborn.MODULES.get(Gui.class);
        int alpha = gui == null ? 0x66 : (int) (gui.dim.get() / 100.0 * 0xAA);
        g.fill(0, 0, width, height, alpha << 24);

        String hoverText = null;
        for (Panel p : panels) {
            box(g, p.x, p.y, PW, HEAD, HEADER);
            g.drawString(font, p.title, p.x + 5, p.y + 4, WHITE, true);
            // Einklapp-Knopf
            int bx = p.x + PW - 14, by = p.y + 2;
            g.fill(bx, by, bx + 12, by + 12, PURPLE);
            g.drawCenteredString(font, p.collapsed ? "+" : "-", bx + 6, by + 2, WHITE);

            if (p.collapsed) continue;
            int y = p.y + HEAD;
            for (Module m : p.modules) {
                boolean hover = mx >= p.x && mx < p.x + PW && my >= y && my < y + ROW;
                int color = m.isEnabled() ? PURPLE_DARK : (hover ? GREY_HOVER : GREY);
                box(g, p.x, y, PW, ROW, color);
                g.drawCenteredString(font, m.getName(), p.x + PW / 2, y + 4, WHITE);
                if (hover) hoverText = m.getDescription();
                y += ROW;
            }
        }

        if (selected != null) renderSettings(g, mx, my);
        if (hoverText != null) g.drawCenteredString(font, hoverText, width / 2, height - 16, 0xFFBBBBBB);
        g.drawCenteredString(font, "Linksklick: an/aus  |  Rechtsklick: Einstellungen", width / 2, height - 30, 0xFF888888);
    }

    private void renderSettings(GuiGraphics g, int mx, int my) {
        int h = settingsHeight();
        box(g, sx, sy, SW, HEAD, HEADER);
        g.drawCenteredString(font, selected.getName(), sx + SW / 2, sy + 4, WHITE);
        g.fill(sx, sy + HEAD, sx + SW, sy + h, 0xFF202020);

        int y = sy + HEAD + 4;
        for (Setting s : selected.getSettings()) {
            if (s instanceof SliderSetting sl) {
                g.drawString(font, sl.getName() + ": " + sl.display(), sx + 4, y, WHITE, true);
                int bx = sx + 4, by = y + 11, bw = SW - 8;
                g.fill(bx - 1, by - 1, bx + bw + 1, by + 9, BORDER);
                g.fill(bx, by, bx + bw, by + 8, TRACK);
                double frac = (sl.get() - sl.getMin()) / (sl.getMax() - sl.getMin());
                g.fill(bx, by, bx + (int) (bw * frac), by + 8, PURPLE);
                y += 24;
            } else if (s instanceof BoolSetting b) {
                box(g, sx + 4, y, SW - 8, ROW, b.get() ? PURPLE_DARK : GREY);
                g.drawCenteredString(font, b.getName() + ": " + (b.get() ? "AN" : "AUS"), sx + SW / 2, y + 4, WHITE);
                y += ROW + 4;
            }
        }
        // Tastenbelegung
        box(g, sx + 4, y, SW - 8, ROW, listeningForKey ? PURPLE : GREY);
        String keyName = selected.getKey() == GLFW.GLFW_KEY_UNKNOWN ? "NONE"
                : InputConstants.getKey(selected.getKey(), 0).getDisplayName().getString();
        g.drawCenteredString(font, listeningForKey ? "Taste drücken..." : "Taste: " + keyName, sx + SW / 2, y + 4, WHITE);
    }

    private int settingsHeight() {
        int h = HEAD + 4;
        for (Setting s : selected.getSettings()) h += (s instanceof SliderSetting) ? 24 : ROW + 4;
        return h + ROW + 4;
    }

    private static void box(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + h, BORDER);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, color);
    }

    // ---------- Input ----------

    @Override
    public boolean mouseClicked(double mxD, double myD, int button) {
        int mx = (int) mxD, my = (int) myD;

        if (selected != null && handleSettingsClick(mx, my, button)) return true;

        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel p = panels.get(i);
            if (mx < p.x || mx >= p.x + PW || my < p.y || my >= p.y + p.height()) continue;

            if (my < p.y + HEAD) {
                int bx = p.x + PW - 14;
                if (mx >= bx && mx < bx + 12 && button == 0) {
                    p.collapsed = !p.collapsed;
                } else if (button == 0) {
                    dragPanel = p;
                    dragOffX = mx - p.x;
                    dragOffY = my - p.y;
                    panels.remove(p);
                    panels.add(p); // nach vorne holen
                }
                return true;
            }
            if (p.collapsed) return true;
            int index = (my - p.y - HEAD) / ROW;
            if (index >= 0 && index < p.modules.size()) {
                Module m = p.modules.get(index);
                if (button == 0) {
                    m.toggle();
                } else if (button == 1) {
                    selected = (selected == m) ? null : m;
                    listeningForKey = false;
                    if (selected != null) {
                        sx = Math.min(p.x + PW + 6, Math.max(0, width - SW));
                        sy = p.y + HEAD + index * ROW;
                    }
                }
            }
            return true;
        }
        return super.mouseClicked(mxD, myD, button);
    }

    private boolean handleSettingsClick(int mx, int my, int button) {
        int h = settingsHeight();
        if (mx < sx || mx >= sx + SW || my < sy || my >= sy + h) return false;
        if (button != 0) return true;

        if (my < sy + HEAD) {
            dragSettings = true;
            dragOffX = mx - sx;
            dragOffY = my - sy;
            return true;
        }
        int y = sy + HEAD + 4;
        for (Setting s : selected.getSettings()) {
            if (s instanceof SliderSetting sl) {
                if (my >= y + 9 && my < y + 21) {
                    dragSlider = sl;
                    updateSlider(sl, mx);
                    return true;
                }
                y += 24;
            } else if (s instanceof BoolSetting b) {
                if (my >= y && my < y + ROW) { b.toggle(); return true; }
                y += ROW + 4;
            }
        }
        if (my >= y && my < y + ROW) listeningForKey = true;
        return true;
    }

    private void updateSlider(SliderSetting sl, int mx) {
        int bx = sx + 4, bw = SW - 8;
        double frac = Math.max(0, Math.min(1, (mx - bx) / (double) bw));
        sl.set(sl.getMin() + frac * (sl.getMax() - sl.getMin()));
    }

    @Override
    public boolean mouseDragged(double mxD, double myD, int button, double dx, double dy) {
        int mx = (int) mxD, my = (int) myD;
        if (dragSlider != null) { updateSlider(dragSlider, mx); return true; }
        if (dragSettings) { sx = mx - dragOffX; sy = my - dragOffY; return true; }
        if (dragPanel != null) { dragPanel.x = mx - dragOffX; dragPanel.y = my - dragOffY; return true; }
        return super.mouseDragged(mxD, myD, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragPanel = null;
        dragSlider = null;
        dragSettings = false;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (listeningForKey && selected != null) {
            selected.setKey(key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE
                    ? GLFW.GLFW_KEY_UNKNOWN : key);
            listeningForKey = false;
            return true;
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
