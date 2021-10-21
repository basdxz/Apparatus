package com.github.basdxz.paratileentity.defenition.tile;

import com.github.basdxz.paratileentity.defenition.IParaTileManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public abstract class ParaTile implements Cloneable, IParaTile {
    protected final int tileID;
    protected IParaTileManager manager;

    // region Modified Lombok @SuperBuilder
    protected ParaTile(ParaTileBuilder<?, ?> b) {
        this.tileID = b.tileID;
    }

    public static abstract class ParaTileBuilder<C extends ParaTile, B extends ParaTileBuilder<C, B>> {
        private int tileID;

        public B tileID(int tileID) {
            this.tileID = tileID;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "ParaTile.ParaTileBuilder(tileID=" + this.tileID + ")";
        }
    }
    // endregion

    @Override
    public void registerManager(IParaTileManager manager) {
        if (this.manager != null)
            throw new IllegalStateException("Manager already registered.");
        this.manager = manager;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void updateEntity() {
    }

    @Override
    public ParaTile clone() {
        try {
            return (ParaTile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Failed to create ParaTile!");
        }
    }
}