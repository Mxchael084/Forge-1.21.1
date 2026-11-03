package de.rupturereborn.module.impl;

import de.rupturereborn.module.CombatModule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class AimAssist extends CombatModule {
    private static final float SMOOTH = 0.2f;   // Anteil der Korrektur pro Tick
    private static final float MAX_ANGLE = 60f; // nur Ziele im Blickfeld

    public AimAssist() { super("AimAssist", "Zielt sanft auf das nächste Ziel", GLFW.GLFW_KEY_Z); }

    @Override
    protected void tick(Minecraft mc) {
        var p = mc.player;
        var target = nearestTarget(mc, 5.0);
        if (target == null) return;
        Vec3 d = target.getEyePosition().subtract(p.getEyePosition());
        float yaw = (float) (Math.toDegrees(Math.atan2(d.z, d.x)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(d.y, Math.sqrt(d.x * d.x + d.z * d.z)));
        float dYaw = Mth.wrapDegrees(yaw - p.getYRot());
        float dPitch = pitch - p.getXRot();
        if (Math.abs(dYaw) > MAX_ANGLE) return;
        p.setYRot(p.getYRot() + dYaw * SMOOTH);
        p.setXRot(Mth.clamp(p.getXRot() + dPitch * SMOOTH, -90f, 90f));
    }
}
