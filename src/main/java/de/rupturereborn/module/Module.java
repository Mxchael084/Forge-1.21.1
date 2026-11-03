package de.rupturereborn.module;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    private final String name;
    private final String description;
    private int key;
    private boolean enabled;

    protected Module(String name, String description, int defaultKey) {
        this.name = name;
        this.description = description;
        this.key = defaultKey;
    }

    protected Module(String name, String description) {
        this(name, description, GLFW.GLFW_KEY_UNKNOWN);
    }

    public final void toggle() { setEnabled(!enabled); }

    public final void setEnabled(boolean state) {
        if (state == enabled) return;
        enabled = state;
        Minecraft mc = Minecraft.getInstance();
        if (enabled) onEnable(mc); else onDisable(mc);
    }

    public void onEnable(Minecraft mc) {}
    public void onDisable(Minecraft mc) {}
    public void onTick(Minecraft mc) {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
}
