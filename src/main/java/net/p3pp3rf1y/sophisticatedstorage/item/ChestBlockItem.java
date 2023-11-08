package net.p3pp3rf1y.sophisticatedstorage.item;

import net.minecraft.world.level.block.Block;

public class ChestBlockItem extends WoodStorageBlockItem {
	public ChestBlockItem(Block block) {
		this(block, new Properties());
	}
	public ChestBlockItem(Block block, Properties properties) {
		super(block, properties);
	}
}
