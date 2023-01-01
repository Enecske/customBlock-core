package net.enecske.customblock_core.blocks;

import net.enecske.customblock_core.core.BlockIdentifier;
import net.enecske.customblock_core.core.CustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class ExampleBlock extends CustomBlock {
    /** This class is a possible implementation of a custom block in the mod
     *
     *  <p>It has the same properties as {@link Blocks#STONE}
     *
     *  <p>Later this class will be moved to a new GitHub branch <b>example_mod</b>
     *
     * @see CustomBlock
     */

    public final BlockIdentifier identifier = new BlockIdentifier(0, 1, "example", "example_block");

    public BlockIdentifier getIdentifier() {
        return identifier;
    }

    public Material getMaterial() {
        return Material.STONE;
    }
    public BlockSoundGroup getSoundGroup() {
        return BlockSoundGroup.SLIME;
    }

    public float getHardness() {
        return 1.5F;
    }
    public float getResistance() {
        return 6F;
    }

    @Override
    public Block getSimilarBlock() {
        return Blocks.STONE;
    }

    public boolean isProperTool(ItemStack itemStack) {
        return itemStack.getItem() instanceof PickaxeItem;
    }

    public boolean isPartOfTag(TagKey<Block> tag) {
        return tag == BlockTags.STONE_ORE_REPLACEABLES ||
                tag == BlockTags.BAMBOO_PLANTABLE_ON;
    }
}
