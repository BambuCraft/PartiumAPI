package de.abq.partium.common.item;

import de.abq.partium.Partium;
import de.abq.partium.common.data_components.ELDataComponents;
import de.abq.partium.common.data_components.PartsComponents;
import de.abq.partium.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ZItems {

    private static final Map<ResourceLocation, Item> ITEMS = new LinkedHashMap<>();

    private static final Item.Properties DEFAULT_PROPERTIES = Services.PLATFORM.defaultItemBuilder().rarity(Rarity.EPIC).stacksTo(1).component(ELDataComponents.SWORD_PARTS, PartsComponents.DEFAULT);

    public static final Item SWORD = build("sword", new PartiumSwordItem(Tiers.NETHERITE, DEFAULT_PROPERTIES));
    public static final Item PICKAXE = build("pickaxe", new PartiumPickaxeItem(Tiers.NETHERITE, DEFAULT_PROPERTIES));
    public static final Item AXE = build("axe", new PartiumAxeItem(Tiers.NETHERITE, DEFAULT_PROPERTIES));
    public static final Item HOE = build("hoe", new PartiumHoeItem(Tiers.NETHERITE, DEFAULT_PROPERTIES));
    public static final Item SHOVEL = build("shovel", new PartiumShovelItem(Tiers.NETHERITE, DEFAULT_PROPERTIES));

    public static final Item TRIDENT = new PartiumTridentItem(DEFAULT_PROPERTIES);
    public static final Item MACE = new PartiumMaceItem(DEFAULT_PROPERTIES);

    public static final Item BOW = new PartiumBowItem(DEFAULT_PROPERTIES);
    public static final Item CROSSBOW = new PartiumCrossbowItem(DEFAULT_PROPERTIES);

    private static Item build(String id, Item item){
        ITEMS.put(Partium.path(id), item);
        return item;
    }

    public static void registerItems(BiConsumer<Item, ResourceLocation> register) {
        register.accept(SWORD, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "sword"));

        //NEEDS WORK
        register.accept(PICKAXE, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "pickaxe"));
        register.accept(AXE, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "axe"));
        register.accept(HOE, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "hoe"));
        register.accept(SHOVEL, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "shovel"));
        register.accept(TRIDENT, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "trident"));
        register.accept(MACE, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "mace"));
        register.accept(BOW, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "bow"));
        register.accept(CROSSBOW, ResourceLocation.fromNamespaceAndPath(Partium.MOD_ID, "crossbow"));
    }
}