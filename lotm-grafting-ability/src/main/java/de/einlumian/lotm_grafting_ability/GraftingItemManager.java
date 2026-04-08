package de.einlumian.lotm_grafting_ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class GraftingItemManager {

    public final NamespacedKey KEY_IS_GRAFTING;
    public final NamespacedKey KEY_MODE;
    public final NamespacedKey KEY_POS1;
    public final NamespacedKey KEY_POS2;
    public final NamespacedKey KEY_GRAVITY_GRAFTING;
    public final NamespacedKey KEY_PRESSURE_GRAFTING;
    public final NamespacedKey KEY_SUN_GRAFTING;
    public final NamespacedKey KEY_SPACE_GRAFTING;

    private final Lotm_grafting_ability plugin;

    public GraftingItemManager(Lotm_grafting_ability plugin) {
        this.plugin = plugin;
        KEY_IS_GRAFTING = new NamespacedKey(plugin, "grafting_item");
        KEY_MODE = new NamespacedKey(plugin, "grafting_mode");
        KEY_POS1 = new NamespacedKey(plugin, "grafting_pos1");
        KEY_POS2 = new NamespacedKey(plugin, "grafting_pos2");
        KEY_GRAVITY_GRAFTING = new NamespacedKey(plugin, "gravity_grafting");
        KEY_PRESSURE_GRAFTING = new NamespacedKey(plugin, "pressure_grafting");
        KEY_SUN_GRAFTING = new NamespacedKey(plugin, "sun_grafting");
        KEY_SPACE_GRAFTING = new NamespacedKey(plugin, "space_grafting");
    }

    // ── Space Grafting Item ────────────────────────────────────────────────
    public ItemStack createSpaceGrafting() {
        ItemStack item = new ItemStack(Material.SHULKER_SHELL);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§5§lSpace Grafting");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Arrays.asList(
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§fRight-click to access your",
                "§5Ender Chest §ffrom anywhere.",
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_SPACE_GRAFTING, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isSpaceGrafting(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEY_SPACE_GRAFTING, PersistentDataType.BOOLEAN);
    }

    // ── Sun Grafting Item ────────────────────────────────────────────────
    public ItemStack createSunGrafting() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§5§lSun Grafting");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Arrays.asList(
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§fRight-click to graft a flare of the sun",
                "§7on all nearby mobs (except you).",
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_SUN_GRAFTING, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isSunGrafting(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEY_SUN_GRAFTING, PersistentDataType.BOOLEAN);
    }

    // ── Distance Grafting Item ────────────────────────────────────────────────
    public ItemStack createGraftingItem() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§5§lDistance Grafting");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(buildLore(0, null, null));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_IS_GRAFTING, PersistentDataType.BOOLEAN, true);
        pdc.set(KEY_MODE, PersistentDataType.INTEGER, 0);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isGraftingItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEY_IS_GRAFTING, PersistentDataType.BOOLEAN);
    }

    // ── Pressure Grafting Item ────────────────────────────────────────────────

    public ItemStack createPressureGrafting() {
    ItemStack item = new ItemStack(Material.BUCKET);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§5§lPressure Grafting");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Arrays.asList(
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§fRight-click to graft the pressure of the ocean",
                "§7on all nearby mobs (except you).",
                "§fDuration: 10 minutes",
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_PRESSURE_GRAFTING, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isPressureGrafting(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEY_PRESSURE_GRAFTING, PersistentDataType.BOOLEAN);
    }

    // ── Gravity Grafting Item ────────────────────────────────────────────────
    public ItemStack createGravityGrafting() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§5§lGravity Grafting");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(Arrays.asList(
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§fRight-click to cast levitation",
                "§7on all nearby mobs (except you).",
                "§fDuration: 10 minutes",
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_GRAVITY_GRAFTING, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isGravityGrafting(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(KEY_GRAVITY_GRAFTING, PersistentDataType.BOOLEAN);
    }

    // ── Mode & Positions for Distance Grafting ──────────────────────────────────────────────────────
    public int getMode(ItemStack item) {
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        Integer mode = pdc.get(KEY_MODE, PersistentDataType.INTEGER);
        return mode != null ? mode : 0;
    }

    public int toggleMode(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        int newMode = (pdc.getOrDefault(KEY_MODE, PersistentDataType.INTEGER, 0) == 0) ? 1 : 0;
        pdc.set(KEY_MODE, PersistentDataType.INTEGER, newMode);

        meta.setLore(buildLore(newMode, decodeLocation(pdc.get(KEY_POS1, PersistentDataType.STRING)),
                decodeLocation(pdc.get(KEY_POS2, PersistentDataType.STRING))));
        item.setItemMeta(meta);
        return newMode;
    }

    public void setPosition(ItemStack item, int posIndex, Location blockLoc) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        NamespacedKey targetKey = (posIndex == 0) ? KEY_POS1 : KEY_POS2;
        NamespacedKey otherKey = (posIndex == 0) ? KEY_POS2 : KEY_POS1;

        String encoded = encodeLocation(blockLoc);
        pdc.set(targetKey, PersistentDataType.STRING, encoded);

        int mode = pdc.getOrDefault(KEY_MODE, PersistentDataType.INTEGER, 0);
        Location otherLoc = decodeLocation(pdc.get(otherKey, PersistentDataType.STRING));

        Location pos1 = (posIndex == 0) ? blockLoc : otherLoc;
        Location pos2 = (posIndex == 1) ? blockLoc : otherLoc;
        meta.setLore(buildLore(mode, pos1, pos2));
        item.setItemMeta(meta);
    }

    public Location getPos1(ItemStack item) {
        return decodeLocation(item.getItemMeta().getPersistentDataContainer().get(KEY_POS1, PersistentDataType.STRING));
    }

    public Location getPos2(ItemStack item) {
        return decodeLocation(item.getItemMeta().getPersistentDataContainer().get(KEY_POS2, PersistentDataType.STRING));
    }

    private String encodeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location decodeLocation(String encoded) {
        if (encoded == null) return null;
        String[] parts = encoded.split(",");
        if (parts.length != 4) return null;
        World world = plugin.getServer().getWorld(parts[0]);
        if (world == null) return null;
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);
        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }

    private List<String> buildLore(int mode, Location pos1, Location pos2) {
        String modeLabel = (mode == 0) ? "§a§lPosition 1 §r§7(right-click block)" : "§b§lPosition 2 §r§7(right-click block)";
        String pos1Label = (pos1 == null) ? "§8  Not set" : "§7  " + formatCoords(pos1);
        String pos2Label = (pos2 == null) ? "§8  Not set" : "§7  " + formatCoords(pos2);

        return Arrays.asList(
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§fCurrently selecting:",
                "  " + modeLabel,
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§aPosition 1:",
                pos1Label,
                "§bPosition 2:",
                pos2Label,
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§7Right-click §fblock  §8— graft position",
                "§7Right-click §fair   §8— switch mode"
        );
    }

    private String formatCoords(Location loc) {
        return (int) (loc.getX() - 0.5) + ", " + (loc.getBlockY() - 1) + ", " + (int) (loc.getZ() - 0.5);
    }
}