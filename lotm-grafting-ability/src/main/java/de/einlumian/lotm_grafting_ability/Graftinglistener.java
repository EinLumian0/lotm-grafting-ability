package de.einlumian.lotm_grafting_ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.Material;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Graftinglistener implements Listener {

    private final Lotm_grafting_ability plugin;
    private final GraftingItemManager itemManager;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 2_000L;

    public Graftinglistener(Lotm_grafting_ability plugin, GraftingItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // ── Space Grafting ────────────────────────────────
        if (itemManager.isSpaceGrafting(item)) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            event.setCancelled(true);

            player.openInventory(player.getEnderChest());

            // Sound + particle effects
            Location loc = player.getLocation().clone().add(0, 1, 0);
            player.getWorld().playSound(loc, Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
            Location center = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(2));
            var world = player.getWorld();

            new BukkitRunnable() {
                double size = 1.5; // box size
                double progress = 0; // how much of the box is built

                @Override
                public void run() {
                    progress += 0.1;

                    if (progress >= 1.0) {
                        this.cancel();
                        return;
                    }

                    int points = 40;

                    for (int i = 0; i < points * progress; i++) {
                        double t = i / (double) points;

                        // 12 edges of a cube
                        drawEdge(world, center, -size, -size, -size, size, -size, -size, t); // bottom front
                        drawEdge(world, center, size, -size, -size, size, -size, size, t);
                        drawEdge(world, center, size, -size, size, -size, -size, size, t);
                        drawEdge(world, center, -size, -size, size, -size, -size, -size, t);

                        drawEdge(world, center, -size, size, -size, size, size, -size, t); // top front
                        drawEdge(world, center, size, size, -size, size, size, size, t);
                        drawEdge(world, center, size, size, size, -size, size, size, t);
                        drawEdge(world, center, -size, size, size, -size, size, -size, t);

                        drawEdge(world, center, -size, -size, -size, -size, size, -size, t); // verticals
                        drawEdge(world, center, size, -size, -size, size, size, -size, t);
                        drawEdge(world, center, size, -size, size, size, size, size, t);
                        drawEdge(world, center, -size, -size, size, -size, size, size, t);
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);

            player.sendMessage("§5§fGrafted space onto yourself.");
            return;
        }

        // ── Sun Grafting ────────────────────────────────
        if (itemManager.isSunGrafting(item)) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            event.setCancelled(true);

            World world = player.getWorld();
            long originalTime = world.getTime();


            world.setTime(6000);

            double radius = 100.0;
            for (var entity : world.getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (entity instanceof LivingEntity living && !(entity instanceof Player) && !(living instanceof ArmorStand)) {

                    //Particle effect
                    Location center = living.getLocation().clone();
                    World plugin = center.getWorld();

                    double height = 10;       // how high the pillar starts
                    int steps = 18;          // how smooth the descent is
                    double pillarRadius = 0.5;

                    new BukkitRunnable() {
                        int step = 0;

                        @Override
                        public void run() {
                            if (step >= steps) {

                                // ── IMPACT ──
                                plugin.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.6f);
                                plugin.spawnParticle(Particle.EXPLOSION_HUGE, center, 1);

                                living.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                                living.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 5));
                                living.setFireTicks(1000);

                                // ── GROUND SPREAD ──
                                new BukkitRunnable() {
                                    double currentRadius = 0;
                                    final double maxRadius = 6;

                                    @Override
                                    public void run() {
                                        if (currentRadius >= maxRadius) {
                                            cancel();
                                            return;
                                        }

                                        int points = 40;
                                        for (int i = 0; i < points; i++) {
                                            double angle = 2 * Math.PI * i / points;
                                            double x = Math.cos(angle) * currentRadius;
                                            double z = Math.sin(angle) * currentRadius;

                                            Location loc = center.clone().add(x, 0.1, z);

                                            plugin.spawnParticle(Particle.FALLING_DRIPSTONE_LAVA, loc, 2, 0, 0, 0, 0);
                                            plugin.spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                                        }

                                        currentRadius += 0.6;
                                    }
                                }.runTaskTimer(Graftinglistener.this.plugin, 0L, 1L);

                                cancel();
                                return;
                            }

                            // ── DESCENDING CYLINDER ──
                            double y = center.getY() + height - (step * (height / steps));

                            int points = 12;
                            for (int i = 0; i < points; i++) {
                                double angle = 2 * Math.PI * i / points;
                                double x = Math.cos(angle) * pillarRadius;
                                double z = Math.sin(angle) * pillarRadius;

                                Location loc = new Location(plugin, center.getX() + x, y, center.getZ() + z);

                                plugin.spawnParticle(Particle.FALLING_DRIPSTONE_LAVA, loc, 2, 0, 0, 0, 0);
                                plugin.spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                            }

                            // center beam (makes it feel solid)
                            plugin.spawnParticle(Particle.FLAME, new Location(plugin, center.getX(), y, center.getZ()), 3, 0, 0, 0, 0);

                            step++;
                        }
                    }.runTaskTimer(this.plugin, 0L, 1L);
                }
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> world.setTime(originalTime), 10L);

            player.sendMessage("§5§fGrafted a flare of the sun onto the surrounding area!");
            return; // item is not consumed
        }

        // ── Pressure Grafting ────────────────────────────────
        if (itemManager.isPressureGrafting(item)) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            event.setCancelled(true);

            double radius = 100.0;
            for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (entity instanceof LivingEntity living && !(entity instanceof Player) && !(living instanceof ArmorStand)) {

                    //particle effect
                    Location center = living.getLocation().clone();
                    World world = center.getWorld();

                    double height = 6;       // height of the circle in the sky
                    double circleRadius = 2; // size of the sky circle

                    new BukkitRunnable() {
                        double progress = 0; // goes from 0 → 2PI

                        @Override
                        public void run() {

                            // ── PHASE 1: DRAW CIRCLE ──
                            if (progress < Math.PI * 2) {
                                int points = 8; // how many points per tick

                                for (int i = 0; i < points; i++) {
                                    double angle = progress + (i * 0.2);
                                    double x = Math.cos(angle) * circleRadius;
                                    double z = Math.sin(angle) * circleRadius;

                                    Location loc = center.clone().add(x, height, z);

                                    world.spawnParticle(Particle.PORTAL, loc, 2, 0, 0, 0, 0);
                                    world.spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0, 0);
                                }

                                progress += 0.25; // speed of circle forming
                                return;
                            }

                            // ── PHASE 2: SMALL DELAY ──
                            if (progress < Math.PI * 2 + 5) {
                                progress++;
                                return;
                            }

                            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12000, 1000));
                            living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 12000, 10));
                            living.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 12000, 3));

                            // ── PHASE 3: PRESSURE LINES ──
                            int lines = 12;

                            for (int i = 0; i < lines; i++) {
                                double angle = 2 * Math.PI * i / lines;
                                double x = Math.cos(angle) * circleRadius;
                                double z = Math.sin(angle) * circleRadius;

                                // vertical beam
                                for (double y = height; y >= 0; y -= 0.5) {
                                    Location beamLoc = center.clone().add(x, y, z);

                                    world.spawnParticle(Particle.END_ROD, beamLoc, 1, 0, 0, 0, 0);
                                    world.spawnParticle(Particle.SMOKE_NORMAL, beamLoc, 0, 0, 0, 0, 0);
                                }
                            }

                            // center hit effect
                            world.spawnParticle(Particle.EXPLOSION_NORMAL, center, 10, 0.3, 0.3, 0.3, 0.05);
                            world.playSound(center, Sound.ENTITY_ENDERMAN_STARE, 1f, 0.5f);

                            cancel();
                        }

                    }.runTaskTimer(plugin, 0L, 1L);
                }
            }

            player.sendMessage("§5§fGrafted the pressure of the ocean to every mob!");
            return; // item is not consumed
        }

        // ── Gravity Grafting ────────────────────────────────
        if (itemManager.isGravityGrafting(item)) {
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            event.setCancelled(true);

            double radius = 100.0;
            for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (entity instanceof LivingEntity living && !(entity instanceof Player) && !(living instanceof ArmorStand)) {
                    living.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 12000, 1));

                    //particle effect
                    Location loc = living.getLocation().clone().add(0, 1, 0);
                    for (int i = 0; i < 10; i++) {
                        double angle = i * Math.PI * 2 / 10;
                        double x = Math.cos(angle) * 0.5;
                        double z = Math.sin(angle) * 0.5;
                        player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.clone().add(x, 0, z), 1, 0, 0, 0, 0);
                    }
                }
            }

            player.sendMessage("§5§fGrafted the gravity of space to all nearby mobs!");
            return; // item is not consumed
        }

        // ── Distance Grafting ─────────────────────────────
        if (!itemManager.isGraftingItem(item)) return;
        Action action = event.getAction();

        if ((action == Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
            event.setCancelled(true);
            Block clicked = event.getClickedBlock();
            Location blockLoc = clicked.getLocation();
            int mode = itemManager.getMode(item);
            itemManager.setPosition(item, mode, blockLoc);
            player.getInventory().setItemInMainHand(item);

            if (mode == 0) {
                removeMarkers(blockLoc.getWorld(), "pos1_marker");
                spawnGlowMarker(blockLoc, true);

                player.sendMessage("§aPosition 1 grafted at §7" + formatCoords(blockLoc) + "§f.");
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1.4f);
                blockLoc.getWorld().spawnParticle(Particle.SPELL_WITCH, blockLoc.clone().add(0.5, 0.8, 0.5), 25, 0.3, 0.2, 0.3, 0);
            } else {
                removeMarkers(blockLoc.getWorld(), "pos2_marker");
                spawnGlowMarker(blockLoc, false);

                player.sendMessage("§bPosition 2 grafted at §7" + formatCoords(blockLoc) + "§f.");
                player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 0.8f);
                blockLoc.getWorld().spawnParticle(Particle.SPELL_WITCH, blockLoc.clone().add(0.5, 0.8, 0.5), 40, 0.3, 0.4, 0.3, 0.5);
            }
        } else if (action == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);
            int newMode = itemManager.toggleMode(item);
            player.getInventory().setItemInMainHand(item);
            String modeStr = (newMode == 0) ? "§aPosition 1" : "§bPosition 2";
            player.sendMessage("§5[Grafting] §fNow selecting: " + modeStr + "§f.");
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, newMode == 0 ? 1.6f : 0.7f);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§5§lGrafting Abilities");
        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        player.sendMessage("§5/grafting give space §7→ Unlocks space grafting");
        player.sendMessage("§6/grafting give sun §7→ Unlocks sun grafting");
        player.sendMessage("§3/grafting give pressure §7→ Unlocks pressure grafting");
        player.sendMessage("§d/grafting give gravity §7→ Unlocks gravity grafting");

        player.sendMessage("§a/grafting give distance §7→ Unlocks distance grafting");
        player.sendMessage("§7Right-click block §8→ Set position");
        player.sendMessage("§7Right-click air §8→ Change mode");
        player.sendMessage("§7Step on block while holding the item §8→ Teleport");

        player.sendMessage("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;

        Player player = event.getPlayer();
        UUID uid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = cooldowns.get(uid);
        if (last != null && now - last < COOLDOWN_MS) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!itemManager.isGraftingItem(item)) return;

        Location pos1 = itemManager.getPos1(item);
        Location pos2 = itemManager.getPos2(item);
        if (pos1 == null || pos2 == null) return;

        Block standingOn = to.getBlock().getRelative(BlockFace.DOWN);

        if (isSameBlock(standingOn.getLocation(), pos1)) {
            cooldowns.put(uid, now);
            teleport(player, pos2, "§bPosition 2");
        } else if (isSameBlock(standingOn.getLocation(), pos2)) {
            cooldowns.put(uid, now);
            teleport(player, pos1, "§aPosition 1");
        }
    }

    private void teleport(Player player, Location dest, String destName) {
        Location finalDest = dest.clone();
        finalDest.setYaw(player.getLocation().getYaw());
        finalDest.setPitch(player.getLocation().getPitch());

        Location from = player.getLocation();
        from.getWorld().spawnParticle(Particle.PORTAL, from.clone().add(0, 1, 0), 60, 0.3, 0.6, 0.3, 0.1);
        player.getWorld().playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

        player.teleport(finalDest);

        finalDest.getWorld().spawnParticle(Particle.PORTAL, finalDest.clone().add(0, 1, 0), 60, 0.3, 0.6, 0.3, 0.1);
        player.getWorld().playSound(finalDest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.3f);

        player.sendMessage("§5[Grafting] §fGrafted to " + destName + "§f.");
    }

    private boolean isSameBlock(Location blockLoc, Location storedDest) {
        if (blockLoc.getWorld() == null || storedDest.getWorld() == null) return false;
        if (!blockLoc.getWorld().equals(storedDest.getWorld())) return false;
        return blockLoc.getBlockX() == storedDest.getBlockX() &&
                blockLoc.getBlockY() == storedDest.getBlockY() - 1 &&
                blockLoc.getBlockZ() == storedDest.getBlockZ();
    }

    private String formatCoords(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    // ── HELPER METHOD: Draw a line for particle cube ──
    private void drawEdge(World world, Location center,
                          double x1, double y1, double z1,
                          double x2, double y2, double z2,
                          double t) {

        double x = x1 + (x2 - x1) * t;
        double y = y1 + (y2 - y1) * t;
        double z = z1 + (z2 - z1) * t;

        Location point = center.clone().add(x, y, z);
        world.spawnParticle(Particle.PORTAL, point, 1, 0, 0, 0, 0);
    }

    // ── HELPER METHOD: Glow for positions ──
    private void spawnGlowMarker(Location loc, boolean isPos1) {
        World world = loc.getWorld();
        if (world == null) return;

        Location center = loc.clone().add(0.5, 0, 0.5);

        ArmorStand stand = world.spawn(center, ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setMarker(true);
            a.getEquipment().setHelmet(new ItemStack(Material.GLASS));
            a.setGravity(false);
            a.setSmall(true);
            a.setInvulnerable(true);
            a.setSilent(true);

            a.setGlowing(true);

            a.setCustomName(isPos1 ? "pos1_marker" : "pos2_marker");
            a.setCustomNameVisible(false);
        });

        Team team;
        if (isPos1) {
            team = getOrCreateTeam(world, "graft_pos1", ChatColor.GREEN);
        } else {
            team = getOrCreateTeam(world, "graft_pos2", ChatColor.BLUE);
        }

        team.addEntry(stand.getUniqueId().toString());
    }
    private Team getOrCreateTeam(World world, String name, ChatColor color) {
        Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();

        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
            team.setColor(color);
        }

        return team;
    }

    private void removeMarkers(World world, String name) {
        for (Entity e : world.getEntities()) {
            if (e instanceof ArmorStand stand && name.equals(stand.getCustomName())) {
                stand.remove();
            }
        }
    }
}