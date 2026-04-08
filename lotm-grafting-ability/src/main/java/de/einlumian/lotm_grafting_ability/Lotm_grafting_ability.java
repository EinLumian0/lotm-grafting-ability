package de.einlumian.lotm_grafting_ability;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Lotm_grafting_ability extends JavaPlugin {

    private GraftingItemManager itemManager;

    @Override
    public void onEnable() {
        itemManager = new GraftingItemManager(this);
        getServer().getPluginManager().registerEvents(new Graftinglistener(this, itemManager), this);
        getLogger().info("GraftingPlugin enabled — the path of the Grafting ability opens.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GraftingPlugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("grafting")) return false;

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("grafting.give")) {
            player.sendMessage("§5[Grafting] §cYou do not have permission to use this.");
            return true;
        }
        if (args.length == 0 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("§5[Grafting] §fUsage: §d/grafting give <type>");
            sender.sendMessage("§7Types: distance, gravity, pressure, sun, space");
            return true;
        }

        String type = args.length >= 2 ? args[1].toLowerCase() : "distance";

        if (type.equals("distance")) {
            player.getInventory().addItem(itemManager.createGraftingItem());
            player.sendMessage("§5[Grafting] §fYou received §dThe power of distance grafting!§f.");
        } else if (type.equals("gravity")) {
            player.getInventory().addItem(itemManager.createGravityGrafting());
            player.sendMessage("§5[Grafting] §fYou received §dThe power of gravity grafting!§");
        } else if (type.equals("pressure")) {
        player.getInventory().addItem(itemManager.createPressureGrafting());
        player.sendMessage("§5[Grafting] §fYou received §dThe power of pressure grafting!§");
        }else if (type.equals("sun")) {
            player.getInventory().addItem(itemManager.createSunGrafting());
            player.sendMessage("§5[Grafting] §fYou received §dThe power to graft a flare of the sun!§");
        }else if (type.equals("space")) {
            player.getInventory().addItem(itemManager.createSpaceGrafting());
            player.sendMessage("§5[Grafting] §fYou received §dThe power of space grafting!§");
        }

        return true;
        }

    public GraftingItemManager getItemManager() {
        return itemManager;
    }
}
