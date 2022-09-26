package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(ExplosionBehavior.class)
public class ExplosionBehaviorMixin {
    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        float resistance = blockState.getBlock().getBlastResistance();
        if(world.getBlockEntity(pos) instanceof CustomBlockEntity blockEntity && blockEntity.getBlock() != null)
            resistance = blockEntity.getBlock().getResistance();

        return blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(Math.max(resistance, fluidState.getBlastResistance()));
    }
}
