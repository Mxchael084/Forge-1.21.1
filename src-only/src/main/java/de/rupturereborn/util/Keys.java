package de.rupturereborn.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class Keys {
    private Keys() {}

    public static boolean physical(Minecraft mc, KeyMapping k) {
        return InputConstants.isKeyDown(mc.getWindow().getWindow(), k.getKey().getValue());
    }

    /** Setzt die Taste auf den echten Tastaturzustand zurück. */
    public static void restore(Minecraft mc, KeyMapping k) {
        k.setDown(physical(mc, k));
    }
}
