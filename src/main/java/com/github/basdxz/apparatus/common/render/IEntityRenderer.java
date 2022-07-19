package com.github.basdxz.apparatus.common.render;

import lombok.*;

import java.util.List;

//TODO: Check rendering capability
//TODO: Register resources
public interface IEntityRenderer {
    List<IRenderer> renderers();

    IRenderer renderer(@NonNull IRenderView view);
}