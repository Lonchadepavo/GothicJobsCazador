package com.loncha.gothicjobscazador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Cauldron;
import org.bukkit.material.Stairs;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitScheduler;

public class CrafteosInteractivos implements Listener {
	Main m;
	ArrayList<String> tipoConstrucciones = new ArrayList<String>(Arrays.asList("mesa de trabajo","estante para cuero","estante para comida"));
	ArrayList<Material> objetosMesa = new ArrayList<Material>(Arrays.asList(Material.LOG, Material.LOG_2));
	
	ArrayList<Integer> usosTronco = new ArrayList<Integer>(Arrays.asList(5,10,15,20,25,30));
	ArrayList<String> resultadosPorUso = new ArrayList<String>(Arrays.asList("§fMango corto", "§fMango medio", "§fMango largo", "§fAsta", "§fCuerpo de arco de "));
	ArrayList<Material> resultadosPorUsoMaterial = new ArrayList<Material>(Arrays.asList(Material.BLAZE_ROD,Material.BLAZE_ROD,Material.BLAZE_ROD,Material.BLAZE_ROD,Material.BLAZE_ROD));
	
	ArrayList<String> cueroParaSecar = new ArrayList<String>(Arrays.asList("§fPiel pequeña limpia", "§fPiel mediana limpia", "§fPiel grande limpia"));
	ArrayList<String> cueroParaLavar = new ArrayList<String>(Arrays.asList("§fPiel pequeña sucia", "§fPiel mediana sucia", "§fPiel grande sucia"));
	ArrayList<String> comidaParaSecar = new ArrayList<String>(Arrays.asList("RAW_BEEF", "MUTTON", "PORK", "RABBIT","RAW_FISH"));
	
	ArrayList<String> carne = new ArrayList<String>(Arrays.asList("RAW_BEEF", "MUTTON", "PORK", "RABBIT"));
	ArrayList<String> pescado = new ArrayList<String>(Arrays.asList("RAW_FISH"));
	
	public CrafteosInteractivos(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlockPlaced();
		
		switch(checkMesa(b)) {
			case "mesa de trabajo":
				if (objetosMesa.contains(b.getType())) {
					if (b.getType() == Material.LOG) {
						
						b.setMetadata("left", new FixedMetadataValue(m, "true"));
						b.setMetadata("mesa de trabajo", new FixedMetadataValue(m, "true"));
						b.setMetadata("cazador", new FixedMetadataValue(m, "true"));
						b.setMetadata("usos", new FixedMetadataValue(m, 0));
						b.setMetadata("herramientacazador", new FixedMetadataValue(m, "§fcuchillo de cazador"));
						
						switch((int) b.getData()) {
							case 0:
								b.setMetadata("tipoarco", new FixedMetadataValue(m, "roble"));
								
								break;
							case 1:
								b.setMetadata("tipoarco", new FixedMetadataValue(m, "abeto"));
								
								break;
							case 2:
								b.setMetadata("tipoarco", new FixedMetadataValue(m, "abedul"));
								
								break;
							case 3:
								b.setMetadata("tipoarco", new FixedMetadataValue(m, "jungla"));
								
								break;
						}
					} else if (b.getType() == Material.LOG_2) {
						
						b.setMetadata("left", new FixedMetadataValue(m, "true"));
						b.setMetadata("mesa de trabajo", new FixedMetadataValue(m, "true"));
						b.setMetadata("cazador", new FixedMetadataValue(m, "true"));
						b.setMetadata("usos", new FixedMetadataValue(m, 0));
						b.setMetadata("herramientacazador", new FixedMetadataValue(m, "§fcuchillo de cazador"));
						
						switch((int) b.getData()) {
							case 0:
								b.setMetadata("tipoarco", new FixedMetadataValue(m, "acacia"));
								
								break;
							case 1:
								b.setMetadata("tipoarco", new FixedMetadataValue(m, "roble oscuro"));
								
								break;
						}
					}
				break;
				}
		}
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		String nombreItemInHand = "";
		String loreItemInHand = "";
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
			if (itemInHand.getItemMeta().hasLore()) {
				loreItemInHand = itemInHand.getItemMeta().getLore().get(0);
			}
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			//TALLAR ARCOS DE TRONCOS DE MADERA
			if (b.hasMetadata("left")) {
				if (b.hasMetadata("mesa de trabajo")) {
					if (b.getMetadata("herramientacazador").get(0).asString().equalsIgnoreCase(nombreItemInHand)) {
						
						e.setCancelled(true);
						b.removeMetadata("constructor", m);
						//PERMISO NECESARIO
						if (p.hasPermission("gjobs.cazador0")) {
							if (b.getMetadata("usos").get(0).asInt() < 35) {
								int usos = b.getMetadata("usos").get(0).asInt();
								b.setMetadata("usos", new FixedMetadataValue(m,usos+1));
								b.getWorld().dropItem(b.getLocation(), new ItemStack(Material.STICK));
								
								for (int i = 0; i < usosTronco.size(); i++) {
									if (b.getMetadata("usos").get(0).asInt() >= usosTronco.get(i)) {
										if (i > resultadosPorUso.size()-1) {
											b.setMetadata("resultado", new FixedMetadataValue(m, resultadosPorUsoMaterial.get(i-1)));
											b.setMetadata("resultadonombre", new FixedMetadataValue(m, resultadosPorUso.get(i-1)));
											
											for (String s : resultadosPorUso) {
												b.removeMetadata(s, m);
											}
											
											b.setMetadata(resultadosPorUso.get(i-1), new FixedMetadataValue(m, "true"));
											
										} else {
											b.setMetadata("resultado", new FixedMetadataValue(m, resultadosPorUsoMaterial.get(i)));
											b.setMetadata("resultadonombre", new FixedMetadataValue(m, resultadosPorUso.get(i)));
											
											for (String s : resultadosPorUso) {
												b.removeMetadata(s, m);
											}
											
											b.setMetadata(resultadosPorUso.get(i), new FixedMetadataValue(m, "true"));
										}
									}
								}
								
								//Reducir durabilidad y romper herramienta
								if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
									itemInHand.setDurability((short) (itemInHand.getDurability()+1));
								} else {
									p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								}
								
							} else {							
								b.setType(Material.AIR);
								b.removeMetadata("left", m);
								
								for (String s : resultadosPorUso) {
									b.removeMetadata(s, m);
								}
								
								p.sendMessage("¡Has tallado demasiado el tronco y lo has dejado inservible!");
								
								int maxDamage = itemInHand.getType().getMaxDurability();
								int damage = itemInHand.getDurability() - (maxDamage -1);
								
								if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
									itemInHand.setDurability((short) (itemInHand.getDurability()+1));
								} else {
									p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								}
							}
						}
					}
				}
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			//RECOGER UN TRONCO TALLADO
			if (b.hasMetadata("left")) {
				if (b.hasMetadata("mesa de trabajo")) {
					if (b.getMetadata("usos").get(0).asInt() >= usosTronco.get(0)) {
						ItemStack resultado = new ItemStack(Material.getMaterial(b.getMetadata("resultado").get(0).asString()));
						ItemMeta im = resultado.getItemMeta();
						String nombre = b.getMetadata("resultadonombre").get(0).asString();
						
						if (nombre.equalsIgnoreCase("§fCuerpo de arco de ")) {
							nombre += b.getMetadata("tipoarco").get(0).asString();
						}
						
						im.setDisplayName(nombre);
						resultado.setItemMeta(im);
						b.getWorld().dropItem(b.getLocation(), resultado);
						
						b.setType(Material.AIR);
						b.removeMetadata("left", m);
						b.removeMetadata(nombre, m);
					}
					
				}
			} else {
				//SECAR CUERO
				if (cueroParaSecar.contains(nombreItemInHand)) {
					if (checkMesa(b).equalsIgnoreCase("estante para cuero")) {
						
						//PERMISO
						if (p.hasPermission("gjobs.cazador2")) {
							Location posicionBloqueFinal = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ());
							Block bCuero = posicionBloqueFinal.getBlock();
							
							if (bCuero.getType() == Material.AIR) {
							
								bCuero.setType(Material.WOOL);
								bCuero.setData((byte) 12);
								bCuero.setMetadata("right", new FixedMetadataValue(m, "true"));
								bCuero.setMetadata("estante de cuero", new FixedMetadataValue(m, "true"));
								bCuero.setMetadata("resultado", new FixedMetadataValue(m, "LEATHER"));
								bCuero.setMetadata("resultadonombre", new FixedMetadataValue(m,nombreItemInHand));
								bCuero.setMetadata("resultadolore", new FixedMetadataValue(m,loreItemInHand));
								
								if (itemInHand.getAmount()-1 == 0) {
									p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								} else {
									itemInHand.setAmount(itemInHand.getAmount()-1);
									p.getInventory().setItemInMainHand(itemInHand);
								}
								
								BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
					                @Override
					                public void run() {          	
					                	if (bCuero.hasMetadata("right")) {
						                	bCuero.setMetadata("resultado", new FixedMetadataValue(m, "LEATHER"));
						                	System.out.println(bCuero.getMetadata("resultadonombre").get(0).asString());
						                	
						                	if (bCuero.getMetadata("resultadonombre").get(0).asString().contains("pequeña")) {
						                		bCuero.setMetadata("resultadonombre", new FixedMetadataValue(m, "§fCuero pequeño"));
						                		System.out.println(bCuero.getMetadata("resultadonombre").get(0).asString());
						                		
						                	} else if (bCuero.getMetadata("resultadonombre").get(0).asString().contains("mediana")) {
						                		bCuero.setMetadata("resultadonombre", new FixedMetadataValue(m, "§fCuero mediano"));
						                		System.out.println(bCuero.getMetadata("resultadonombre").get(0).asString());
						                		
						                	} else if (bCuero.getMetadata("resultadonombre").get(0).asString().contains("grande")) {
						                		bCuero.setMetadata("resultadonombre", new FixedMetadataValue(m, "§fCuero grande"));
						                		System.out.println(bCuero.getMetadata("resultadonombre").get(0).asString());
						                	}
						                	
						                	bCuero.setMetadata("resultadolore", new FixedMetadataValue(m, "Cuero seco listo para ser usado"));
						                    bCuero.setData((byte) 15);
					                	}
					                }
					            }, 100);
					            
					            for (Player players : Bukkit.getOnlinePlayers()) {
				    				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				    					players.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.01F);
				    				}
				    			}
				            
							}
						}

					}
				//SECAR COMIDA
				} else if (comidaParaSecar.contains(nombreItemInHand)) {
					if (checkMesa(b).equalsIgnoreCase("estante para comida")) {
						
						if (p.hasPermission("gjobs.cazador1")) {
							Location posicionBloqueFinal = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()+1, b.getLocation().getZ());
							Block bCuero = posicionBloqueFinal.getBlock();
							
							if (bCuero.getType() == Material.AIR) {
							
								bCuero.setType(Material.WOOL);
								bCuero.setData((byte) 12);
								bCuero.setMetadata("right", new FixedMetadataValue(m, "true"));
								bCuero.setMetadata("resultado", new FixedMetadataValue(m, itemInHand.getType().toString()));
								bCuero.setMetadata("resultadolore", new FixedMetadataValue(m,loreItemInHand));
								
								if (itemInHand.getAmount()-1 == 0) {
									p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								} else {
									itemInHand.setAmount(itemInHand.getAmount()-1);
									p.getInventory().setItemInMainHand(itemInHand);
								}
								
								BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
					                @Override
					                public void run() {          	
					                	if (bCuero.hasMetadata("right")) {	
					                		if (carne.contains(bCuero.getMetadata("resultado").get(0).asString())) {
					                			bCuero.setMetadata("estante de carne", new FixedMetadataValue(m, "true"));
							                	bCuero.setMetadata("resultado", new FixedMetadataValue(m, "COOKED_BEEF"));
							                	bCuero.setMetadata("resultadonombre", new FixedMetadataValue(m, "§fCarne seca"));
							                	bCuero.setMetadata("resultadolore", new FixedMetadataValue(m, "Carne secada al sol lista para comer"));
							                	
							                    bCuero.setData((byte) 15);
					                		} else if (pescado.contains(bCuero.getMetadata("resultado").get(0).asString())){
					                			bCuero.setMetadata("estante de carne", new FixedMetadataValue(m, "true"));
					                			bCuero.setMetadata("resultado", new FixedMetadataValue(m, "COOKED_FISH"));
							                	bCuero.setMetadata("resultadonombre", new FixedMetadataValue(m, "§fPescado seco"));
							                	bCuero.setMetadata("resultadolore", new FixedMetadataValue(m, "Pescado secado al sol listo para comer."));
							                	bCuero.setMetadata("§fPescado seco", new FixedMetadataValue(m, "true"));
							                	
							                    bCuero.setData((byte) 15);
					                		}
					                	}
					                }
					            }, 100);
					            
					            for (Player players : Bukkit.getOnlinePlayers()) {
				    				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				    					players.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.01F);
				    				}
				    			}
				            
							}
						}

					}
					
				//LAVAR PIEL
				} else if (cueroParaLavar.contains(nombreItemInHand)) {
					if (b.getType() == Material.CAULDRON) {
						
						//PERMISO
						if (p.hasPermission("gjobs.cazador1")) { 
							Cauldron c = (Cauldron) b.getState().getData();
							
							if (!c.isEmpty()) {
								if (itemInHand.getAmount()-1 == 0) {
									p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								} else {
									itemInHand.setAmount(itemInHand.getAmount()-1);
									p.getInventory().setItemInMainHand(itemInHand);
								}
								
								ItemStack cuerolimpio = new ItemStack(Material.LEATHER);
								ItemMeta cuerometa = cuerolimpio.getItemMeta();
								
								String nombreItem = "";
								
								if (nombreItemInHand.contains("pequeña")) {
									nombreItem = "§fPiel pequeña limpia";
								} else if (nombreItemInHand.contains("mediana")) {
									nombreItem = "§fPiel mediana limpia";
								} else if (nombreItemInHand.contains("grande")) {
									nombreItem = "§fPiel grande limpia";
								}
								
								cuerometa.setDisplayName(nombreItem);
								cuerometa.setLore(new ArrayList<String>(Arrays.asList("Piel limpia de un animal.")));
								
								cuerolimpio.setItemMeta(cuerometa);
								
								p.getInventory().addItem(cuerolimpio);
								BlockState d = b.getState();
				                d.getData().setData((byte) (c.getData()-1));
				                d.update();
				                
				                for (Player players : Bukkit.getOnlinePlayers()) {
				    				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				    					players.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.01F);
				    				}
				    			}
							}
						}
					}
				}
				else {
					//RECOGER CUERO DEL ESTANTE
					if (b.hasMetadata("right")) {
						if (b.hasMetadata("estante de cuero")) {
							ItemStack cuero = new ItemStack(Material.getMaterial(b.getMetadata("resultado").get(0).asString()),1);
							
							if (b.hasMetadata("resultadonombre")) {
								ItemMeta imCuero = cuero.getItemMeta();
								imCuero.setDisplayName(b.getMetadata("resultadonombre").get(0).asString());
								imCuero.setLore(new ArrayList<String>(Arrays.asList(b.getMetadata("resultadolore").get(0).asString())));
								cuero.setItemMeta(imCuero);	
							}
							
							p.getInventory().addItem(cuero);
							
							b.removeMetadata("right", m);
							b.removeMetadata("resultadonombre", m);
							b.setType(Material.AIR);
							
							for (Player players : Bukkit.getOnlinePlayers()) {
			    				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
			    					players.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 0.01F);
			    				}
			    			}
						}
					}
				}
			}
		}
	}
	
	public String checkMesa(Block b) {	
		Location lMesa = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ());
		Location lEstante = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()+1, b.getLocation().getZ());
		
		//COMPRUEBA SI ES UNA MESA DE TRABAJO DE PIEDRA
		if (lMesa.getBlock().getType() == Material.COBBLESTONE_STAIRS) {
			Block mesaCentro = lMesa.getBlock();
			Location lMesa1 = new Location(mesaCentro.getWorld(), mesaCentro.getX()-1, mesaCentro.getY(), mesaCentro.getZ());
			Location lMesa2 = new Location(mesaCentro.getWorld(), mesaCentro.getX()+1, mesaCentro.getY(), mesaCentro.getZ());
			Location lMesa3 = new Location(mesaCentro.getWorld(), mesaCentro.getX(), mesaCentro.getY(), mesaCentro.getZ()-1);
			Location lMesa4 = new Location(mesaCentro.getWorld(), mesaCentro.getX(), mesaCentro.getY(), mesaCentro.getZ()+1);
			
			Location[] locations = {lMesa1, lMesa2, lMesa3, lMesa4};
			
			for (Location l : locations) {
				if (l.getBlock().getType() == Material.COBBLESTONE_STAIRS) {
					return tipoConstrucciones.get(0);
				}
			}
		}
		
		//COMPRUEBA SI ES UN ESTANTE DE CUERO
		if (b.getType().toString().contains("FENCE")) {
			Location posicion1 = new Location (b.getWorld(), b.getLocation().getX()-1, b.getLocation().getY()-1, b.getLocation().getZ());
			Location posicion2 = new Location (b.getWorld(), b.getLocation().getX()+1, b.getLocation().getY()-1, b.getLocation().getZ());
			Location posicion3 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ()-1);
			Location posicion4 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ()+1);
			
			Block estanteCentro = lEstante.getBlock();
			Location lEstante1 = new Location(estanteCentro.getWorld(), estanteCentro.getX()-1, estanteCentro.getY(), estanteCentro.getZ());
			Location lEstante2 = new Location(estanteCentro.getWorld(), estanteCentro.getX()+1, estanteCentro.getY(), estanteCentro.getZ());
			Location lEstante3 = new Location(estanteCentro.getWorld(), estanteCentro.getX(), estanteCentro.getY(), estanteCentro.getZ()-1);
			Location lEstante4 = new Location(estanteCentro.getWorld(), estanteCentro.getX(), estanteCentro.getY(), estanteCentro.getZ()+1);
			
			Location[] locations = {lEstante1, lEstante2, lEstante3, lEstante4};
			
			for (Location l : locations) {
				if (l.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(2);
				}
			}
			
			if (posicion1.getBlock().getType().toString().contains("FENCE")) {
				if (posicion2.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(1);
				}
			} else if (posicion3.getBlock().getType().toString().contains("FENCE")) {
				if (posicion4.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(1);
				}
			}
		}
		
		return "nada";
	}
	
	public void reducirDurabilidad(Player p ,ItemStack itemInHand, int numero) {
		if (itemInHand.getDurability() < itemInHand.getType().getMaxDurability()) {
			itemInHand.setDurability((short) (itemInHand.getDurability()+numero));
		} else {
			p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
	}

}
