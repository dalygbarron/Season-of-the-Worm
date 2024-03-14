package com.bluecamel.worm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * This class contains all of the random data and crap that is needed all over
 * the application.
 */
public class Assets {
    public static final float MAIN_RATIO = 1.7777777f;
    public final IRect worldViewport = new IRect();
    public final Texture tileset;
    public final TextureAtlas uiAtlas;
    public final TextureAtlas spriteAtlas;
    public final ShaderProgram mainShader;
    public final ObjectMap<String, Agent> agents;
    public final BitmapFont mainFont;
    public float w4;
    public float h4;

    /**
     * Creates the nice assets object.
     */
    Assets() {
        onResize();
        tileset = new Texture(Gdx.files.internal("tiles.png"));
        uiAtlas = new TextureAtlas(Gdx.files.internal("ui.atlas"));
        spriteAtlas = new TextureAtlas(Gdx.files.internal("sprites.atlas"));
        mainShader = new ShaderProgram(
            Gdx.files.internal("basic.vert").readString(),
            Gdx.files.internal("basic.frag").readString()
        );
        agents = Agent.loadFromFile(spriteAtlas, Gdx.files.internal("agents.json"));
        mainFont = generateFont(Gdx.files.internal("fonts/ubuntu.ttf"), 48);
    }

    /**
     * Returns a reference to the tileset texture which is used to draw floors.
     * @return tileset texture.
     */
    Texture getTileset() {
        return tileset;
    }

    /**
     * This should be called any time the screen is resized and also once at the
     * start of the program.
     */
    void onResize() {
        int width = Gdx.graphics.getWidth();
        int realHeight = Gdx.graphics.getHeight();
        int height = Math.min(realHeight, (int)(width * MAIN_RATIO));
        worldViewport.set(0, realHeight - height, width, height);
        w4 = width / 4f;
        h4 = realHeight / 4f;
    }

    /**
     * Frees all the resources that need freeing.
     */
    void dispose() {
        tileset.dispose();
        uiAtlas.dispose();
        mainShader.dispose();
    }

    private BitmapFont generateFont(FileHandle file, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
