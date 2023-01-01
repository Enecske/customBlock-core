package net.enecske.customblock_core.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow private int blockBreakingProgress;
    @Shadow private int tickCounter;
    @Shadow @Final protected ServerPlayerEntity player;
    @Shadow protected ServerWorld world;

    private float continueMining(BlockState state, BlockPos pos, int failedStartMiningTime) {
        int i = this.tickCounter - failedStartMiningTime;
        float f = state.calcBlockBreakingDelta(this.player, this.player.world, pos) * (float)(i + 1);
        int j = (int)(f * 10.0F);
        this.world.setBlockBreakingInfo(this.player.getId(), pos, j);
        this.blockBreakingProgress = j;

        return f;
    }
}