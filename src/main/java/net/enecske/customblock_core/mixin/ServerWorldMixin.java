package net.enecske.customblock_core.mixin;

import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess {
    @Shadow @Final private MinecraftServer server;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayerList()) {
            if (serverPlayerEntity != null && serverPlayerEntity.world == this) {
                double d = (double) pos.getX() - serverPlayerEntity.getX();
                double e = (double) pos.getY() - serverPlayerEntity.getY();
                double f = (double) pos.getZ() - serverPlayerEntity.getZ();
                if (d * d + e * e + f * f < 1024.0) {
                    serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
                }
            }
        }
    }
}
