package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.enecske.customblock_core.core.CustomBlockRegistry.getBlockType;
import static net.minecraft.block.NoteBlock.INSTRUMENT;
import static net.minecraft.block.NoteBlock.NOTE;

@SuppressWarnings({"deprecated", "unused", "deprecation"})
@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block implements BlockEntityProvider {
    @Shadow public abstract boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data);

    @Shadow protected abstract void playNote(@Nullable Entity entity, World world, BlockPos pos);

    public NoteBlockMixin(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, 0).with(NoteBlock.POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        CustomBlock block = getBlockType(state);

        return block != null ? block.createBlockEntity(pos, state) : null;
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(INSTRUMENT, Instrument.HARP);
    }
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        CustomBlock block = getBlockType(state);
        if (block != null) {
            applyBreakingEffects(block, player);

            MinecraftServer server = player.getServer();
            BlockSoundGroup soundGroup = block.getSoundGroup();

            /*
            This section manages hit sounds
            There's probably a better and less buggy way to solve it
            Write an issue or comment if you have any idea
             */

            if (server != null) {
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), "stopsound " + player.getName().getString() + " block block.wood.break");
                //server.getCommandManager().executeWithPrefix(server.getCommandSource(), "playsound " + soundGroup.getHitSound().getId() + " block " + player.getName().getString() + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + soundGroup.getVolume() + " " + soundGroup.getPitch());

                player.getWorld().playSound(player, pos, soundGroup.getHitSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
            }
        }

        float f = state.getHardness(world, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = player.canHarvest(state) ? 30 : 100;
            return player.getBlockBreakingSpeed(state) / f / (float) i;
        }
    }

    public BlockSoundGroup _getSoundGroup(BlockState state) {
        CustomBlock block = getBlockType(state);
        if(block == null)
            return super.getSoundGroup(state);
        return block.getSoundGroup();
    }

    public boolean hasRandomTicks(BlockState state) {
        return getBlockType(state) != null && getBlockType(state).hasRandomTicks();
    }
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        getBlockType(state).randomTick(state, world, pos, random);
    }
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        getBlockType(state).scheduledTick(state, world, pos, random);
    }
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        super.precipitationTick(state, world, pos, precipitation);
        getBlockType(state).precipitationTick(state, world, pos, precipitation);
    }
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (getBlockType(state) == null) {
            boolean bl = world.isReceivingRedstonePower(pos);
            if (bl != state.get(NoteBlock.POWERED)) {
                this.playNote(null, world, pos, getBlockType(state));

                world.setBlockState(pos, state.with(NoteBlock.POWERED, bl), 3);
            }
        }
        else getBlockType(state).neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (getBlockType(state) == null) {
            this.playNote(player, world, pos, getBlockType(state));
            player.incrementStat(Stats.PLAY_NOTEBLOCK);
        }
        else {
            getBlockType(state).calcBreakingEffects(getBlockType(state));
            getBlockType(state).onBlockBreakStart(state, world, pos, player);
        }
    }
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);

        CustomBlock block = getBlockType(state);
        if(block != null) block.onBroken(world, pos, state);
    }
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        CustomBlock block = getBlockType(state);
        if(block != null) block.onBreak(world, pos, state, player);
    }
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.onBroken(world, pos, state);

        CustomBlock block = getBlockType(state);
        if(block != null) block.afterBreak(world, player, pos, state, blockEntity, stack);
    }
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);

        CustomBlock block = getBlockType(world.getBlockState(pos));
        if(block != null) block.onDestroyedByExplosion(world, pos, explosion);
    }

    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, stack, dropExperience);

        CustomBlock block = getBlockType(state);
        if (block == null) return;

        block.onStacksDropped(state, world, pos, stack, dropExperience);
        
        if (dropExperience) {
            this.dropExperienceWhenMined(world, pos, stack, block.getExperienceDrops());
        }
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        CustomBlock block = getBlockType(state);
        if(block != null) block.onSteppedOn(world, pos, state, entity);
    }
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.onLandedUpon(world, state, pos, entity, fallDistance);

        CustomBlock block = getBlockType(state);
        if(block != null) block.onLandedUpon(world, state, pos, entity, fallDistance);
    }
    public void onEntityLand(BlockView world, Entity entity) {
        super.onEntityLand(world, entity);

        CustomBlock block = getBlockType(world.getBlockState(entity.getBlockPos().down()));
        if(block != null) block.onEntityLand(world, entity);
    }
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        CustomBlock block = getBlockType(state);
        if(block != null) block.onEntityCollision(state, world, pos, entity);
    }
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        super.onProjectileHit(world, state, hit, projectile);

        CustomBlock block = getBlockType(state);
        if(block != null) block.onProjectileHit(world, state, hit, projectile);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(getBlockType(state) == null) {}
        else return getBlockType(state).onUse(state, world, pos, player, hand, hit);

        return ActionResult.PASS;
    }


    private void playNote(@Nullable Entity entity, World world, BlockPos pos, CustomBlock block) {
        if (world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }
    private void applyBreakingEffects(CustomBlock block, PlayerEntity player) {
        if (block.getHasteModifier() > 0)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20, block.getHasteModifier() - 1, false, false, false));

        if (block.getFatigueModifier() > 0)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, block.getFatigueModifier() -
                    (block.isProperTool(player.getMainHandStack()) ? 1 : 0) + (player.getMainHandStack().getItem() instanceof AxeItem ? 1 : 0), false, false, false));
    }
}
