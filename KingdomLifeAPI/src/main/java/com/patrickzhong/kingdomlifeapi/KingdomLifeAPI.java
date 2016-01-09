package com.patrickzhong.kingdomlifeapi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class KingdomLifeAPI extends JavaPlugin{
	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "KingdomLifeAPI" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] ";
	private static File dataFile;
	public static FileConfiguration data;
	
	public void onEnable(){
		try{
            if(!getDataFolder().exists())getDataFolder().mkdir();
            dataFile = new File(getDataFolder(), "data.yml");
            if (!dataFile.exists())dataFile.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
		
		getLogger().info("KingdomLifeAPI enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("apiadd")){
			Player player = (Player) sender;
			ItemStack item = player.getInventory().getItemInHand();
			if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()){
				messageP(player, ChatColor.RED+"You must be holding the item in your hand!");
				return true;
			}
			
			String minLevel = "";
			String rarity = "";
			String type = "";
			
			if(item.getType().equals(Material.STICK))
				type = "Mage";
			else if(item.getType().equals(Material.BOW))
				type = "Archer";
			else if(item.getType().equals(Material.DIAMOND_SPADE) || item.getType().equals(Material.GOLD_SPADE) || item.getType().equals(Material.IRON_SPADE) || item.getType().equals(Material.STONE_SPADE) || item.getType().equals(Material.WOOD_SPADE))
				type = "Rogue";
			
			List<String> lore = item.getItemMeta().getLore();
			for(int i = 0; i < lore.size(); i++){
				String line = ChatColor.stripColor(lore.get(i));
				if(line.contains("Level")){
					String[] arr = line.split(" ");
					minLevel = arr[arr.length-1];
				}else if(line.contains("Item")){
					String[] arr = line.split(" ");
					rarity = arr[0];
				}
			}
			
			refreshD();
			
			List<ItemStack> items = (List<ItemStack>)data.getList(type+"."+rarity+"."+minLevel);
			if(items == null)
				items = new ArrayList<ItemStack>();
			items.add(item);
			data.set(type+"."+rarity+"."+minLevel, items);
			saveD();
			
			messageP(player, ChatColor.GRAY+"Successfully added a "+ChatColor.YELLOW+"Level "+minLevel+" "+rarity+" "+type+"'s "+item.getItemMeta().getDisplayName()+ChatColor.GRAY+" to the API.");
			
			return true;
		}
		
		return false;
	}
	
	private void messageP(Player player, String message){
		player.sendMessage(prefix+message);
	}
	
	private void refreshD(){
		data = YamlConfiguration.loadConfiguration(dataFile);
	}
	
	private void saveD(){
		try {
			data.save(dataFile);
		} catch(IOException e) {
			  e.printStackTrace();
		}
	}
	
	public List<ItemStack> getItems(String type, String rarity, String minLevel){
		refreshD();
		return (List<ItemStack>)data.getList(type+"."+rarity+"."+minLevel);
	}
}
