package com.patrickzhong.kingdomlifeapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class KingdomLifeAPI extends JavaPlugin{
	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "KingdomLifeAPI" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] ";
	static String path = (new File("")).getAbsolutePath()+"/plugins/Skript/variables.csv";
	//public Plugin plugin;
	
	public void onEnable(){
		//plugin = this;
		
		PluginManager pM = getServer().getPluginManager();
		Plugin crates = pM.getPlugin("LootCrates");
		Plugin mech = pM.getPlugin("Mechanics");
		if(crates != null && !crates.isEnabled())
			pM.enablePlugin(crates);
		if(mech != null && !mech.isEnabled())
			pM.enablePlugin(mech);
		
		getLogger().info("KINGDOMLIFE API ENABLED");
		
		
		
		//getServer().getServicesManager().register(KingdomLifeAPI.class, this, plugin, ServicePriority.Highest);
	}
	
	public void onDisable(){
		PluginManager pM = getServer().getPluginManager();
		Plugin crates = pM.getPlugin("LootCrates");
		Plugin mech = pM.getPlugin("Mechanics");
		if(crates != null && crates.isEnabled())
			pM.disablePlugin(crates);
		if(mech != null && mech.isEnabled())
			pM.disablePlugin(mech);
	}
	
	public static String test(String testString){
		return ChatColor.AQUA+"The API works! "+testString;
	}
	
	
	public List<ItemStack> getItems(String type, String rarity, String minLevel){
		BufferedReader br = null;
		String line = "";
		
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			//getLogger().info("FILE NOT FOUND!");
			//getLogger().info("PRINTING ERROR MESSAGE:");
			//getLogger().info(e.getMessage());
			//getLogger().info(e.getCause().toString());
		}
		
		List<ItemStack> listOfItems = new ArrayList<ItemStack>();
		
		try {
			rarity = rarity.toLowerCase();
			String itemType = "";
			if(type.equalsIgnoreCase("Mage"))
				itemType = "STICK";
			else if(type.equalsIgnoreCase("Archer"))
				itemType = "BOW";
			else if(type.equalsIgnoreCase("Rogue"))
				itemType = "LEVER";
			else if(type.equalsIgnoreCase("Warrior"))
				itemType = "STONE_AXE";
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(",");
				String firstEl = arr[0].split("::")[0];
				if(firstEl.equals((rarity+"."+minLevel)) || (rarity.equals("any") && (firstEl.equals("common."+minLevel) || firstEl.equals("uncommon."+minLevel) || firstEl.equals("unique."+minLevel) ||firstEl.equals("rare."+minLevel)))){
					rarity = firstEl.split("\\.")[0];
					getLogger().info(rarity);
					String hexString = arr[2].substring(1);    
			    	byte[] bytes = null;
					try {
						bytes = Hex.decodeHex(hexString.toCharArray());
					} catch (DecoderException e) {
						e.printStackTrace();
						//getLogger().info("DECODER EXCEPTION! What's that?");
					}
					
					String itemString = new String(bytes, "UTF-8");
			    	String[] info = new String[2];
			    	if(itemString.contains(itemType) || (itemType.equals("AXE") && itemString.contains("AXE"))){
			    		String[] infoArr = itemString.split(String.format("%n"));
			    		outerloop:
			    		for(int i = 0; i < infoArr.length; i++){
			    			if(infoArr[i].contains("display-name:")){
								info[0] = infoArr[i].substring(infoArr[i].indexOf("display-name:")+14);
			    			}
			    			else if(infoArr[i].contains("Attack:")){
								info[1] = infoArr[i].substring(infoArr[i].indexOf("Attack:")+8, infoArr[i].length()-1);
			    			}
							
							if(info[0] != null && info[1] != null){
								break outerloop;
							}
			    		}
			    		ItemStack item = new ItemStack(Material.getMaterial(itemType));
			    		ItemMeta im = item.getItemMeta();
			    		List<String> lores = new ArrayList<String>();
			    		lores.add(ChatColor.RED+"\u2694 Attack: "+info[1]);
			    		lores.add(ChatColor.GOLD+"\u2723 Min. Level: "+minLevel);
			    		String color = ChatColor.WHITE+"";
			    		if(rarity.equals("uncommon"))
			    			color = ChatColor.AQUA+"";
			    		else if(rarity.equals("unique"))
			    			color = ChatColor.YELLOW+"";
			    		else if(rarity.equals("rare"))
			    			color = ChatColor.LIGHT_PURPLE+"";
			    		lores.add("");
			    		lores.add(color+(rarity.charAt(0)+"").toUpperCase()+rarity.substring(1)+" Item");
			    		im.setDisplayName(info[0]);
			    		im.setLore(lores);
			    		item.setItemMeta(im);
			    		listOfItems.add(item);
			    	}
			    	
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
			//getLogger().info("IO EXCEPTION, dunno what caused it.");
		}
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return listOfItems;
	}
	
	public int level(String uuid, String type){
		BufferedReader br = null;
		String line = "";
		
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			getLogger().info("FILE NOT FOUND!");
			getLogger().info("PRINTING ERROR MESSAGE:");
			getLogger().info(e.getMessage());
			getLogger().info(e.getCause().toString());
		}
		
		try {
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(",");
				if(arr[0].equals("level."+type+"."+uuid)){
					int level = Integer.parseInt(arr[2].substring(1), 16);
					br.close();
					return level;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("ioexception level");
		}
		
		return -1;
	}
	
	public String type(String uuid){
		BufferedReader br = null;
		String line = "";
		
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			getLogger().info("FILE NOT FOUND!");
			getLogger().info("PRINTING ERROR MESSAGE:");
			getLogger().info(e.getMessage());
			getLogger().info(e.getCause().toString());
		}
		try {
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(",");
				if(arr[0].equals("class."+uuid)){
					String hexString = arr[2].substring(1);    
			    	byte[] bytes = null;
					try {
						bytes = Hex.decodeHex(hexString.toCharArray());
					} catch (DecoderException e) {
						e.printStackTrace();
						//getLogger().info("DECODER EXCEPTION! What's that?");
					}
					
					String classString = new String(bytes, "UTF-8");
					br.close();
					return classString.toLowerCase().substring(2);
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("ioexception type");
		}
		
		return "";
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("apiview")){
			Player player = (Player) sender;
			List<ItemStack> items = getItems(args[0], args[1], args[2]);
			Double size = (double)items.size();
			Inventory inv = Bukkit.createInventory(player, (int)Math.ceil(size/9)*9, "Level "+args[2]+" "+args[1]+" "+args[0]);
	        for(int z = 0; z < items.size(); z++)
	        {
	        	ItemStack i = items.get(z);
	            inv.setItem(z, i);
	        }
	        player.openInventory(inv);
	        return true;
		}else if(cmd.getName().equalsIgnoreCase("apiinfo")){
			String uuid = ((Player) sender).getUniqueId().toString();
			if(args.length > 0){
				Player player = Bukkit.getServer().getPlayer(args[0]);
				if(player == null)
					uuid = Bukkit.getServer().getOfflinePlayer(args[0]).getUniqueId().toString();
				else
					uuid = player.getUniqueId().toString();
				String type = type(uuid);
				((Player)sender).sendMessage(ChatColor.YELLOW+args[1]+ChatColor.GRAY+" is a level "+level(uuid, type)+" "+type);
			}else{
				String type = type(uuid);
				((Player)sender).sendMessage(ChatColor.GRAY+"You are a level "+level(uuid, type)+" "+type);
			}
			return true;
		}
		return false;
	}
	
}