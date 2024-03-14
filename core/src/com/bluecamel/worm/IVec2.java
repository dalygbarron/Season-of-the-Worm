package com.bluecamel.worm;

/**
 * Two dimensional vector of integers.
 */
public class IVec2 {
    public int x = 0;
    public int y = 0;

    /**
     * Sets the value of the vector component wise.
     * @param x x component.
     * @param y y component.
     * @return itself for chaining.
     */
    public IVec2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets the value of the vector component wise from floats that will be cast
     * into ints.
     * @param x x component.
     * @param y y component.
     * @return itself for chaining.
     */
    public IVec2 set(float x, float y) {
        this.x = (int)x;
        this.y = (int)y;
        return this;
    }

    public IVec2 set(IVec2 vec) {
        x = vec.x;
        y = vec.y;
        return this;
    }

    /**
     * Sets the value of the vector to the x and z components of a 3d vector so
     * that the x goes to x and the z goes to y.
     * @param vec3 the vector to take values from.
     * @return itself for sensual chaining.
     */
    public IVec2 xzPlane(IVec3 vec3) {
        x = vec3.x;
        y = vec3.z;
        return this;
    }
}