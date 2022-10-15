package net.enecske.customblock_core.blocks;

import net.enecske.customblock_core.core.BlockIdentifier;
import net.enecske.customblock_core.core.CustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ExampleBlock extends CustomBlock {
    public BlockIdentifier identifier = new BlockIdentifier(0, 1);
    public String id = "example";

    public BlockIdentifier getIdentifier() {
        return identifier;
    }
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
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

    @Override
    public IntProvider getExperienceDrops() {
        return UniformIntProvider.create(1, 3);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
        MinecraftServer server = world.getServer();
        server.getCommandManager().executeWithPrefix(server.getCommandSource(), "say onStacksDropped");
    }
}
