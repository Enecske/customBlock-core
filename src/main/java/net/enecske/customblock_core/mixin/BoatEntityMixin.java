package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {
    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getNearbySlipperiness", at = @At("TAIL"), cancellable = true)
    public void getNearbySlipperiness(CallbackInfoReturnable<Float> cir) {
        Box box = this.getBoundingBox();
        Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = MathHelper.floor(box2.minX) - 1;
        int j = MathHelper.ceil(box2.maxX) + 1;
        int k = MathHelper.floor(box2.minY) - 1;
        int l = MathHelper.ceil(box2.maxY) + 1;
        int m = MathHelper.floor(box2.minZ) - 1;
        int n = MathHelper.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = VoxelShapes.cuboid(box2);
        float f = 0.0F;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int p = i; p < j; ++p) {
            for(int q = m; q < n; ++q) {
                int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
                if (r != 2) {
                    for(int s = k; s < l; ++s) {
                        if (r <= 0 || s != k && s != l - 1) {
                            mutable.set(p, s, q);
                            BlockState blockState = this.world.getBlockState(mutable);
                            if (!(blockState.getBlock() instanceof LilyPadBlock) && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.world, mutable).offset(p, s, q), voxelShape, BooleanBiFunction.AND)) {
                                CustomBlock block = CustomBlockRegistry.getBlockType(blockState);
                                f += block != null ? block.getSlipperiness() : blockState.getBlock().getSlipperiness();
                                ++o;
                            }
                        }
                    }
                }
            }
        }

        cir.setReturnValue(f / (float) o);
    }
}
