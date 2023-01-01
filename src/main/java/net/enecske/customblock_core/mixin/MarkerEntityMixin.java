package net.enecske.customblock_core.mixin;

import net.enecske.customblock_core.CustomBlockCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(MarkerEntity.class)
public abstract class MarkerEntityMixin extends Entity {
    @Shadow private NbtCompound data;

    int tick, lastTick;

    public MarkerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public void tick() {
        if (this.getScoreboardTags().contains(CustomBlockCore.MODID + ":breaking_manager")) {
            List<? extends PlayerEntity> players = world.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                PlayerEntity player = players.get(i);
                NbtCompound playerNbt = player.writeNbt(new NbtCompound());
                if (playerNbt.getInt("breakingManagerEntityId") == this.getId()) break;

                if (i == players.size() - 1) this.kill();
            }

            BlockPos pos = NbtHelper.toBlockPos(data.getCompound("miningPos"));
            tick = data.getInt("tick");
            if (tick == lastTick) {
                world.setBlockBreakingInfo(getId(), pos, -1);

                data.putInt("breakingProgress", -1);
                data.putInt("tick", -1);
            }

            lastTick = tick;
        }
    }
}
