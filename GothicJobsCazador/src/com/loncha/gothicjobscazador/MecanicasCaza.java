package com.loncha.gothicjobscazador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.scheduler.BukkitScheduler;

import javax.swing.Timer;

import com.loncha.gothicjobs.Profesiones;

import net.minecraft.server.v1_12_R1.CustomFunction.c;

public class MecanicasCaza implements Listener, Plugin {
	//Todas las mecánicas del proceso de caza, desde matar al animal hasta despellejarle, trocearlo y conseguir diferentes partes.
	
	Main m;
	
	public MecanicasCaza (Main m) {
		this.m = m;
	}
	
	public static int getNivelCaza(Player p) {
		int nivelCaza = Profesiones.checkNivelesProfesiones(p)[1];
		
		return nivelCaza;
	}	
	
	//PROCESO DE MATAR A UN MOB Y COMPROBAR SI DEJA CADAVER O NO
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent e) {
		
		if (e.getEntity().getKiller() instanceof Player) {
			Player p = e.getEntity().getKiller();
			ItemStack itemInHand = p.getInventory().getItemInMainHand();
			String entityName = "";
			Location l = e.getEntity().getLocation();
			
			String nombreItemInHand = "";
			
			if (itemInHand.hasItemMeta()) {
				nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
			} else {
				nombreItemInHand = itemInHand.getType().toString();
			}
			
			if (e.getEntity().getCustomName() != null) {
				entityName = e.getEntity().getCustomName();
			} else {
				entityName = e.getEntity().getName();
			}
	
			//Si has matado a un mob
			if (e.getEntity() instanceof LivingEntity) {
				if (!(e.getEntity() instanceof Player)) {
					int nivelDeCazador = getNivelCaza(p);
					ArrayList<ArrayList<ArrayList<String>>> nivelCaza = m.listaCaza.get(nivelDeCazador);
					
					for (int i = 0; i < m.animalesCazables.size(); i++) {
						if (m.animalesCazables.get(i).equalsIgnoreCase(entityName)) {
							if (itemInHand.getType().toString().contains("BOW")) { 
								ArrayList<String> materiales = nivelCaza.get(i).get(0);
								
								ArrayList<String> material = new ArrayList<String>();
								ArrayList<String> cantidad = new ArrayList<String>();
		
								for (String s : materiales) {
									String[] split = s.split(",");
									material.add(split[0]);
									cantidad.add(split[1]);
								}
								
								//Crear bloque
								Block b = l.getBlock();
								
								if (b.getType().toString().contains("SLAB") || b.getType().toString().contains("STEP") ) {
									Location loc = new Location(p.getWorld(), b.getLocation().getX(), b.getLocation().getY()+1,b.getLocation().getZ());		
									b = loc.getBlock();
								}
								
								b.setType(Material.WOOL);
								b.setData((byte) 12);
								
								p.sendMessage("El cadáver del animal cae al suelo");
								
								//Añadir metadatos
								String sindesollar = "sin-desollar";
								
								b.setMetadata(sindesollar, new FixedMetadataValue(m,"true"));
								b.setMetadata("Cadaver de " + entityName.toLowerCase(), new FixedMetadataValue(m,"true"));
								b.setMetadata("left", new FixedMetadataValue(m,"true"));
								
								for (int k = 0; k < material.size(); k++) {
									b.setMetadata(material.get(k), new FixedMetadataValue(m, cantidad.get(k)));
								}
								
								BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					            scheduler.scheduleSyncDelayedTask(m, new CrearCadaver(b,m), 6000);
							}
						}
					}
				}
			}
		}
	}
	
	//EVENTO PARA LAS MECÁNICAS RELACIONADAS CON LA CAZA AL INTERACTUAR CON EL CLICK DERECHO (DESPELLEJAR Y TROCEAR ANIMALES)
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		String nombreItemInHand = "";
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			for (int i = 0; i < m.animalesCazables.size(); i++) {
				if (b.hasMetadata("left")) {
					if (b.hasMetadata("Cadaver de " + m.animalesCazables.get(i).toLowerCase())) {
						e.setCancelled(true);
						
						if (nombreItemInHand.equalsIgnoreCase("§fCuchillo de cazador")) {
							if (b.hasMetadata("sin-desollar")) {
								for (ItemStack item : m.itemsCustomCaza) {
									String nombreItem = "";
									
									if (item.hasItemMeta()) {
										nombreItem = item.getItemMeta().getDisplayName();
									} else {
										nombreItem = item.getType().toString();
									}
									
									if (b.hasMetadata(nombreItem)) {
										int cantidadItem = b.getMetadata(nombreItem).get(0).asInt();
										
										if (cantidadItem > 0) {
											b.getLocation().getWorld().dropItem(b.getLocation(), item);
											cantidadItem--;
											b.setMetadata(nombreItem, new FixedMetadataValue(m, cantidadItem));
											
											if (cantidadItem == 0) {
												
												if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
													itemInHand.setDurability((short) (itemInHand.getDurability()+1));
												} else {
													p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
												}
												
												b.removeMetadata(nombreItem, m);
												b.removeMetadata("sin-desollar", m);
												
												b.setMetadata("desollado", new FixedMetadataValue(m,"true"));
												b.setType(Material.WOOL);
												b.setData((byte) 2);
												
												int nivelDeCazador = getNivelCaza(p);
												ArrayList<ArrayList<ArrayList<String>>> nivelCaza = m.listaCaza.get(nivelDeCazador);
												
												ArrayList<String> materiales = nivelCaza.get(i).get(1);
												
												ArrayList<String> material = new ArrayList<String>();
												ArrayList<String> cantidad = new ArrayList<String>();
						
												for (String s : materiales) {
													String[] split = s.split(",");
													material.add(split[0]);
													cantidad.add(split[1]);
												}
												
												for (int k = 0; k < material.size(); k++) {
													b.setMetadata(material.get(k), new FixedMetadataValue(m, cantidad.get(k)));
												}
												
												if (b.hasMetadata(nombreItem)) {
													cantidadItem = b.getMetadata(nombreItem).get(0).asInt();
													cantidadItem--;
													b.setMetadata(nombreItem, new FixedMetadataValue(m, cantidadItem));
													
													if (cantidadItem == 0) {
														b.removeMetadata(nombreItem, m);
														b.removeMetadata("desollado", m);
														b.removeMetadata("Cadaver de " + m.animalesCazables.get(i), m);
														b.removeMetadata("left", m);
														b.setType(Material.AIR);
													}
												}
											}
											
										}
									}
								}
								
							} else if (b.hasMetadata("desollado")) {
							
								for (ItemStack item : m.itemsCustomCaza) {
									String nombreItem = "";
									
									if (item.hasItemMeta()) {
										nombreItem = item.getItemMeta().getDisplayName();
									} else {
										nombreItem = item.getType().toString();
									}
									
									if (b.hasMetadata(nombreItem)) {
										int cantidadItem = b.getMetadata(nombreItem).get(0).asInt();
										if (cantidadItem > 0) {
											
											if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
												itemInHand.setDurability((short) (itemInHand.getDurability()+1));
											} else {
												p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
											}
											
											b.getLocation().getWorld().dropItem(b.getLocation(), item);
											cantidadItem--;
											b.setMetadata(nombreItem, new FixedMetadataValue(m, cantidadItem));
											
											if (cantidadItem == 0) {
												b.removeMetadata(nombreItem, m);
												b.removeMetadata("desollado", m);
												b.removeMetadata("Cadaver de " + m.animalesCazables.get(i), m);
												b.removeMetadata("left", m);
												b.setType(Material.AIR);
											}
										}
									}
								}
							}
						} else {
							p.sendMessage(ChatColor.DARK_RED+"Necesitas un cuchillo de cazador para trabajar con el animal.");
						}
					}
				}
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			for (int i = 0; i < m.animalesCazables.size(); i++) {
				if (b.hasMetadata("left")) {
					if (b.hasMetadata("Cadaver de " + m.animalesCazables.get(i).toLowerCase())) {
						e.setCancelled(true);
						
						if (!nombreItemInHand.equalsIgnoreCase("§fCuchillo de cazador")) {
							if (b.hasMetadata("sin-desollar")) {
								p.sendMessage(ChatColor.DARK_RED+"Para recoger un cadáver completo primero tienes que desollarlo.");
								
							} else if (b.hasMetadata("desollado")) {
								ItemStack cadaver = new ItemStack(Material.DIAMOND_HOE);
								ItemMeta cadaverMeta = cadaver.getItemMeta();
								
								cadaverMeta.setDisplayName("§fCadaver de " + m.animalesCazables.get(i).toLowerCase());
								cadaverMeta.setLore(new ArrayList<String>(Arrays.asList("El cadáver de un animal desollado.")));
								
								cadaverMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
								cadaverMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
								cadaverMeta.setUnbreakable(true);
								
								cadaver.setItemMeta(cadaverMeta);
								
								
								for (ItemStack item : m.itemsCustomCaza) {
									String nombreItem = "";
									
									if (item.hasItemMeta()) {
										nombreItem = item.getItemMeta().getDisplayName();
									} else {
										nombreItem = item.getType().toString();
									}
									
									b.removeMetadata(nombreItem, m);
								}
								
								b.removeMetadata("desollado", m);
								b.removeMetadata("Cadaver de " + m.animalesCazables.get(i), m);
								b.removeMetadata("left", m);
								b.setType(Material.AIR);
								
								b.getLocation().getWorld().dropItem(b.getLocation(), cadaver);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FileConfiguration getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getDataFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginDescriptionFile getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginLoader getPluginLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getResource(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Server getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNaggable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reloadConfig() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveConfig() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveDefaultConfig() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveResource(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNaggable(boolean arg0) {
		// TODO Auto-generated method stub
		
	}
}
