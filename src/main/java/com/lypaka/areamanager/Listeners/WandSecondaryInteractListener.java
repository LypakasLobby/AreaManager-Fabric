package com.lypaka.areamanager.Listeners;

import com.lypaka.areamanager.Wand.WandHandler;
import com.lypaka.lypakautils.Handlers.WorldHandlers;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class WandSecondaryInteractListener implements UseBlockCallback {

    @Override
    public ActionResult interact (PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {

        if (!world.isClient && hand == Hand.MAIN_HAND && playerEntity.isCreative() && blockHitResult != null && !playerEntity.getWorld().isClient) {

            ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
            if (!player.isCreative()) return ActionResult.PASS;
            ItemStack item = player.getMainHandStack();
            NbtComponent component = item.get(DataComponentTypes.CUSTOM_DATA);
            try {

                NbtCompound compound = component.copyNbt();
                if (compound.contains("AreaManagerWand")) {

                    int x = blockHitResult.getBlockPos().getX();
                    int y = blockHitResult.getBlockPos().getY();
                    int z = blockHitResult.getBlockPos().getZ();
                    String worldName = WorldHandlers.getWorldName(player);
                    WandHandler.addPosition(player, x, y, z, worldName, 1);
                    return ActionResult.FAIL;

                }

            } catch (NullPointerException e) {

                // do nothing, don't care

            }

        }
        return ActionResult.PASS;

    }

}
