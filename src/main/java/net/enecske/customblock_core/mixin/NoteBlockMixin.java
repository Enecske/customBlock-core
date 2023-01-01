package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.NoteBlockBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
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

@SuppressWarnings({"unused", "deprecation"})
@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block implements BlockEntityProvider {
    @Shadow public abstract boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data);

    public NoteBlockMixin(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, 0).with(NoteBlock.POWERED, false));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        CustomBlock block = getBlockType(state);
        return block != null ? block.createBlockEntity(pos, state) : new NoteBlockBlockEntity(pos, state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(INSTRUMENT, Instrument.HARP);
    }
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView blockView, BlockPos pos) {
        CustomBlock block = getBlockType(state);
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        World world = player.getWorld();
        float f = state.getHardness(blockView, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = player.canHarvest(state) ? 30 : 100;
            if (block != null){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 2, 50, false, false));
                breakBlockByManager(state, pos, player, world, blockView);
            }

            return player.getBlockBreakingSpeed(state) / f / (float) i;
        }
    }

    public BlockSoundGroup getSoundGroup(BlockState state) {
        CustomBlock block = getBlockType(state);
        if(block == null)
            return super.getSoundGroup(state);
        return block.getSoundGroup();
    }

    public boolean hasRandomTicks(BlockState state) {
        CustomBlock block = getBlockType(state);
        return block != null && block.hasRandomTicks();
    }
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        CustomBlock block = getBlockType(state);
        if(block != null)
            block.randomTick(state, world, pos, random);
    }
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        CustomBlock block = getBlockType(state);
        if(block != null)
            block.scheduledTick(state, world, pos, random);
    }
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        super.precipitationTick(state, world, pos, precipitation);
        CustomBlock block = getBlockType(state);
        if(block != null)
            block.precipitationTick(state, world, pos, precipitation);
    }
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        CustomBlock block = getBlockType(state);
        if (block == null) {
            boolean bl = world.isReceivingRedstonePower(pos);
            if (bl != state.get(NoteBlock.POWERED)) {
                this.playNote(null, world, pos);

                world.setBlockState(pos, state.with(NoteBlock.POWERED, bl), 3);
            }
        }
        else block.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        CustomBlock block = getBlockType(state);

        if (block == null) {
            this.playNote(player, world, pos);
            player.incrementStat(Stats.PLAY_NOTEBLOCK);
        }
        else {
            block.calcBreakingEffects(block);
            block.onBlockBreakStart(state, world, pos, player);
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
        super.afterBreak(world, player, pos, state, blockEntity, stack);

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

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        CustomBlock block = getBlockType(state);
        if(block == null) {
            NoteBlockBlockEntity blockEntity = (NoteBlockBlockEntity) world.getBlockEntity(pos);

            if (blockEntity != null) {
                blockEntity.cycleNote();
            }

            this.playNote(player, world, pos);
            player.incrementStat(Stats.TUNE_NOTEBLOCK);
            return ActionResult.CONSUME;
        }
        else return block.onUse(state, world, pos, player, hand, hit);
    }

    public boolean emitsRedstonePower(BlockState state) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.emitsRedstonePower();

        return super.emitsRedstonePower(state);
    }
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.getWeakRedstonePower(world, pos, direction);

        return super.getWeakRedstonePower(state, world, pos, direction);
    }
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.getStrongRedstonePower(world, pos, direction);

        return super.getStrongRedstonePower(state, world, pos, direction);
    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.getPistonBehavior();

        return super.getPistonBehavior(state);
    }

    public boolean hasComparatorOutput(BlockState state) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.hasComparatorOutput();

        return super.hasComparatorOutput(state);
    }
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.getComparatorOutput(world, pos);

        return super.getComparatorOutput(state, world, pos);
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        CustomBlock block = getBlockType(state);
        if(block != null) return block.createScreenHandlerFactory(world, pos);

        return super.createScreenHandlerFactory(state, world, pos);
    }

    private void playNote(@Nullable Entity entity, World world, BlockPos pos) {
        if (world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }
    private void breakBlockByManager(BlockState state, BlockPos pos, PlayerEntity player, World world, BlockView blockView) {
        int breakingManagerEntityId = player.writeNbt(new NbtCompound()).getInt("breakingManagerEntityId");
        Entity entity = world.getEntityById(breakingManagerEntityId);

        if (entity == null) return;

        NbtCompound parentCompound = entity.writeNbt(new NbtCompound());
        NbtCompound dataCompound = parentCompound.getCompound("data");

        int tick = dataCompound.getInt("tick");

        CustomBlock block = getBlockType(state);
        float f = block != null ? block.getHardness() : state.getHardness(blockView, pos);
        int i = (block != null ? block.isProperTool(player.getMainHandStack()) : player.canHarvest(state)) ? 30 : 100;

        float v = block != null ? player.getMainHandStack().getMiningSpeedMultiplier(block.getSimilarBlock().getDefaultState()) : player.getInventory().getBlockBreakingSpeed(state);
        {
            if (v > 1.0F) {
                int efficiency = EnchantmentHelper.getEfficiency(player);
                ItemStack itemStack = player.getMainHandStack();
                if (efficiency > 0 && !itemStack.isEmpty()) {
                    v += (float) (efficiency * efficiency + 1);
                }
            }

            if (StatusEffectUtil.hasHaste(player))
                v *= 1.0F + (float) (StatusEffectUtil.getHasteAmplifier(player) + 1) * 0.2F;

            if (player.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player))
                v /= 5F;

            if (!player.isOnGround())
                v /= 5F;
        }

        v /= f * (float) i;

        float breakingProgress = dataCompound.getFloat("breakingProgress");
        int j = (int) (v * (tick + 1) * 10.0F);
        if (j != breakingProgress) {
            world.setBlockBreakingInfo(breakingManagerEntityId, pos, j);
            breakingProgress = j;
        }

        tick++;

        if (breakingProgress > 10) {
            ((ServerPlayerEntity) player).interactionManager.tryBreakBlock(pos);
            tick = -1;
            breakingProgress = -1;
        }

        dataCompound.putFloat("breakingProgress", breakingProgress);
        dataCompound.putFloat("blockBreakingSpeed", v);
        dataCompound.putInt("tick", tick);
        dataCompound.put("miningPos", NbtHelper.fromBlockPos(pos));

        parentCompound.put("data", dataCompound);
        entity.readNbt(parentCompound);
    }
}
