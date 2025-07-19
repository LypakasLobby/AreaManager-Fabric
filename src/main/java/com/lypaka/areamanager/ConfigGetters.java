package com.lypaka.areamanager;

import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.ItemStackHandler;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.lypaka.shadow.google.common.reflect.TypeToken;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class ConfigGetters {

    public static List<String> regionNames;
    public static String wandDisplayName;
    public static String wandID;

    public static void load() throws ObjectMappingException {

        regionNames = new ArrayList<>(AreaManager.configManager.getConfigNode(0, "Regions").getList(TypeToken.of(String.class)));
        if (AreaManager.configManager.getConfigNode(0, "Wand", "Display-Name").isVirtual()) {

            wandDisplayName = "&4Lazy Wand";
            wandID = "minecraft:golden_axe";
            AreaManager.configManager.getConfigNode(0, "Wand", "Display-Name").setValue(wandDisplayName);
            AreaManager.configManager.getConfigNode(0, "Wand", "ID").setValue(wandID);
            AreaManager.configManager.save();

        } else {

            wandDisplayName = AreaManager.configManager.getConfigNode(0, "Wand", "Display-Name").getString();
            wandID = AreaManager.configManager.getConfigNode(0, "Wand", "ID").getString();

        }

    }

    public static ItemStack getWand() {

        ItemStack wand = ItemStackHandler.buildFromStringID(wandID);
        wand.set(DataComponentTypes.CUSTOM_NAME, FancyTextHandler.getFormattedText(wandDisplayName));
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("AreaManagerWand", true);
        wand.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
        return wand;

    }

}
