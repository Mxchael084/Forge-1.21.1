package de.rupturereborn.module.impl;

import de.rupturereborn.module.CombatModule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.lwjgl.glfw.GLFW;

public class KillAura extends CombatModule {
    public KillAura() { super("KillAura", "Greift das nächste Ziel in Reichweite an (nur lokal)", GLFW.GLFW_KEY_R); }

    @Override
    protected void tick(Minecraft mc) {
        var p = mc.player;
        if (p.getAttackStrengthScale(0f) < 1f) return;
        var target = nearestTarget(mc, 3.5);
        if (target == null) return;
        mc.gameMode.attack(p, target);
        p.swing(InteractionHand.MAIN_HAND);
    }
}
