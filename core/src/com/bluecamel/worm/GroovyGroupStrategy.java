package com.bluecamel.worm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

/**
 * Extension of camera group strategy that always returns a custom shader for use.
 */
public class GroovyGroupStrategy implements GroupStrategy {
    private final Camera camera;
    private final ShaderProgram shader;

    /**
     * Creates our groovy groovy group strat.
     * @param camera the camera that is used for whatever camera group strat does with it.
     * @param shader the shader which will be used always in this group.
     */
    public GroovyGroupStrategy(Camera camera, ShaderProgram shader) {
        this.camera = camera;
        this.shader = shader;
    }

    @Override
    public void beforeGroups () {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        shader.bind();
        shader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
        shader.setUniformi("u_texture", 0);
        shader.setUniformi("colourMap", 1);
    }

    @Override
    public void afterGroups() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    @Override
    public ShaderProgram getGroupShader(int group) {
        return shader;
    }

    @Override
    public int decideGroup(Decal decal) {
        return 0;
    }

    @Override
    public void beforeGroup(int group, Array<Decal> contents) {}

    @Override
    public void afterGroup(int group) {}
}
