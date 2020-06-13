package com.loncha.gothicjobscazador;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class CrearCadaver implements Runnable {
	Main m;
	Entity b;
	
	public CrearCadaver(Entity b, Main m) {
		this.b = b;
		this.m = m;
	}
	@Override
	public void run() {
		b.removeMetadata("left", m);
        b.remove();
	}

}
