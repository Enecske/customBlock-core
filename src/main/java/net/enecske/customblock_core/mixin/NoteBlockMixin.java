package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
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
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
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
import static net.minecraft.block.NoteBlock.NOTE;

@SuppressWarnings({"deprecated", "unused", "deprecation"})
@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends Block implements BlockEntityProvider {
    @Shadow public abstract boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data);

    public NoteBlockMixin(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(INSTRUMENT, Instrument.HARP).with(NOTE, 0).with(NoteBlock.POWERED, false));
    }

    public BlockSoundGroup _getSoundGroup(BlockState state) {
        CustomBlock block = CustomBlockRegistry.getBlockType(state);
        if(block == null)
            return super.getSoundGroup(state);
        return block.getSoundGroup();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        CustomBlock block = CustomBlockRegistry.getBlockType(state);

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
        CustomBlock block = CustomBlockRegistry.getBlockType(state);
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
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), "playsound " + soundGroup.getHitSound().getId() + " block " + player.getName().getString() + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + soundGroup.getVolume() + " " + soundGroup.getPitch());
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

    private void applyBreakingEffects(CustomBlock block, PlayerEntity player) {
        if (block.hasteModifier > 0)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 20, block.hasteModifier - 1, false, false, false));

        if (block.fatigueModifier > 0)
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, block.fatigueModifier -
                    (block.isProperTool(player.getMainHandStack()) ? 1 : 0) + (player.getMainHandStack().getItem() instanceof AxeItem ? 1 : 0), false, false, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (CustomBlockRegistry.getBlockType(state) == null) {
            boolean bl = world.isReceivingRedstonePower(pos);
            if (bl != state.get(NoteBlock.POWERED)) {
                this.playNote(null, world, pos, CustomBlockRegistry.getBlockType(state));

                world.setBlockState(pos, state.with(NoteBlock.POWERED, bl), 3);
            }
        }
        else CustomBlockRegistry.getBlockType(state).neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private void playNote(@Nullable Entity entity, World world, BlockPos pos, CustomBlock block) {
        if (world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(CustomBlockRegistry.getBlockType(state) == null) {}
        else return CustomBlockRegistry.getBlockType(state).onUse(state, world, pos, player, hand, hit);

        return ActionResult.PASS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (CustomBlockRegistry.getBlockType(state) == null) {
            this.playNote(player, world, pos, CustomBlockRegistry.getBlockType(state));
            player.incrementStat(Stats.PLAY_NOTEBLOCK);
        }
        else CustomBlockRegistry.getBlockType(state).onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        CustomBlock block = CustomBlockRegistry.getBlockType(state);
        if(block == null) return;

        if(block.getExperienceDrops().getMax() > 0 && world instanceof ServerWorld serverWorld) {
            this.dropExperienceWhenMined(serverWorld, pos, player.getMainHandStack(), block.getExperienceDrops());
        }
    }
}
