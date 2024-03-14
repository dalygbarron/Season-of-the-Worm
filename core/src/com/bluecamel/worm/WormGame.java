package com.bluecamel.worm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture3D;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.CustomTexture3DData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * ThE WHOLE GAME YEEAA.
 */
public class WormGame extends ApplicationAdapter {
	final Array<Screen> screenStack = new Array<>();
	Assets assets;
	Floor floor;
	
	@Override
	public void create () {
		assets = new Assets();
		Music bgm = Gdx.audio.newMusic(Gdx.files.internal("music/menu.ogg"));
		bgm.play();
		Tile tile = new GroundTile(null);
		Tile wall = new WallTile();
		floor = new Floor(assets, new Vector2(20, 20));
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 20; z++) {
				if (x == 0 || x == 19 || z == 0 || z == 19) {
					floor.setTile(x, z, wall);
				} else {
					double level = Math.random();
					floor.setTile(x, z, level < 0.65 ? tile : wall);
				}
			}
		}
		floor.addAgent(assets.agents.get("player"), 5, 5);
		floor.addAgent(assets.agents.get("druid"), 6, 7);
		floor.addAgent(assets.agents.get("druid"), 6, 6);
		screenStack.add(floor);
		Gdx.input.setInputProcessor(new GestureDetector(floor));
	}

	@Override
	public void render () {
		assert(screenStack.size > 0);
		Screen screen = screenStack.peek();
		boolean keep = screen.update(Gdx.graphics.getDeltaTime());
		if (!keep) {
			screenStack.pop();
			screen.dispose();
		}
		Screen next = screen.getNext();
		if (next != null) {
			screenStack.add(next);
			Gdx.input.setInputProcessor(new GestureDetector(next));
		}
		assert(screenStack.size > 0);
		screenStack.peek().render();
	}
	
	@Override
	public void dispose () {
		assets.dispose();
	}
}
