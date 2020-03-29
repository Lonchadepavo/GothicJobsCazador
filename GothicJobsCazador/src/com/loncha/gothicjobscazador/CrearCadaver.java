package com.loncha.gothicjobscazador;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class CrearCadaver implements Runnable {
	Main m;
	Block b;
	
	public CrearCadaver(Block b, Main m) {
		this.b = b;
		this.m = m;
	}
	@Override
	public void run() {
		b.removeMetadata("left", m);
        b.setType(Material.AIR);
	}

}
