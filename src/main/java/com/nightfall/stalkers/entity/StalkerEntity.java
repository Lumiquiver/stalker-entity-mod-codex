package com.nightfall.stalkers.entity;

import com.nightfall.stalkers.NightfallStalkers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.random.Random;

/** A light-shy hunter that turns a close encounter into a dream. */
public final class StalkerEntity extends HostileEntity {
    private static final RegistryKey<World> FRACTURED_DREAM = RegistryKey.of(RegistryKeys.WORLD, NightfallStalkers.id("fractured_dream"));
    private int teleportCooldown;

    public StalkerEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 12;
    }

    public static DefaultAttributeContainer.Builder createStalkerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 34.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.31D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 42.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.45D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.75D));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 48.0F));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static boolean canSpawn(EntityType<StalkerEntity> type, ServerWorldAccess world, SpawnReason reason,
                                   BlockPos pos, Random random) {
        return world.getLightLevel(pos) <= 3 && HostileEntity.canSpawnInDark(type, world, reason, pos, random);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.teleportCooldown > 0) this.teleportCooldown--;
        if (this.getWorld().isClient) return;

        PlayerEntity player = this.getTarget();
        if (player == null || player.isCreative() || player.isSpectator()) return;
        int light = this.getWorld().getLightLevel(this.getBlockPos());
        if (light > 7) {
            this.setTarget(null);
            if (this.age % 40 == 0) this.teleportRandomly();
            return;
        }

        // A visible blink makes stalking feel unnatural without granting free hits.
        if (this.squaredDistanceTo(player) > 144.0D && this.age % 100 == 0) this.blinkBehind(player);
        if (this.squaredDistanceTo(player) < 16.0D && this.age % 20 == 0) trapInDream(player);
    }

    private void trapInDream(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer) || this.teleportCooldown > 0) return;
        ServerWorld dream = serverPlayer.getServer().getWorld(FRACTURED_DREAM);
        if (dream == null || serverPlayer.getWorld().getRegistryKey().equals(FRACTURED_DREAM)) return;

        BlockPos destination = dream.getSpawnPos().up();
        serverPlayer.teleport(dream, destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D, serverPlayer.getYaw(), serverPlayer.getPitch());
        serverPlayer.sendMessage(net.minecraft.text.Text.translatable("message.nightfall.dream_trap"), true);
        dream.spawnParticles(ParticleTypes.ASH, serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(), 80, 1.2D, 1.4D, 1.2D, 0.03D);
        this.playSound(SoundEvents.ENTITY_ENDERMAN_STARE, 1.2F, 0.45F);
        this.teleportCooldown = 18_000;
    }

    private void blinkBehind(LivingEntity target) {
        Vec3d direction = target.getRotationVec(1.0F).normalize();
        Vec3d point = target.getPos().subtract(direction.multiply(8.0D));
        BlockPos floor = this.getWorld().getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockPos.ofFloored(point));
        this.refreshPositionAndAngles(floor.getX() + 0.5D, floor.getY(), floor.getZ() + 0.5D, this.getYaw(), this.getPitch());
        this.getWorld().spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0D, this.getZ(), 14, 0.3D, 0.8D, 0.3D, 0.01D);
        this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.7F, 0.55F);
    }

    private void teleportRandomly() {
        this.refreshPositionAndAngles(this.getX() + this.random.nextInt(17) - 8, this.getY(), this.getZ() + this.random.nextInt(17) - 8, this.getYaw(), this.getPitch());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient && this.random.nextFloat() < 0.22F) teleportRandomly();
        return super.damage(source, amount);
    }
}
