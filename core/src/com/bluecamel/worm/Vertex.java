package com.bluecamel.worm;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;

/**
 * Helps in the construction of geometry by storing vertex data temporarily.
 */
public class Vertex {
    public final Vector3 pos = new Vector3();
    public final Vector2 uv = new Vector2();
    public final Vector4 col = new Vector4(1, 1, 1, 1);

    /**
     * Creates the vertex by component.
     * @param x x position.
     * @param y y position.
     * @param z z position.
     * @param u x texture coorrdinate.
     * @param v y texture coordinate.
     */
    public Vertex(float x, float y, float z, float u, float v) {
        pos.x = x;
        pos.y = y;
        pos.z = z;
        uv.x = u;
        uv.y = v;
    }

    @Override
    public String toString() {
        return String.format("v((%f, %f, %f) (%f, %f))", pos.x, pos.y, pos.z, uv.x, uv.y);
    }

    /**
     * Updates the tile's UVs so that it's like the old uvs were relative to a
     * specified tile in a 4x4 grid that covers the overall area.
     * @param idx is the index of the tile inside the grid under the assumption
     *            that it counts along rows then down.
     * @return itself.
     */
    public Vertex grid(int idx) {
        float x = 0.25f * (idx % 4);
        float y = 0.25f * (int)(idx / 4);
        uv.x = x + uv.x * 0.25f;
        uv.y = y + uv.y * 0.25f;
        return this;
    }

    public Vertex subGrid(int idx, int subIdx) {
        float x = 0.25f * (idx % 4) + 0.125f * (subIdx % 2);
        float y = 0.25f * (int)(idx / 4) + 0.125f * (int)(subIdx / 2);
        uv.x = x + uv.x * 0.125f;
        uv.y = y + uv.y * 0.125f;
        return this;
    }
}
