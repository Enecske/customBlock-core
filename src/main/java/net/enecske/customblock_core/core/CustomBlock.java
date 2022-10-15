package net.enecske.customblock_core.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class CustomBlock {
    private int hasteModifier;
    private int fatigueModifier;

    public abstract BlockIdentifier getIdentifier();
    public abstract String getId();

    public int getHasteModifier() {
        return hasteModifier;
    }
    public int getFatigueModifier() {
        return fatigueModifier;
    }
    public String toString() {
        return "CustomBlock{" + getId() + ", " + getIdentifier() + "}";
    }
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

    @NotNull
    public CustomBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomBlockEntity(pos, state);
    }

    public boolean hasRandomTicks() {
        return false;
    }
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.scheduledTick(state, world, pos, random);
    }
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {}
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {}
    public void neighborUpdate (BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {}

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) { return ActionResult.PASS; }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {}
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {}
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {}
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {}
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {}
    public IntProvider getExperienceDrops() {
        return UniformIntProvider.create(0, 0);
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {}
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {}
    public void onEntityLand(BlockView world, Entity entity) {}
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {}
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {}

    public boolean isPartOfTag(TagKey<Block> tag) {
        return false;
    }

    //These methods' testing are still pending

    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return true;
    }
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {}

    //These methods are not implemented

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return null;
    }

    public float getSlipperiness() {
        return 0.6F;
    }
    public float getVelocityMultiplier() {
        return 1F;
    }
    public float getJumpVelocityMultiplier() {
        return 1F;
    }

    public Item asItem() { return null; }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {return false;}

    public boolean emitsRedstonePower(BlockState state) {
        return false;
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return getMaterial().getPistonBehavior();
    }

    public boolean hasComparatorOutput(BlockState state) {
        return false;
    }
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 0;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }
}
