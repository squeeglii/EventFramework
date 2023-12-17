package me.squeeglii.plugin.eventfw.session.type.blockhunt;

import org.bukkit.block.Block;

public class HiddenBlockTracker {

    public static final int TICKS_TO_SETTLE = 40;

    private Block block;
    private int ticksStoodStill;

    private boolean isApplied = false;


    // Everyone has a tracker but only hiders use the ticking.
    private boolean apply() {
        if(this.isApplied)
            return false;

        this.ticksStoodStill = 0;

        // TODO: create entity

        this.isApplied = true;
    }


    public void tickStoodStill() {
        if(!this.isApplied) return;

        // TODO: Snap entity to world.
    }

    public void onMoveTick() {
        if(!this.isApplied) return;

        this.ticksStoodStill = 0;

        //TODO: Re-snap entity to user pos.
    }

    public Block getBlock() {
        return this.block;
    }

    public double getSettleProgress() {
        return (double) this.ticksStoodStill / TICKS_TO_SETTLE;
    }

    public boolean isApplied() {
        return this.isApplied;
    }
}
