package de.rupturereborn.module.impl;

import de.rupturereborn.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
    public Speed() { super("Speed", "Leicht erhöhte Bodengeschwindigkeit", GLFW.GLFW_KEY_J); }

    @Override
    public void onTick(Minecraft mc) {
        var p = mc.player;
        if (!p.onGround() || p.isInWater() || p.input.forwardImpulse == 0 && p.input.leftImpulse == 0) return;
        Vec3 v = p.getDeltaMovement();
        p.setDeltaMovement(v.x * 1.08, v.y, v.z * 1.08);
    }
}
