package de.rupturereborn.module.impl;

import de.rupturereborn.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/** Für Singleplayer / eigene Server mit erlaubten Mods. */
public class Flight extends Module {
    public Flight() { super("Flight", "Fliegen wie im Kreativmodus", GLFW.GLFW_KEY_F); }

    @Override
    public void onTick(Minecraft mc) {
        var a = mc.player.getAbilities();
        a.mayfly = true;
        a.setFlyingSpeed(0.05f);
    }

    @Override
    public void onDisable(Minecraft mc) {
        var p = mc.player;
        if (p == null || p.isCreative() || p.isSpectator()) return;
        p.getAbilities().mayfly = false;
        p.getAbilities().flying = false;
    }
}
