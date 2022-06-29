package com.github.basdxz.apparatus.tiger;

import com.github.basdxz.apparatus.common.loader.IInitializeable;
import com.github.basdxz.apparatus.common.parathing.IParaBlock;
import com.github.basdxz.apparatus.common.parathing.IParaItem;
import com.github.basdxz.apparatus.common.parathing.IParaThing;
import com.github.basdxz.apparatus.common.registry.IParaManager;
import lombok.*;

public class RegistryAdapter implements IInitializeable {
    protected final IParaManager registry;

    public RegistryAdapter(@NonNull IParaManager registry) {
        this.registry = registry;
    }

    @Override
    public void preInit() {
        registry.preInit();
        registry.paraThings().forEach(this::adapt);
    }

    //TODO: Rethink this
    public Object adapt(@NonNull IParaThing paraThing) {
        if (paraThing instanceof IParaBlock)
            return new ParaBlockAdapter((IParaBlock) paraThing);
        if (paraThing instanceof IParaItem)
            return new ParaItemAdapter((IParaItem) paraThing);
        return null;
    }

    @Override
    public void init() {
        registry.init();
    }

    @Override
    public void postInit() {
        registry.postInit();
    }
}
