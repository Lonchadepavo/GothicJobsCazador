package com.loncha.gothicjobscazador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	MecanicasCaza mCaza = new MecanicasCaza(this);
	CrafteosInteractivos crafteos = new CrafteosInteractivos(this);
	
	public static ArrayList<ArrayList<ArrayList<ArrayList<String>>>> listaCaza = new ArrayList<ArrayList<ArrayList<ArrayList<String>>>>();
	public static List<String> animalesCazables = new ArrayList<String>();
	public static ArrayList<ItemStack> itemsCustomCaza = new ArrayList<ItemStack>();
	
	FileConfiguration configFile;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.mCaza, this);
		getServer().getPluginManager().registerEvents(this.crafteos, this);
		
		getCommand("reloadcazador").setExecutor(new Reload(this));
		
		try {
			rellenarListaCaza();
			cargarItemsCustom();
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void rellenarListaCaza() throws FileNotFoundException, IOException, InvalidConfigurationException {
		File config = new File("plugins/GothicJobsCazador/config.yml");
		
		configFile = new YamlConfiguration();
		configFile.load(config);
		
		listaCaza = new ArrayList<ArrayList<ArrayList<ArrayList<String>>>>();
		
		//Cargar lista de animales cazables
		animalesCazables = getCustomConfig().getStringList("animales");
		
		//Niveles de caza
		for (int i = 0; i < 6; i++) {
			ArrayList<ArrayList<ArrayList<String>>> nivelCaza = new ArrayList<ArrayList<ArrayList<String>>>();
 			
			//Sin desollar y desollado
			ArrayList<String> tempSinDesollar = new ArrayList<String>();
			ArrayList<String> tempDesollado = new ArrayList<String>();
			
			for (int k = 0; k < animalesCazables.size(); k++) {
				ArrayList<ArrayList<String>> tempAnimal = new ArrayList<ArrayList<String>>();
				
				tempSinDesollar = (ArrayList<String>) getCustomConfig().getStringList("nivel"+i+"."+animalesCazables.get(k).toLowerCase()+".sin-desollar");
				tempDesollado = (ArrayList<String>) getCustomConfig().getStringList("nivel"+i+"."+animalesCazables.get(k).toLowerCase()+".desollado");
				
				tempAnimal.add(tempSinDesollar);
				tempAnimal.add(tempDesollado);
				
				nivelCaza.add(tempAnimal);
				
				ArrayList<String> materiales = nivelCaza.get(0).get(0);	
			}
			
			listaCaza.add(nivelCaza);
		}
		
	}
	
	public void cargarItemsCustom() throws FileNotFoundException, IOException, InvalidConfigurationException {
		File config = new File("plugins/GothicJobsCazador/itemscustom.yml");
		
		configFile = new YamlConfiguration();
		configFile.load(config);
		
		itemsCustomCaza = new ArrayList<ItemStack>();
		
		if (getCustomConfig().getConfigurationSection("items").getKeys(true) != null) {
			for (String s : getCustomConfig().getConfigurationSection("items").getKeys(false)) {
				String nombre, material;
				int cantidad;
				List<String> lore = new ArrayList<String>();
				
				nombre = getCustomConfig().getString("items."+s+".nombre");
				material = getCustomConfig().getString("items."+s+".material");
				cantidad = getCustomConfig().getInt("items."+s+".cantidad");
				lore = getCustomConfig().getStringList("items."+s+".lore");
				
				ItemStack is = new ItemStack(Material.getMaterial(material), cantidad);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(nombre);
				im.setLore(lore);
				is.setItemMeta(im);
				
				itemsCustomCaza.add(is);
				
			}
		}
	}
	
	public FileConfiguration getCustomConfig() {
		return this.configFile;
	}
}
