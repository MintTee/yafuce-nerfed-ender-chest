package yafuce_nerfed_ender_chest.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import yafuce_nerfed_ender_chest.EnderChestAccessManager;
import yafuce_nerfed_ender_chest.config.ConfigIO;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin {

    @Inject(
            method = "onUse",
            at = @At("HEAD"),
            cancellable = true
    )

    private void handleFreeAccess(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            BlockHitResult hit,
            CallbackInfoReturnable<ActionResult> cir
    ) {

        // Early return conditions to abort mixin and default to Vanilla
        if (player.getAbilities().creativeMode || !ConfigIO.CURRENT.costEchestOpening || !(player instanceof ServerPlayerEntity serverPlayer)) {return;}

        // If Echest disabled altogether totally block them
        if (ConfigIO.CURRENT.disableEchestAltogether) {
                cir.setReturnValue(ActionResult.SUCCESS);
        }

        boolean freeAccess = EnderChestAccessManager.hasFreeAccess(serverPlayer);

        ItemStack handStack = player.getStackInHand(player.getActiveHand());

        if (freeAccess) {
            // Free access: open chest normally, any item
            openVanillaChest(world, pos, player);
            cir.setReturnValue(ActionResult.CONSUME);
        } else {

            String itemId = ConfigIO.CURRENT.openerItem;
            Identifier id = Identifier.tryParse(itemId);

            if (id == null) {
                // invalid config value, fail gracefully
                cir.setReturnValue(ActionResult.FAIL);
                return;
            }

            Item requiredItem = Registries.ITEM.get(id);
            int handStackItemCount = handStack.getCount();

            if (handStack.isOf(requiredItem)
                    && handStackItemCount >= ConfigIO.CURRENT.openerItemCount) {

                handStack.decrement(ConfigIO.CURRENT.openerItemCount);
                openVanillaChest(world, pos, player);
                EnderChestAccessManager.startFreeAccess(serverPlayer); // start new free access period
                cir.setReturnValue(ActionResult.CONSUME);
                player.sendMessage(Text.literal("Ender chest opened for " + ConfigIO.CURRENT.timeTickFreeAccess / 20 / 60 + " minutes").formatted(Formatting.GREEN),true);

                world.playSound(
                        null,
                        pos,
                        SoundEvents.ENTITY_ENDER_EYE_DEATH,
                        SoundCategory.BLOCKS,
                        1f,
                        1f
                );

            } else {
                // Deny opening
                player.sendMessage(Text.literal("Ender chest requires " + ConfigIO.CURRENT.openerItemCount + " ").append(Registries.ITEM.get(Identifier.of(ConfigIO.CURRENT.openerItem)).getName()).append(" to be opened").formatted(Formatting.RED),true);
                world.playSound(
                        null,
                        pos,
                        SoundEvents.BLOCK_ENDER_CHEST_CLOSE,
                        SoundCategory.BLOCKS,
                        1f,
                        1f
                );
                cir.setReturnValue(ActionResult.SUCCESS); // block item default use
            }
        }
    }

    /** Helper method to replicate vanilla chest opening logic */
    @Unique
    private void openVanillaChest(World world, BlockPos pos, PlayerEntity player) {
        EnderChestInventory inventory = player.getEnderChestInventory();
        var blockEntity = world.getBlockEntity(pos);
        if (inventory != null && blockEntity instanceof EnderChestBlockEntity chestEntity) {
            inventory.setActiveBlockEntity(chestEntity);
            player.openHandledScreen(
                    new SimpleNamedScreenHandlerFactory(
                            (i, playerInventory, playerEntity1) ->
                                    GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, inventory),
                            Text.translatable("container.enderchest")
                    )
            );
            player.incrementStat(Stats.OPEN_ENDERCHEST);
        }
    }
}
