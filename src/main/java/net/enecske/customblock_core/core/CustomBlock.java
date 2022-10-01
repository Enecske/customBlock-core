package net.enecske.customblock_core.core;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.block.NoteBlock.INSTRUMENT;

public abstract class CustomBlock {
    /*public static CustomBlock[] customBlocks = {
            new GabbroBlock()
    };*/

    public CustomBlock() {
        calcBreakingEffects(this);
    }

    public abstract BlockIdentifier getIdentifier();
    public abstract String getId();

    public int hasteModifier;
    public int fatigueModifier;

    public Material getMaterial() {
        return Material.WOOD;
    }
    public BlockSoundGroup getSoundGroup() {
        return BlockSoundGroup.WOOD;
    }
    public float getHardness() {
        return 0.8F;
    }
    public float getResistance() {
        return 1F;
    }
    public boolean isProperTool(ItemStack itemStack) {
        return true;
    }

    public void neighborUpdate (BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {}

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {}

    public final void calcBreakingEffects(CustomBlock block) {
        float h = 1f / block.getHardness();
        int haste = 0;
        int fatigue = 0;

        if(h > 1f) {
            for (int i = 0; i <= 10; i++) {
                if (Math.abs(i * 0.2f - h + 1f) <= Math.abs(haste * 0.2f - h + 1f)) haste = i;
                else break;
            }
        }

        if (h < 1f) {
            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 10; j++) {
                    if (Math.abs(h - ((j * 0.2f + 1f) * Math.pow(0.3f, i))) <= Math.abs(h - ((haste * 0.2f + 1f) * Math.pow(0.3f, fatigue)))) {
                        haste = j;
                        fatigue = i;
                    }
                }
            }
        }

        hasteModifier = haste;
        fatigueModifier = fatigue;
    }

    public IntProvider getExperienceDrops() {
        return UniformIntProvider.create(0, 0);
    }

    @NotNull
    public CustomBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomBlockEntity(pos, state);
    }

    @Override
    public String toString() {
        return "CustomBlock{" + getId() + ", " + getIdentifier() + "}";
    }
}
