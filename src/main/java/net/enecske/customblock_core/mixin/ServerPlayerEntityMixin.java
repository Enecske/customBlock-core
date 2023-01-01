package net.enecske.customblock_core.mixin;

import com.mojang.authlib.GameProfile;
import net.enecske.customblock_core.CustomBlockCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    private int breakingManagerEntityId;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        breakingManagerEntityId = nbt.getInt("breakingManagerEntityId");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("breakingManagerEntityId", breakingManagerEntityId);
    }

    private void createBreakingManager(World world) {
        Entity entity = world.getEntityById(breakingManagerEntityId);
        if(entity instanceof MarkerEntity) return;

        entity = new MarkerEntity(EntityType.MARKER, world);

        entity.setPosition(this.getPos());
        entity.addScoreboardTag(CustomBlockCore.MODID + ":breaking_manager");

        breakingManagerEntityId = entity.getId();

        world.spawnEntity(entity);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        createBreakingManager(this.world);

        Entity entity = this.world.getEntityById(breakingManagerEntityId);

        assert entity != null;
        entity.setPosition(getPos());
    }
}