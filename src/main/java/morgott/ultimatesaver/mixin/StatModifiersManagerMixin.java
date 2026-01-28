package morgott.ultimatesaver.mixin;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.StatModifiersManager;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 * Mixin for per-weapon SignatureEnergy (ultimate) storage.
 * - SAVE: Only when switching FROM a weapon with ultimate (HEAD inject)
 * - RESTORE: When switching TO a weapon with ultimate and energy=0 (RETURN inject)
 */
@Mixin(StatModifiersManager.class)
public abstract class StatModifiersManagerMixin {

    @Unique
    private static final Logger LOGGER = Logger.getLogger("UltimateSaver");

    @Unique
    private static final String SIGNATURE_ENERGY_KEY = "SavedSignatureEnergy";

    // Track slot and energy for detecting weapon switches
    @Unique
    private static final Map<EntityStatMap, Byte> lastWeaponSlot = new WeakHashMap<>();

    @Unique
    private static final Map<EntityStatMap, Float> lastSignatureEnergy = new WeakHashMap<>();

    // Track if we already restored for this weapon session
    @Unique
    private static final Map<EntityStatMap, Boolean> hasRestored = new WeakHashMap<>();

    @Inject(method = "recalculateEntityStatModifiers", at = @At("HEAD"))
    private void onBeforeRecalculate(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull EntityStatMap statMap,
            @Nonnull ComponentAccessor<EntityStore> componentAccessor,
            CallbackInfo ci
    ) {
        if (!(EntityUtils.getEntity(ref, componentAccessor) instanceof LivingEntity livingEntity)) {
            return;
        }

        Inventory inventory = livingEntity.getInventory();
        ItemContainer hotbar = inventory.getHotbar();

        if (hotbar == null) {
            return;
        }

        byte currentSlot = inventory.getActiveHotbarSlot();
        Byte oldSlot = lastWeaponSlot.get(statMap);

        // Check if slot changed and we had a weapon before
        if (oldSlot != null && oldSlot.byteValue() != currentSlot) {
            // Switching away from previous slot - save energy to the OLD weapon
            ItemStack oldItem = hotbar.getItemStack(oldSlot);
            if (oldItem != null && !oldItem.isEmpty()) {
                // Check if old item is a weapon (has weapon component)
                if (oldItem.getItem().getWeapon() != null) {
                    // Get the tracked energy (what we last saw)
                    Float energyToSave = lastSignatureEnergy.get(statMap);

                    if (energyToSave != null && energyToSave > 0) {
                        ItemStack updatedItem = oldItem.withMetadata(SIGNATURE_ENERGY_KEY, Codec.FLOAT, energyToSave);
                        hotbar.setItemStackForSlot(oldSlot, updatedItem);
                        LOGGER.info("[UltimateSaver] SAVED on switch: slot=" + oldSlot + " energy=" + energyToSave);
                    } else {
                        // Clear metadata if no energy
                        ItemStack clearedItem = oldItem.withMetadata(SIGNATURE_ENERGY_KEY, Codec.FLOAT, null);
                        hotbar.setItemStackForSlot(oldSlot, clearedItem);
                        LOGGER.info("[UltimateSaver] CLEARED on switch: slot=" + oldSlot);
                    }
                }
            }

            // Clear tracking for old weapon
            lastWeaponSlot.remove(statMap);
            lastSignatureEnergy.remove(statMap);
            hasRestored.remove(statMap);
        }
    }

    @Inject(method = "recalculateEntityStatModifiers", at = @At("RETURN"))
    private void onAfterRecalculate(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull EntityStatMap statMap,
            @Nonnull ComponentAccessor<EntityStore> componentAccessor,
            CallbackInfo ci
    ) {
        if (!(EntityUtils.getEntity(ref, componentAccessor) instanceof LivingEntity livingEntity)) {
            return;
        }

        Inventory inventory = livingEntity.getInventory();
        ItemContainer hotbar = inventory.getHotbar();
        byte currentSlot = inventory.getActiveHotbarSlot();

        if (hotbar == null) {
            return;
        }

        ItemStack itemInHand = inventory.getItemInHand();
        if (itemInHand == null || itemInHand.isEmpty()) {
            lastWeaponSlot.remove(statMap);
            lastSignatureEnergy.remove(statMap);
            hasRestored.remove(statMap);
            return;
        }

        // Check if current item is a weapon
        if (itemInHand.getItem().getWeapon() == null) {
            lastWeaponSlot.remove(statMap);
            lastSignatureEnergy.remove(statMap);
            hasRestored.remove(statMap);
            return;
        }

        // We have a weapon in hand
        int energyIndex = DefaultEntityStatTypes.getSignatureEnergy();
        EntityStatValue energyStat = statMap.get(energyIndex);

        if (energyStat == null) {
            return;
        }

        float currentEnergy = energyStat.get();
        float maxEnergy = energyStat.getMax();

        // If maxEnergy is 0, this weapon doesn't have an ultimate
        if (maxEnergy <= 0) {
            lastWeaponSlot.remove(statMap);
            lastSignatureEnergy.remove(statMap);
            hasRestored.remove(statMap);
            return;
        }

        // Detect if this is a new weapon (slot changed or first time)
        Byte prevSlot = lastWeaponSlot.get(statMap);
        boolean isNewWeapon = prevSlot == null || prevSlot.byteValue() != currentSlot;

        if (isNewWeapon) {
            LOGGER.info("[UltimateSaver] New weapon in slot " + currentSlot + ", currentEnergy=" + currentEnergy + ", maxEnergy=" + maxEnergy);
            hasRestored.remove(statMap);
        }

        // RESTORE: Only if energy is 0, we haven't restored yet, and metadata has value
        if (currentEnergy == 0 && maxEnergy > 0 && !Boolean.TRUE.equals(hasRestored.get(statMap))) {
            Float savedEnergy = itemInHand.getFromMetadataOrNull(SIGNATURE_ENERGY_KEY, Codec.FLOAT);

            if (savedEnergy != null && savedEnergy > 0) {
                float restoreValue = Math.min(savedEnergy, maxEnergy);
                statMap.setStatValue(EntityStatMap.Predictable.SELF, energyIndex, restoreValue);
                currentEnergy = restoreValue;
                LOGGER.info("[UltimateSaver] RESTORED energy=" + restoreValue);
            }

            hasRestored.put(statMap, Boolean.TRUE);
        }

        // Track current state for future saves (but don't write to metadata now)
        lastWeaponSlot.put(statMap, currentSlot);
        lastSignatureEnergy.put(statMap, currentEnergy);
    }
}
