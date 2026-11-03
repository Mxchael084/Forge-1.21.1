package de.rupturereborn.module;

import de.rupturereborn.module.impl.*;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new Sprint());
        modules.add(new Brightness());
        modules.add(new Flight());
        modules.add(new Step());
        modules.add(new Speed());
        modules.add(new Parkour());
        modules.add(new AutoRespawn());
        modules.add(new Glow());
        modules.add(new KillAura());
        modules.add(new Triggerbot());
        modules.add(new AimAssist());
        modules.add(new AutoArmor());
        modules.add(new AutoHeal());
    }

    public List<Module> all() { return Collections.unmodifiableList(modules); }

    public <T extends Module> T get(Class<T> type) {
        for (Module m : modules) if (type.isInstance(m)) return type.cast(m);
        return null;
    }
}
