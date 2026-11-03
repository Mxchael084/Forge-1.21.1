package de.rupturereborn;

import de.rupturereborn.gui.ClickGuiScreen;
import de.rupturereborn.module.Module;
import de.rupturereborn.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;

@Mod(RuptureReborn.MODID)
@Mod.EventBusSubscriber(modid = RuptureReborn.MODID, value = Dist.CLIENT)
public class RuptureReborn {
    public static final String MODID = "rupturereborn";
    public static final String NAME = "Rupture Reborn";
    public static final ModuleManager MODULES = new ModuleManager();

    /** Öffnet die ClickGUI (Standard: Rechte Shift-Taste). */
    public static final int GUI_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

    public RuptureReborn() {
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        for (Module m : MODULES.all()) {
            if (m.isEnabled()) m.onTick(mc);
        }
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || event.getAction() != GLFW.GLFW_PRESS) return;
        if (event.getKey() == GUI_KEY) {
            mc.setScreen(new ClickGuiScreen());
            return;
        }
        for (Module m : MODULES.all()) {
            if (m.getKey() != GLFW.GLFW_KEY_UNKNOWN && m.getKey() == event.getKey()) m.toggle();
        }
    }

    @SubscribeEvent
    public static void onRender(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.screen != null || mc.options.hideGui) return;

        GuiGraphics g = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
        g.drawString(mc.font, NAME, 4, 4, 0xFF55FFFF, true);

        List<Module> active = MODULES.all().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparingInt((Module m) -> mc.font.width(m.getName())).reversed())
                .toList();
        int w = mc.getWindow().getGuiScaledWidth();
        int y = 4;
        for (Module m : active) {
            g.drawString(mc.font, m.getName(), w - mc.font.width(m.getName()) - 4, y, 0xFFFFFFFF, true);
            y += 10;
        }
        g.flush();
    }
}
