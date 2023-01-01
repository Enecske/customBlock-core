package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.core.CustomBlock;
import net.enecske.customblock_core.core.CustomBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    public float setSlipperiness(Block instance) {
        CustomBlock block = CustomBlockRegistry.getBlockType(this.world.getBlockState(this.getVelocityAffectingPos()));
        if(block == null) return instance.getSlipperiness();

        return block.getSlipperiness();
    }
}
