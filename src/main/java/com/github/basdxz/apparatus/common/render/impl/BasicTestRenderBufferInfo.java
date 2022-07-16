package com.github.basdxz.apparatus.common.render.impl;

import com.github.basdxz.apparatus.common.render.BasicRenderBufferLayout;
import com.github.basdxz.apparatus.common.render.IRenderBufferInfo;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public class BasicTestRenderBufferInfo implements IRenderBufferInfo<BasicRenderBufferLayout> {
    public static final IRenderBufferInfo<BasicRenderBufferLayout> INSTANCE = new BasicTestRenderBufferInfo();

    @Override
    public BasicRenderBufferLayout layout() {
        return BasicRenderBufferLayout.INSTANCE;
    }

    @Override
    public int byteSize() {
        return TestRenderModel.requiredBufferByteSize();
    }
}