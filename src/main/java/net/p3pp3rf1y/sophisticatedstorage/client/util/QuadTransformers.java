package net.p3pp3rf1y.sophisticatedstorage.client.util;

import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public class QuadTransformers {
    public static RenderContext.QuadTransform applying(Transformation transform) {
        if (transform.equals(Transformation.identity()))
            return quad -> true;

        Matrix4f matrix = transform.getMatrix();
        Matrix3f normalMatrix = new Matrix3f(matrix).invert().transpose();

        return quad -> {
            for (int i = 0; i < 4; i++) {
                Vector4f pos = new Vector4f(quad.x(i), quad.y(i), quad.z(i), 1).mul(transform.getMatrix());
                pos.div(pos.w);
                quad.pos(i, pos.x(), pos.y(), pos.z());
            }

            for (int i = 0; i < 4; i++) {
                if (quad.hasNormal(i)) {
                    quad.normal(i,
                            new Vector3f(quad.normalX(i), quad.normalY(i), quad.normalZ(i))
                            .mul(normalMatrix)
                            .normalize()
                    );
                }
            }
            return true;
        };
    }

    public static class LazyQuadTransformer implements RenderContext.QuadTransform {
        final ObjectArrayList<RenderContext.QuadTransform> stack = new ObjectArrayList<>();

        @Override
        public boolean transform(MutableQuadView quad) {
			for (RenderContext.QuadTransform quadTransform : stack) {
				if (!quadTransform.transform(quad)) {
					return false;
				}
			}
            return true;
        }

        public void add(RenderContext.QuadTransform transformer) {
            stack.add(transformer);
        }
    }
}
