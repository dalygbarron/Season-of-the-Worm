package com.bluecamel.worm;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture3D;
import com.badlogic.gdx.graphics.glutils.CustomTexture3DData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.nio.IntBuffer;

/**
 * Basically just a namespace for a bunch of static utility functions.
 */
public class Util {
    /**
     * Creates a 33x33x33 3d texture that can be used to map colours based on a
     * set of passed in curves.
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static Texture3D createColourMap(
        int detail,
        boolean linear,
        Vector2[] red,
        Vector2[] green,
        Vector2[] blue
    ) {
        CustomTexture3DData data = new CustomTexture3DData(
            detail,
            detail,
            detail,
            0,
            GL30.GL_RGBA,
            GL30.GL_RGBA8,
            GL30.GL_UNSIGNED_BYTE
        );
        IntBuffer buffer = data.getPixels().asIntBuffer();
        Color c = new Color(Color.BLACK);
        for (int b = 0; b < detail; b++) {
            for (int g = 0; g < detail; g++) {
                for (int r = 0; r < detail; r++) {
                    float total = r + g + b / (detail * 3f);
                    buffer.put(c.set(
                        r / (float)(detail - 1),
                        g / (float)(detail - 1),
                        b / (float)(detail - 2) + 1.9f / detail,
                        1
                    ).toIntBits());
                }
            }
        }
        buffer.flip();
        Texture3D tex = new Texture3D(data);
        if (linear) {
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return tex;
    }

    public static Vector2 rotate(Vector2 victim, Vector2 direction) {
        float xx = direction.y;
        float xy = -direction.x;
        float yx = direction.x;
        float yy = direction.y;
        victim.set(victim.x * xx + victim.y * yx, victim.x * xy + victim.y * yy);
        return victim;
    }

    /**
     * Rotates a vector by 90 degrees, mutating it, and returns it.
     * @param victim is the vector to rtotate.
     * @param clockwise if true rotate clockwise, else the other way.
     * @return the victim.
     */
    public static Vector2 rotate(Vector2 victim, boolean clockwise) {
        float temp = victim.y;
        if (clockwise) {
            victim.y = -victim.x;
            victim.x = temp;
        } else {
            victim.y = victim.x;
            victim.x = -temp;
        }
        return victim;
    }
}
