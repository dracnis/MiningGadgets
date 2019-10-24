package com.direwolf20.mininggadgets.common.containers;


import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.util.EnergisedItem;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.List;

public class ModificationTableCommands {
    public static void insertButton(ModificationTableContainer container) {
        Slot laserSlot = container.inventorySlots.get(0);
        Slot upgradeSlot = container.inventorySlots.get(1);
        ItemStack laser = laserSlot.getStack();
        ItemStack upgradeCard = upgradeSlot.getStack();
        if (laser.getItem() instanceof MiningGadget && upgradeCard.getItem() instanceof UpgradeCard) {
            Upgrade card = ((UpgradeCard) upgradeCard.getItem()).getUpgrade();
            if (card == Upgrade.EMPTY)
                return; //Don't allow inserting empty cards.
            List<Upgrade> upgrades = UpgradeTools.getUpgrades(laser);

            // Fortune has to be done slightly differently as it requires us to check
            // against all fortune tiers and not just it's existence.
            boolean hasFortune = UpgradeTools.containsUpgradeFromList(upgrades, Upgrade.FORTUNE_1);

            // Reject fortune and silk upgrades when combined together.
            //
            if ((hasFortune && card == Upgrade.SILK) ||
                    (upgrades.contains(Upgrade.SILK) && card.getBaseName().equals(Upgrade.FORTUNE_1.getBaseName())))
                return;

            if (UpgradeTools.containsUpgrade(laser, card))
                return;

            MiningGadget.applyUpgrade(laser, (UpgradeCard) upgradeCard.getItem());
            if (card == Upgrade.BATTERY_1)
                laser.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((EnergisedItem) e).setMaxEnergyStored(2000000));
            else if (card == Upgrade.BATTERY_2)
                laser.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((EnergisedItem) e).setMaxEnergyStored(5000000));
            else if (card == Upgrade.BATTERY_3)
                laser.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((EnergisedItem) e).setMaxEnergyStored(10000000));
            container.putStackInSlot(1, ItemStack.EMPTY);
        }
    }

    public static void extractButton(ModificationTableContainer container) {
        Slot laserSlot = container.inventorySlots.get(0);
        Slot upgradeSlot = container.inventorySlots.get(1);

        ItemStack laser = laserSlot.getStack();
        ItemStack upgradeCard = upgradeSlot.getStack();

        if (laser.getItem() instanceof MiningGadget && upgradeCard.isEmpty()) {
            if (!(UpgradeTools.getUpgrades(laser).isEmpty())) {
                List<Upgrade> upgrades = UpgradeTools.getUpgrades(laser);
                Upgrade upgrade = upgrades.get(upgrades.size() - 1);

                UpgradeTools.removeUpgrade(laser, upgrade);
                container.putStackInSlot(1, new ItemStack(upgrade.getCard()));
                if (upgrade == Upgrade.THREE_BY_THREE) {
                    MiningGadget.setToolRange(laser, 1);
                    if (upgrade == Upgrade.BATTERY_1)
                        laser.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((EnergisedItem) e).setMaxEnergyStored(1000000));
                }
            }
        }

    }
}
