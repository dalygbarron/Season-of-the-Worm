package com.bluecamel.worm;

import com.badlogic.gdx.Gdx;

/**
 * Rectnalge that uses integers since the libgdx one uses floats which we then need to convert back
 * to ints.
 */
public class IRect {
    public int x = 0;
    public int y = 0;
    public int w = 0;
    public int h = 0;

    /**
     * Creates it with default values which are all 0.
     */
    public IRect() {}

    /**
     * Creates the rectangle and sets all it's junk.
     * @param x position from left.
     * @param y position from top.
     * @param w width.
     * @param h height.
     */
    public IRect(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Sets all the fields.
     * @param x sets x.
     * @param y sets y.
     * @param w sets w.
     * @param h sets h.
     */
    public void set(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Sets this rectangle as the opengl viewport.
     */
    public void glViewport() {
        Gdx.gl.glViewport(x, y, w, h);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %d)", x, y, w, h);
    }
}
