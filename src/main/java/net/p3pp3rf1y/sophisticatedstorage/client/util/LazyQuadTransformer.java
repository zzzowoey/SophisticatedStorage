package net.p3pp3rf1y.sophisticatedstorage.client.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public class LazyQuadTransformer implements RenderContext.QuadTransform {
    ObjectArrayList<RenderContext.QuadTransform> stack = new ObjectArrayList<>();

    @Override
    public boolean transform(MutableQuadView quad) {
        for (int i = 0; i < stack.size(); i++) {
            if (!stack.get(i).transform(quad)) {
                return false;
            }
        }
        return true;
    }

    public void add(RenderContext.QuadTransform transformer) {
        stack.add(transformer);
    }
}
