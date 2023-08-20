package net.p3pp3rf1y.sophisticatedstorage.client.util;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class QuaternionHelper {
    public static Quaternionf quatFromXYZDegree(Vector3f xyz) {
        return quatFromXYZDegree(xyz.x, xyz.y, xyz.z);
    }
    public static Quaternionf quatFromXYZDegree(float x, float y, float z) {
        return new Quaternionf().rotateXYZ(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }
}
