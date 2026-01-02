package yafuce_nerfed_ender_chest.mixin;

import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yafuce_nerfed_ender_chest.config.ConfigIO;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Inject(
            method = "canInsert(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockConfiguredItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot self = (Slot) (Object) this;

        if (!(self.inventory instanceof EnderChestInventory)) return;

        try {
            Identifier id = Registries.ITEM.getId(stack.getItem());
            String itemId = id.toString();

            // Compare against the config list
            if (ConfigIO.CURRENT.blockedEchestItems.contains(itemId)) {
                cir.setReturnValue(false);
            }
        } catch (Exception ignored) {
            // Ignore any invalid or null items
        }
    }
}
