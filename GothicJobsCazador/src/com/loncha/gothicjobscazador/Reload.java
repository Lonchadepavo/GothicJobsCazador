package com.loncha.gothicjobscazador;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Reload implements CommandExecutor {
	Main m;
	public Reload(Main m) {
		this.m = m;
	}
	

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player p = (Player) arg0;
		if (p.isOp()) {
			if (arg1.getName().equalsIgnoreCase("reloadcazador")) {
				try {
					m.rellenarListaCaza();
					m.cargarItemsCustom();
					p.sendMessage(ChatColor.GREEN+"GothicJobsCazador recargado");
					return true;
				} catch (IOException | InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}

}
