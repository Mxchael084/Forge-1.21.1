package de.rupturereborn.module.impl;

import de.rupturereborn.module.CombatModule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;

public class AutoArmor extends CombatModule {
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public AutoArmor() { super("AutoArmor", "Zieht Rüstung aus dem Inventar automatisch an"); }

    @Override
    protected void tick(Minecraft mc) {
        if (mc.screen != null) return;
        var p = mc.player;
        for (EquipmentSlot slot : SLOTS) {
            if (!p.getItemBySlot(slot).isEmpty()) continue;
            int bestIndex = -1, bestDefense = -1;
            for (int i = 9; i <= 44; i++) {
                var stack = p.inventoryMenu.getSlot(i).getItem();
                if (stack.getItem() instanceof ArmorItem a && a.getEquipmentSlot() == slot && a.getDefense() > bestDefense) {
                    bestDefense = a.getDefense();
                    bestIndex = i;
                }
            }
            if (bestIndex != -1) {
                mc.gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, bestIndex, 0, ClickType.QUICK_MOVE, p);
                return; // ein Teil pro Tick
            }
        }
    }
}
