package net.p3pp3rf1y.sophisticatedstorage.item;

import net.minecraft.world.level.block.Block;

public class ChestBlockItem extends WoodStorageBlockItem {
	public ChestBlockItem(Block block) {
		this(block, new Properties());
	}
	public ChestBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

/*	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private final NonNullLazy<BlockEntityWithoutLevelRenderer> ister = NonNullLazy.of(() -> new ChestItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return ister.get();
			}
		});
	}*/
}
