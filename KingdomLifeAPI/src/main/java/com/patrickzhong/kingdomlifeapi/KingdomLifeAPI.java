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
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.apache.commons.io.input.ReversedLinesFileReader;

import com.rylinaux.plugman.util.PluginUtil;

public class KingdomLifeAPI extends JavaPlugin{
	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "KingdomLifeAPI" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] ";
	static String path = (new File("")).getAbsolutePath()+"/plugins/Skript/variables.csv";
	String[] plugins = {"LootCrates", "Mechanics", "Abilities", "AbilityPackage"};
	//public Plugin plugin;
	
	public void onEnable(){
		//plugin = this;
		
		/*PluginManager pM = getServer().getPluginManager();
		for(String s : plugins){
			Plugin p = pM.getPlugin(s);
			if(p != null)
				PluginUtil.reload(p);
		}*/
		
		getLogger().info("KINGDOMLIFE API ENABLED");
	}
	
	public void onDisable(){
		PluginManager pM = getServer().getPluginManager();
		for(String s : plugins){
			Plugin p = pM.getPlugin(s);
			if(p != null && p.isEnabled())
				PluginUtil.disable(p);
		}
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
		}
		
		List<ItemStack> listOfItems = new ArrayList<ItemStack>();
		
		try {
			rarity = rarity.toLowerCase();
			String lookedFor = rarity;
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
				if(line.contains(",") && line.contains("::")){
					String identifier = line.substring(0, line.indexOf(","));
					String firstEl = identifier.substring(0, identifier.indexOf("::"));
					if(firstEl.equals(lookedFor+"."+minLevel) || (lookedFor.equals("any") && (firstEl.equals("common."+minLevel) || firstEl.equals("uncommon."+minLevel) || firstEl.equals("unique."+minLevel) || firstEl.equals("rare."+minLevel)))){
						rarity = firstEl.substring(0, firstEl.indexOf("."));
						String hexString = line.substring(line.lastIndexOf(",")+2);    
				    	byte[] bytes = null;
						try {
							bytes = Hex.decodeHex(hexString.toCharArray());
						} catch (DecoderException e) {
							e.printStackTrace();
						}
						
						String itemString = new String(bytes, "UTF-8");
				    	String[] info = new String[2];
				    	if(itemString.contains(itemType)){
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
				    		if(rarity.equals("unique"))
				    			color = ChatColor.YELLOW+"";
				    		else if(rarity.equals("rare"))
				    			color = ChatColor.GREEN+"";
				    		else if(rarity.equals("legendary"))
				    			color = ChatColor.AQUA+"";
				    		else if(rarity.equals("mythical"))
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
			}
		} catch (IOException e) {
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
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
				if(line.contains(",")){
					String identifier = line.substring(0, line.indexOf(","));
					if(identifier.equals("level."+type+"."+uuid)){
						int level = Integer.parseInt(line.substring(line.lastIndexOf(",")+2), 16);
						br.close();
						return level;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("ioexception level");
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		
		
		return -1;
	}
	
	public int karma(String uuid){
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
				if(line.contains(",")){
					String identifier = line.substring(0, line.indexOf(","));
					if(identifier.equals("karma."+uuid)){
						double karma = Double.longBitsToDouble(Long.parseLong(line.substring(line.lastIndexOf(",")+2), 16));
						br.close();
						return (int)karma;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("ioexception level");
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		
		return -1;
	}
	
	public String title(String uuid){
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
				if(line.contains(",")){
					String identifier = line.substring(0, line.indexOf(","));
					if(identifier.equals("title."+uuid)){
						String hexString = line.substring(line.lastIndexOf(",")+2);;    
				    	byte[] bytes = null;
						try {
							bytes = Hex.decodeHex(hexString.toCharArray());
						} catch (DecoderException e) {
							e.printStackTrace();
							//getLogger().info("DECODER EXCEPTION! What's that?");
						}
						
						String title = "";
						try {
							title = new String(bytes, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
						br.close();
						
						return title.substring(2);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("ioexception level");
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		
		return "Guest";
	}
	
	public ChatColor color(String title){
		if(title.contains("Guest"))
			return ChatColor.DARK_GRAY;
		else if(title.contains("Administrator"))
			return ChatColor.DARK_RED;
		else if(title.contains("Head"))
			return ChatColor.DARK_PURPLE;
		else if(title.contains("Lead"))
			return ChatColor.BLUE;
		else
			return ChatColor.GOLD;
	}
	
	public ChatColor secColor(String title){
		if(title.contains("Guest"))
			return ChatColor.GRAY;
		else if(title.contains("Administrator"))
			return ChatColor.RED;
		else if(title.contains("Head"))
			return ChatColor.LIGHT_PURPLE;
		else if(title.contains("Lead"))
			return ChatColor.AQUA;
		else
			return ChatColor.YELLOW;
	}
	
	public String type(String uuid){
		ReversedLinesFileReader br = null;
		String line = "";
		String last = null;
		
		try {
			br = new ReversedLinesFileReader(new File(path));
		} catch (IOException e) {
			return "selclass";
		}
		try {
			while ((line = br.readLine()) != null) {
				if(line.contains(",")){
					String identifier = line.substring(0, line.indexOf(","));
					if(identifier.equals("class."+uuid)){
						last = line.substring(line.lastIndexOf(",")+2);
						
						String hexString = last;    
				    	byte[] bytes = null;
						try {
							bytes = Hex.decodeHex(hexString.toCharArray());
						} catch (DecoderException e) {
							e.printStackTrace();
							//getLogger().info("DECODER EXCEPTION! What's that?");
						}
						
						String classString = "";
						try {
							classString = new String(bytes, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
						br.close();
						return classString.toLowerCase().substring(2);
				    }
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			getLogger().info("ioexception type");
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		
		
		return "selclass";
	}
	
	public String karmaTitleName(String uuid, String name){
		String title = title(uuid);
		int karma = karma(uuid);
		ChatColor color = color(title);
		ChatColor secColor = secColor(title);
		return color+"["+karma+"] "+title+" "+ChatColor.DARK_GRAY+": "+secColor+name;
	}
	
	public List<Object[]> classInfo(String uuid){
		List<Object[]> classes = new ArrayList<Object[]>();
		
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
				if(line.contains(",")){
					String identifier = line.substring(0, line.indexOf(","));
					if(identifier.contains("var.") && identifier.contains(uuid)){
						int varNum = Integer.parseInt(identifier.substring(identifier.lastIndexOf(":")+1));
						if(varNum != 1)
							continue;
						
						String className = identifier.substring(identifier.indexOf(".")+1, identifier.lastIndexOf("."));
						int hI = className.indexOf("-");
						className = className.substring(0, hI+1) + (className.charAt(hI+1)+"").toUpperCase() + className.substring(hI+2);
						
						double health = Long.parseLong(line.substring(line.lastIndexOf(",")+2), 16);
						line = br.readLine();
						double maxHealth = Double.longBitsToDouble(Long.parseLong(line.substring(line.lastIndexOf(",")+2), 16));
						line = br.readLine();
						double mana = Double.longBitsToDouble(Long.parseLong(line.substring(line.lastIndexOf(",")+2), 16));
						
						Object[] info = {className, health, maxHealth, mana};
						classes.add(info);
					}
				}
			}
		} catch (IOException e) {
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		
		return classes;
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
			String uuid = "";
			String name = "";
			if(sender instanceof Player){
				uuid = ((Player) sender).getUniqueId().toString();
				name = ((Player) sender).getName();
			}
			if(args.length > 0){
				Player player = Bukkit.getServer().getPlayer(args[0]);
				if(player == null){
					OfflinePlayer offP = Bukkit.getServer().getOfflinePlayer(args[0]);
					uuid = offP.getUniqueId().toString();
					name = offP.getName();
				}
				else {
					uuid = player.getUniqueId().toString();
					name = player.getName();
				}
			}
			
			String type = type(uuid);
			((Player)sender).sendMessage(karmaTitleName(uuid, name)+ChatColor.GRAY+" is a level "+level(uuid, type)+" "+type);
			return true;
		}
		return false;
	}
	
}