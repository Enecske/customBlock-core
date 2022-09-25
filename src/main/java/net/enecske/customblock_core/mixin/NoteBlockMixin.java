package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.BlockIdentifier;
import net.enecske.customblock_core.CustomBlock;
import net.enecske.customblock_core.NoteblockBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.block.NoteBlock.INSTRUMENT;

@SuppressWarnings({"deprecated", "unused"})
@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block implements BlockEntityProvider {
    @Shadow public abstract boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data);

    @Shadow protected abstract void playNote(@Nullable Entity entity, World world, BlockPos pos);

    public NoteBlockMixin(Settings settings) {
        super(Settings.of(Material.STONE));
        this.setDefaultState(this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NoteBlock.NOTE, 0).with(NoteBlock.POWERED, false));
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        CustomBlock block = NoteblockBlockEntity.getBlockType(new BlockIdentifier(state.get(INSTRUMENT).ordinal(), state.get(NoteBlock.NOTE)));
        if(block == null)
            return super.getSoundGroup(state);
        return block.getSoundGroup();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NoteblockBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(INSTRUMENT, Instrument.HARP);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        CustomBlock block = NoteblockBlockEntity.getBlockType(new BlockIdentifier(state.get(INSTRUMENT).ordinal(), state.get(NoteBlock.NOTE)));
        if (block != null) applyBreakingEffects(block, player);

        float f = state.getHardness(world, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = player.canHarvest(state) ? 30 : 100;
            return player.getBlockBreakingSpeed(state) / f / (float) i;
        }
    }

    private void applyBreakingEffects(CustomBlock block, PlayerEntity player) {
        if (player.getServer() != null)
            player.getServer().getCommandManager().executeWithPrefix(player.getServer().getCommandSource(), "say " + block.hasteModifier + ", " + block.fatigueModifier);

        if (block.hasteModifier > 0)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20, block.hasteModifier - 1, false, false, false));
        if (block.fatigueModifier > 0)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, block.fatigueModifier -
                    (block.isProperTool(player.getMainHandStack()) ? 1 : 0), false, false, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        ((NoteblockBlockEntity) world.getBlockEntity(pos)).calculateBlockType(pos, state);

        if (((NoteblockBlockEntity) world.getBlockEntity(pos)).getBlock() == null) {
            boolean bl = world.isReceivingRedstonePower(pos);
            if (bl != state.get(NoteBlock.POWERED)) {
                this.playNote(null, world, pos, world.getBlockEntity(pos));

                world.setBlockState(pos, state.with(NoteBlock.POWERED, bl), 3);
            }
        }
        else ((NoteblockBlockEntity) world.getBlockEntity(pos)).getBlock().neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private void playNote(@Nullable Entity entity, World world, BlockPos pos, BlockEntity blockEntity) {
        if (world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(((NoteblockBlockEntity) world.getBlockEntity(pos)).getBlock() == null) {}
        else ((NoteblockBlockEntity) world.getBlockEntity(pos)).getBlock().onUse(state, world, pos, player, hand, hit);

        return ActionResult.PASS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (((NoteblockBlockEntity) world.getBlockEntity(pos)).getBlock() == null) {
            this.playNote(player, world, pos, world.getBlockEntity(pos));
            player.incrementStat(Stats.PLAY_NOTEBLOCK);
        }
        else ((NoteblockBlockEntity) world.getBlockEntity(pos)).getBlock().onBlockBreakStart(state, world, pos, player);

        if(world.getServer() != null)
            world.getServer().getCommandManager().executeWithPrefix(world.getServer().getCommandSource(), "say breaking started");
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        CustomBlock block;
        if(world.getBlockEntity(pos) instanceof NoteblockBlockEntity blockEntity && blockEntity.getBlock() != null)
            block = blockEntity.getBlock();
        else return;

        if(block.getMinExperienceDrops() > 0 && block.getMaxExperienceDrops() >= block.getMinExperienceDrops()) {
            int xpCount = (int) ((Math.random() * (block.getMaxExperienceDrops() - block.getMinExperienceDrops())) + block.getMinExperienceDrops());
            if (xpCount == 0) return;
            for (int i = 0; i < xpCount; i++) {
                ExperienceOrbEntity entity = ((EntityType<ExperienceOrbEntity>) EntityType.get("minecraft:experience_orb").get()).create(world);
                entity.updatePosition(pos.getX(), pos.getY(), pos.getZ());
                world.spawnEntity(entity);
            }
        }
    }
}
