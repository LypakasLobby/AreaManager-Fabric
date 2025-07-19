package com.lypaka.areamanager.Listeners;

import com.lypaka.areamanager.Wand.WandHandler;
import com.lypaka.lypakautils.Handlers.WorldHandlers;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WandPrimaryInteractListener implements AttackBlockCallback {

    @Override
    public ActionResult interact (PlayerEntity playerEntity, World world, Hand hand, BlockPos pos, Direction direction) {

        if (!world.isClient && hand == Hand.MAIN_HAND && playerEntity.isCreative() && !playerEntity.getWorld().isClient) {

            ServerPlayerEntity player = (ServerPlayerEntity) playerEntity;
            if (!player.isCreative()) return ActionResult.PASS;
            ItemStack item = player.getMainHandStack();
            NbtComponent component = item.get(DataComponentTypes.CUSTOM_DATA);
            try {

                NbtCompound compound = component.copyNbt();
                if (compound.contains("AreaManagerWand")) {

                    if (player.isSneaking()) {

                        WandHandler.clearPositions(player);

                    } else {

                        int x = pos.getX();
                        int y = pos.getY();
                        int z = pos.getZ();
                        String worldName = WorldHandlers.getWorldName(player);
                        WandHandler.addPosition(player, x, y, z, worldName, 1);
                        return ActionResult.FAIL;

                    }

                }

            } catch (NullPointerException e) {

                // do nothing, don't care

            }

        }
        return ActionResult.PASS;

    }

}
