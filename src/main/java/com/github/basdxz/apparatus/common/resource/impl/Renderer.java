package com.github.basdxz.apparatus.common.resource.impl;

import com.github.basdxz.apparatus.common.domain.IDomain;
import com.github.basdxz.apparatus.common.resource.IModel;
import com.github.basdxz.apparatus.common.resource.IRenderer;
import lombok.*;
import lombok.experimental.*;

import java.util.Collections;
import java.util.List;

import static com.github.basdxz.apparatus.common.resource.impl.BlockModel.newDefaultBlockModel;
import static com.github.basdxz.apparatus.common.resource.impl.SpriteModel.newDefaultSpriteModel;

@Data
@Accessors(fluent = true, chain = true)
public class Renderer implements IRenderer {
    protected final List<IModel> models;

    public Renderer(@NonNull List<IModel> models) {
        this.models = Collections.unmodifiableList(models);
    }

    public static IRenderer newDefaultSpriteRenderer(@NonNull IDomain domain, @NonNull String path) {
        return new Renderer(Collections.singletonList(newDefaultSpriteModel(domain, path)));
    }

    public static IRenderer newDefaultBlockRenderer(@NonNull IDomain domain, @NonNull String path) {
        return new Renderer(Collections.singletonList(newDefaultBlockModel(domain, path)));
    }
}
