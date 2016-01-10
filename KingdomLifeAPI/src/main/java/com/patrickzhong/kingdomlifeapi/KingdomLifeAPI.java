package com.patrickzhong.kingdomlifeapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class KingdomLifeAPI extends JavaPlugin{
	private static String prefix = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + "KingdomLifeAPI" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "] ";
	String path = (new File("")).getAbsolutePath()+"/plugins/Skript/variables.csv";
	
	public List<ItemStack> getItems(String type, String rarity, String minLevel){
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
		
		List<ItemStack> listOfItems = new ArrayList<ItemStack>();
		
		try {
			rarity = rarity.toLowerCase();
			String itemType = "";
			if(type.equalsIgnoreCase("Mage"))
				itemType = "STICK";
			else if(type.equalsIgnoreCase("Archer"))
				itemType = "BOW";
			else if(type.equalsIgnoreCase("Rogue"))
				itemType = "SHOVEL";
			
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(",");
			    if(arr[0].equals(rarity+"."+minLevel)){
			    	String hexString = arr[2].substring(1);    
			    	byte[] bytes = null;
					try {
						bytes = Hex.decodeHex(hexString.toCharArray());
					} catch (DecoderException e) {
						e.printStackTrace();
					}
					
			    	String itemString = new String(bytes, "UTF-8");
			    	String[] info = new String[2];
			    	
			    	if(itemString.contains(itemType) || (itemType.equals("SHOVEL") && (itemString.contains("DIAMOND_SHOVEL") || itemString.contains("GOLD_SHOVEL") || itemString.contains("IRON_SHOVEL") || itemString.contains("STONE_SHOVEL") || itemString.contains("WOOD_SHOVEL")))){
			    		String[] infoArr = itemString.split(" ");
			    		outerloop:
			    		for(int i = 0; i < infoArr.length; i++){
			    			if(infoArr[i].equals("display-name:"))
								info[0] = infoArr[i+1] + infoArr[i+2];
							else if(infoArr[i].equals("Attack:")){
								info[1] = infoArr[i+1];
								info[1] = info[1].substring(0, info[1].length()-2);
							}
							
							if(info[0] != null && info[1] != null){
								break outerloop;
							}
			    		}
			    		ItemStack item = new ItemStack(Material.getMaterial(itemType));
			    		ItemMeta im = item.getItemMeta();
			    		List<String> lores = new ArrayList<String>();
			    		lores.add(ChatColor.RED+"⚔ Attack: "+info[1]);
			    		lores.add(ChatColor.GOLD+"✣ Min. Level: "+minLevel);
			    		String color = "";
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
		}
		
		return listOfItems;
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
		}
		return false;
	}
}
	/*
	public static void main(String[] args){
		String hexString = " 810576616C75652080F776616C75653A0A20203D3D3A206F72672E62756B6B69742E696E76656E746F72792E4974656D537461636B0A2020747970653A20535449434B0A20206D6574613A0A202020203D3D3A204974656D4D6574610A202020206D6574612D747970653A20554E53504543494649430A20202020646973706C61792D6E616D653A20C2A762466F75722D4C6561766564204272616E63680A202020206C6F72653A0A202020202D2027C2A763E29A942041747461636B3A20312D32270A202020202D2027C2A736E29CA3204D696E2E204C6576656C3A2030270A202020202D2027270A202020202D20C2A762556E636F6D6D6F6E204974656D0A".substring(1);    
		byte[] bytes = null;
		
		try {
			bytes = Hex.decodeHex(hexString.toCharArray());
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		
		String itemString = null;
		try {
			itemString = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] info = new String[2];
		String type = "STICK";
		
	   	if(itemString.contains(type)){
	   		String[] infoArr = itemString.split(" ");
	   		outerloop:
	   		for(int i = 0; i < infoArr.length; i++){
				if(infoArr[i].equals("display-name:"))
					info[0] = ChatColor.translateAlternateColorCodes('§', infoArr[i+1] + infoArr[i+2]);
				else if(infoArr[i].equals("Attack:")){
					info[1] = infoArr[i+1];
					info[1] = info[1].substring(0, info[1].length()-2);
				}
				
				if(info[0] != null && info[1] != null){
					break outerloop;
				}
			}
			   		
			System.out.println(info[0]);
			System.out.println(info[1]);
		}
	}
	
}


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
	*/
