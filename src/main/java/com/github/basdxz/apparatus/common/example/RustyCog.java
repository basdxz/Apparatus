package com.github.basdxz.apparatus.common.example;

import com.github.basdxz.apparatus.common.parathing.IParaItem;
import com.github.basdxz.apparatus.common.render.IRendererView;
import com.github.basdxz.apparatus.common.resource.IRenderer;

import java.util.Map;

//Has a fancy description with a shift key alt description
//NBT storing how many times it's been pressed
public class RustyCog implements IParaItem {
    @Override
    public String paraID() {
        return null;
    }

    @Override
    public String localizedName() {
        return null;
    }

    @Override
    public Map<IRendererView, IRenderer> renderers() {
        return null;
    }
}
