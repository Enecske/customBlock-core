package net.enecske.customblock_core.blocks;

import net.enecske.customblock_core.BlockIdentifier;
import net.enecske.customblock_core.CustomBlock;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

public class GabbroBlock extends CustomBlock {
    public BlockIdentifier identifier = new BlockIdentifier(0, 1);
    public String id = "gabbro";

    public BlockIdentifier getIdentifier() {
        return identifier;
    }
    public String getId() {
        return id;
    }

    @Override
    public Material getMaterial() {
        return Material.STONE;
    }

    public float getHardness() {
        return 0.6F;
    }
    public float getResistance() {
        return 6F;
    }

    @Override
    public boolean isProperTool(ItemStack itemStack) {
        return itemStack.getItem() instanceof PickaxeItem;
    }

    @Override
    public int getMinExperienceDrops() {
        return 1;
    }

    @Override
    public int getMaxExperienceDrops() {
        return 3;
    }
}
