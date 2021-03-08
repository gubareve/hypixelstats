package dev.evang.hypixelstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.api.HypixelAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class hypixelstats extends JavaPlugin {

    public static final HypixelAPI API = new HypixelAPI(UUID.fromString("61150581-d6e6-409a-9360-7e861d785713"));

    private static String getFieldOrNA(String field, JsonObject json) {
        JsonElement value = json.get(field);
        if (value != null) {
            // If the field was found, return its value
            return value.getAsString();
        } else {
            // Otherwise, return "N/A"
            return "N/A";
        }
    }

    @Override
    public void onEnable() {
        getLogger().info("Thank you for enabling the Hypixel Stats plugin.");
    }
    @Override
    public void onDisable() {
        getLogger().info("Sorry to see you go!");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("stats")) { // If the player typed /basic then do the following, note: If you only registered this executor for one command, you don't need this
            Player in_game_player = (Player) sender;
            try {
                String test = args[0];
            } catch(Exception e) {
                sender.sendMessage(ChatColor.GREEN + "Usage: /stats " + ChatColor.GOLD + "[player name]");
                return true;
            }
            API.getPlayerByName(args[0]).whenComplete((response, error) -> {
                if (error != null) {
                    sender.sendMessage(ChatColor.RED + "An error occurred");
                }
                JsonObject player = response.getPlayer();
                if (player != null) {
                    try {
                        if (args.length == 1) {
                            sender.sendMessage(ChatColor.YELLOW + "Stats for " + ChatColor.GOLD + getFieldOrNA("displayname", player) + ":\n" +
                                    ChatColor.YELLOW + "Rank: " + ChatColor.GOLD + getFieldOrNA("newPackageRank", player) + "\n" +
                                    ChatColor.YELLOW + "Level: " + ChatColor.GOLD + Math.round((Math.sqrt((2 * Double.parseDouble(getFieldOrNA("networkExp", player))) + 30625) / 50) - 2.5) + "\n" +
                                    ChatColor.YELLOW + "Most recent game: " + ChatColor.GOLD + getFieldOrNA("mostRecentGameType", player) + "\n" +
                                    ChatColor.YELLOW + "First join: " + ChatColor.GOLD + Instant.ofEpochSecond(Long.parseLong(getFieldOrNA("firstLogin", player)) / 1000).atZone(ZoneId.of("GMT-4")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                                    ChatColor.YELLOW + "Last join: " + ChatColor.GOLD + Instant.ofEpochSecond(Long.parseLong(getFieldOrNA("lastLogin", player)) / 1000).atZone(ZoneId.of("GMT-4")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                                    ChatColor.YELLOW + "Most used mc version: " + ChatColor.GOLD + getFieldOrNA("mcVersionRp", player)
                            );
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Usage: /stats hypixel");
                        }
                    } catch(Exception e) {
                        sender.sendMessage(ChatColor.RED + "Sorry, an error occurred");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Could not find player. typo?");
                }
            });
            return true;
        } //If this has happened the function will return true.
        // If this hasn't happened the value of false will be returned.
        return false;
    }
}
