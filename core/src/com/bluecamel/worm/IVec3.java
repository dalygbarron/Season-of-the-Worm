package com.bluecamel.worm;

import com.badlogic.gdx.math.Vector3;

/**
 * Three dimensional vector of integers.
 */
public class IVec3 {
    public int x = 0;
    public int y = 0;
    public int z = 0;

    /**
     * Sets the vector component wise.
     * @param x x component.
     * @param y y component.
     * @param z z component.
     * @return itself for chaining.
     */
    public IVec3 set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * copies another vector.
     * @param vec the vector to copy.
     * @return this for chaining.
     */
    public IVec3 set(IVec3 vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        return this;
    }

    /**
     * Copies another float vector, truncating the values by casting them.
     * @param vec the vector to copy.
     * @return itself for chaining.
     */
    public IVec3 set(Vector3 vec) {
        x = (int)vec.x;
        y = (int)vec.y;
        z = (int)vec.z;
        return this;
    }
}
