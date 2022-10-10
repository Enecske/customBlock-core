package net.enecske.customblock_core.blocks;

import net.enecske.customblock_core.core.BlockIdentifier;
import net.enecske.customblock_core.core.CustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class ExampleBlock extends CustomBlock {
    public BlockIdentifier identifier = new BlockIdentifier(0, 1);
    public String id = "example";

    public BlockIdentifier getIdentifier() {
        return identifier;
    }
    public String getId() {
        return id;
    }

    //@Override
    public Material _getMaterial() {
        return Material.STONE;
    }
    public BlockSoundGroup getSoundGroup() {
        return BlockSoundGroup.STONE;
    }

    public float getHardness() {
        return 1.5F;
    }
    public float getResistance() {
        return 6F;
    }

    @Override
    public boolean isProperTool(ItemStack itemStack) {
        return itemStack.getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean isPartOfTag(TagKey<Block> tag) {
        return tag == BlockTags.STONE_ORE_REPLACEABLES ||
                tag == BlockTags.BAMBOO_PLANTABLE_ON;
    }
}
