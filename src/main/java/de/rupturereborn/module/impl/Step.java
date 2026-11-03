package de.rupturereborn.module.impl;

import de.rupturereborn.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.lwjgl.glfw.GLFW;

public class Step extends Module {
    private static final double DEFAULT = 0.6;

    public Step() { super("Step", "Ganze Blöcke hochlaufen", GLFW.GLFW_KEY_H); }

    @Override
    public void onTick(Minecraft mc) {
        var attr = mc.player.getAttribute(Attributes.STEP_HEIGHT);
        if (attr != null && attr.getBaseValue() != 1.0) attr.setBaseValue(1.0);
    }

    @Override
    public void onDisable(Minecraft mc) {
        if (mc.player == null) return;
        var attr = mc.player.getAttribute(Attributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(DEFAULT);
    }
}
