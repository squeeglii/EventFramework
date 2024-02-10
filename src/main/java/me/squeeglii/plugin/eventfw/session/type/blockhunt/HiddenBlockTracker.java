package me.squeeglii.plugin.eventfw.session.type.blockhunt;

import me.squeeglii.plugin.eventfw.EventFramework;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class HiddenBlockTracker {

    public static final int TICKS_TO_SETTLE = 40;

    private final Player trackedPlayer;
    private final BlockHuntEvent event;
    private final BlockData block;

    private BlockPos lastPosition;

    private BlockDisplay blockDisplayEntity;

    private int ticksStoodStill = 0;

    private boolean hasSettled = false;
    private boolean isEnabled = false;


    public HiddenBlockTracker(Player target, BlockHuntEvent event, BlockData block) {
        this.trackedPlayer = target;
        this.event = event;
        this.block = block;

        if(event.getWorld() == null)
            throw new IllegalStateException("Event world cannot be null. A world __must__ be assigned.");

        this.blockDisplayEntity = null;
    }


    // Everyone has a tracker but only hiders use the ticking.
    public boolean enable() {
        if(this.isEnabled)
            return false;

        if(this.blockDisplayEntity != null) {
            this.blockDisplayEntity.remove();
            this.blockDisplayEntity = null;
        }

        this.blockDisplayEntity = ((BlockDisplay) event.getWorld().spawnEntity(
                this.trackedPlayer.getLocation(),
                EntityType.BLOCK_DISPLAY
        ));
        this.blockDisplayEntity.setBlock(block);
        this.blockDisplayEntity.addScoreboardTag("efw_entity");
        this.blockDisplayEntity.teleport(this.trackedPlayer);

        this.isEnabled = true;
        this.hasSettled = false;
        this.ticksStoodStill = 0;

        this.updateVisualStateFor(this.trackedPlayer, false);

        return true;
    }


    public void tick() {
        if(!this.isEnabled)
            return;

        Location newPos = this.trackedPlayer.getLocation();
        BlockPos newPosSnapped = BlockPos.containing(newPos.x(), newPos.y(), newPos.z());

        this.blockDisplayEntity.teleport(this.trackedPlayer);
        this.blockDisplayEntity.setVelocity(this.trackedPlayer.getVelocity());
        
        if(this.lastPosition != null) {
            if(newPosSnapped.equals(this.lastPosition))
                this.onStandTick();
            else
                this.onMoveTick();
        }

        this.lastPosition = newPosSnapped;
    }

    public void onMoveTick() {
        if(!this.isEnabled) return;

        this.ticksStoodStill = 0;

        if(this.hasSettled) {
            this.unsettle();
        }
    }

    public void onStandTick() {
        if(!this.isEnabled) return;

        this.ticksStoodStill++;

        if(this.hasSettled) return;

        int settleTicks = this.event.getHiderSettleTime();

        if(this.ticksStoodStill < settleTicks) {
            // Update Counter
            return;
        }

        if(this.ticksStoodStill == settleTicks) {
            this.broadcastVisualState(false);
            this.settle();
            return;
        }

        // ...

    }

    public void broadcastVisualState(boolean shouldRevert) {
        for(Player player : this.event.getPlayerList()) {

        }
    }

    /**
     * Updates the visuals for a given viewer. Shows either a solid block or a display entity for hiders.
     * @param player the viewer to update the state for
     * @param revert if true, visuals should be returned to vanilla (i.e, player is leaving so why show a new state?)
     */
    public void updateVisualStateFor(Player player, boolean revert) {
        Location blockLocation = new Location(
                this.event.getWorld(),
                this.lastPosition.getX(),
                this.lastPosition.getY(),
                this.lastPosition.getZ()
        );

        if(this.hasSettled || !revert) {
            // Hide moving entity - send fake block
            player.hideEntity(EventFramework.plugin(), this.blockDisplayEntity);
            player.sendBlockChange(blockLocation, this.block);

        } else {
            // Show moving entity, ensure block is correct client-side
            player.showEntity(EventFramework.plugin(), this.blockDisplayEntity);

            BlockData serversideBlock = this.event.getWorld().getBlockData(blockLocation);
            player.sendBlockChange(blockLocation, serversideBlock);
        }
    }

    public void disable() {
        if(this.blockDisplayEntity != null) {
            this.blockDisplayEntity.remove();
            this.blockDisplayEntity = null;
        }
    }

    private void unsettle() {
        if(!this.hasSettled)
            return;

        this.hasSettled = false;

        this.broadcastVisualState(false);
    }

    private void settle() {
        if(this.hasSettled)
            return;

        this.hasSettled = true;
        this.broadcastVisualState(false);
    }

    public BlockData getBlock() {
        return this.block;
    }

    public double getSettleProgress() {
        return (double) this.ticksStoodStill / this.event.getHiderSettleTime();
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }
}
