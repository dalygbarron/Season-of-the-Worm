package com.bluecamel.worm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture3D;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import java.util.ArrayList;

/**
 * Represents a level of the game so to speak. Keeps track of the overall
 * layout of it, the pathfinding data and such associated with it, and can
 * generate a 3d mesh representation of it.
 */
public class Floor extends Screen {
    public static final int FIELD_OF_VIEW = 128;
    private final static Array<IVec2> pointList = new Array<>();
    private final Texture3D colourMap;
    public final Vector2 size;
    private final Tile[][] tiles;
    private final byte[][][] computedMaps;
    private Mesh mesh;
    private boolean dirty = true;
    private final ShapeRenderer shape = new ShapeRenderer();
    private final Array<Agent> agents = new Array<>();
    private final PerspectiveCamera camera = new PerspectiveCamera(
        FIELD_OF_VIEW,
        assets.worldViewport.w,
        assets.worldViewport.h
    );
    private final SpriteBatch batch = new SpriteBatch();
    private final DecalBatch decalBatch = new DecalBatch(
        new GroovyGroupStrategy(camera, assets.mainShader)
    );
    private int agentCursor = 0;
    private Agent player;
    private Action currentAction;
    private final Array<Action> playerActionQueue = new Array<>();

    /**
     * Creates the floor.
     * @param assets is the game's assets.
     * @param size is the width and breadth of the floor.
     */
    public Floor(Assets assets, Vector2 size) {
        super(assets);
        colourMap = Util.createColourMap(
            21,
            true,
            new Vector2[]{},
            new Vector2[]{},
            new Vector2[]{}
        );
        this.size = size;
        tiles = new Tile[(int)size.y][(int)size.x];
        computedMaps = new byte[Layer.N.index][(int)size.y][(int)size.x];
        camera.near = 0.1f;
        camera.far = 30;
        camera.position.set(5.5f, 1, 5.5f);
        camera.lookAt(6, 1, 5.5f);
        camera.update();
    }

    @Override
    public void onResize() {
        camera.fieldOfView = FIELD_OF_VIEW;
        camera.viewportWidth = assets.worldViewport.w;
        camera.viewportHeight = assets.worldViewport.h;
        camera.update();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int z = 0; z < size.y; z++) {
            for (int x = 0; x < size.x; x++) {
                Tile t = getTile(x, z);
                if (t == null) sb.append(' ');
                else sb.append(t.toChar());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Gives you the mesh that represents this floor after potentially
     * generating it if it did not already exist or was out of date.
     * @return the mesh.
     */
    public Mesh getMesh() {
        if (!dirty) return mesh;
        if (mesh != null) mesh.dispose();
        ArrayList<Vertex[]> vertSets = new ArrayList<>();
        int n = 0;
        for (int x = 0; x < size.x; x++) {
            for (int z = 0; z < size.y; z++) {
                Tile tile = getTile(x, z);
                if (tile == null) continue;
                Vertex[] tileVerts = tile.generateVertices(
                    getTile(x, z + 1),
                    getTile(x, z - 1),
                    getTile(x - 1, z),
                    getTile(x + 1, z)
                );
                for (int i = 0; i < tileVerts.length; i++) {
                    // don't modify vertex objects directly, can be reused.
                    Vertex v = tileVerts[i];
                    tileVerts[i] = new Vertex(
                        v.pos.x + x,
                        v.pos.y,
                        v.pos.z + z,
                        v.uv.x,
                        v.uv.y
                    );
                }
                vertSets.add(tileVerts);
                n += tileVerts.length;
            }
        }
        float[] verts = new float[n * 9];
        int i = 0;
        for (Vertex[] vertSet: vertSets) {
            for (Vertex v: vertSet) {
                verts[i++] = v.pos.x;
                verts[i++] = v.pos.y;
                verts[i++] = v.pos.z;
                verts[i++] = v.col.x;
                verts[i++] = v.col.y;
                verts[i++] = v.col.z;
                verts[i++] = v.col.w;
                verts[i++] = v.uv.x;
                verts[i++] = v.uv.y;
            }
        }
        mesh = new Mesh(
            true,
            n,
            0,
            new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE ),
            new VertexAttribute(Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE ),
            new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0")
        );
        mesh.setVertices(verts);
        dirty = false;
        return mesh;
    }

    /**
     * Tells you the computed value for the map type at the location with a
     * default value for out of bounds checks.
     * @param map the computed map to look at.
     * @param x x position to look at.
     * @param z z position to look at.
     * @param revert if x or z is out of bounds it reverts to this value.
     * @return the value found.
     */
    public byte getData(Layer map, int x, int z, byte revert) {
        if (x < 0 || x >= size.x || z < 0 || z >= size.y) return revert;
        return computedMaps[map.index][z][x];
    }

    /**
     * Sets the computed value for the given map at the given location. Out of
     * bounds writes are just ignored.
     * @param map the computed map to update.
     * @param x x position.
     * @param z z position.
     * @param value value to write.
     */
    public void setData(Layer map, int x, int z, byte value) {
        if (x < 0 || x >= size.x || z < 0 || z >= size.y) return;
        computedMaps[map.index][z][x] = value;
    }

    /**
     * Gets the tile at the specified location within the floor. The floor is
     * square going from 0,0 in one corner to size.x,size.y in the other. If you
     * ask about an invalid location you will get null.
     * @param x horizontal location.
     * @param z depth of location.
     * @return the found tile or null if there is presently no tile there or it
     *         is out of bounds.
     */
    public Tile getTile(int x, int z) {
        if (x < 0 || x >= size.x || z < 0 || z >= size.y) return null;
        return tiles[z][x];
    }

    /**
     * Sets a tile on the floor to the given value. If you try to modify an
     * invalid location it simply does nothing.
     * @param x horizontal location.
     * @param z depth location.
     * @param tile is the tile to place.
     */
    public void setTile(int x, int z, Tile tile) {
        if (x < 0 || x >= size.x || z < 0 || z >= size.y) return;
        tiles[z][x] = tile;
        dirty = true;
    }

    /**
     * Adds a copy of the given agent to the floor at the given location.
     * @param parent is the agent this one will be a copy of.
     * @param x x position to give it.
     * @param z z position to give it.
     */
    public void addAgent(Agent parent, int x, int z) {
        Agent agent = Pools.obtain(Agent.class).init(parent);
        agent.setPosition(x, 0, z);
        agents.add(agent);
        if (agent.getBehaviour() == Agent.Behaviour.PLAYER) {
            player = agent;
            follow(agent);
            recalculateMaps();
        }
    }

    @Override
    public boolean update(float delta) {
        // make it that the no current action case runs conditionally then
        // the main case runs every time, since it will be afterwards.
        if (currentAction != null) {
            if (playerActionQueue.notEmpty() && currentAction.agent != player) {
                currentAction.skip();
            }
            boolean more = currentAction.next();
            if (more) {
                if (currentAction.isFinal()) {
                    if (currentAction.agent == player) recalculateMaps();
                    agentCursor++;
                }
                currentAction = null;
            }
        } else {
            if (agents.isEmpty()) return true;
            if (agentCursor >= agents.size) agentCursor = 0;
            Agent currentAgent = agents.get(agentCursor);
            if (currentAgent == player) {
                if (playerActionQueue.notEmpty()) {
                    currentAction = playerActionQueue.pop();
                }
            } else {
                currentAction = currentAgent.update(this);
            }
        }
        return true;
    }

    @Override
    public void render() {
        assets.worldViewport.glViewport();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        follow(player);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glDepthRangef(0, 30);
        colourMap.bind(1);
        assets.getTileset().bind(0);
        assets.mainShader.bind();
        assets.mainShader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
        assets.mainShader.setUniformi("u_texture", 0);
        assets.mainShader.setUniformi("colourMap", 1);
        getMesh().render(assets.mainShader, GL20.GL_TRIANGLES);
        for (Agent agent: agents) {
            Decal decal = agent.getDecal();
            if (decal != null) {
                decal.lookAt(camera.position, camera.up);
                decalBatch.add(decal);
            }

        }
        decalBatch.flush();
        TextureRegion holder = assets.uiAtlas.findRegion("itemHolder");
        TextureRegion grad = assets.uiAtlas.findRegion("gradient");
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(
            grad,
            0,
            assets.worldViewport.y,
            Gdx.graphics.getWidth(),
            grad.getRegionHeight()
        );
        for (int i = 0; i < 4; i++) {
            batch.draw(holder, i * assets.w4, 0, assets.w4, assets.w4);
            batch.draw(holder, i * assets.w4, assets.w4, assets.w4, assets.w4);
        }
        if (currentAction != null) {
            assets.mainFont.draw(batch, currentAction.toString(), 0f, 500f);
        }
        batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLUE);
        for (int x = 0; x < size.x; x++) {
            for (int z = 0; z < size.y; z++) {
                byte path = getData(Layer.PATH, x, z, Byte.MAX_VALUE);
                shape.setColor(path / (float)Byte.MAX_VALUE, 0, 0, 1);
                shape.rect(15 + x * 20, Gdx.graphics.getHeight() - z * 20 - 15, 20, 20);
                //Tile tile = getTile(x, z);
                //if (tile != null && !tile.passable){
                //    shape.rect(15 + x * 20, Gdx.graphics.getHeight() - z * 20 - 15, 20, 20);
                //}
            }
        }
        for (Agent agent: agents) {
            shape.setColor(agent == player ? Color.GREEN : Color.WHITE);
            shape.ellipse(15 + agent.pos.x * 20, Gdx.graphics.getHeight() - agent.pos.z * 20 - 15, 20, 20);
        }
        shape.end();
    }

    @Override
    public void dispose() {
        colourMap.dispose();
        if (mesh != null) mesh.dispose();
        batch.dispose();
        decalBatch.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (player == null) return false;
        if (Math.abs(velocityY) > Math.abs(velocityX)) {
            addPlayerAction(new MoveAction(player, 0, velocityY > 0 ? 1 : -1));
            return false;
        }
        addPlayerAction(new TurnAction(
            player,
            velocityX > 0
        ));
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {}

    /**
     * The types of computed maps that we have got.
     */
    public enum Layer {
        PATH(0),
        VISIBILITY(1),
        STATIONARY(2),
        N(3);

        public final int index;

        /**
         * Creates the thing and gives it an index.
         * @param index the index of the map in a list of computed maps.
         */
        Layer(int index) {
            this.index = index;
        }
    }

    private void recalculateMaps() {
        for (int x = 0; x < size.x; x++) {
            for (int z = 0; z < size.y; z++) {
                setData(Layer.PATH, x, z, Byte.MAX_VALUE);
            }
        }
        pointList.clear();
        pointList.add(Pools.obtain(IVec2.class).set(
            player.pos.x,
            player.pos.z
        ));
        setData(Layer.PATH, (int)player.pos.x, (int)player.pos.z, (byte)0);
        while (pointList.notEmpty()) {
            IVec2 point = pointList.pop();
            byte next = (byte)(getData(Layer.PATH, point.x, point.y, Byte.MAX_VALUE) + 1);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if ((x == 0 && z == 0) || (x != 0 && z != 0)) continue;
                    int pX = point.x + x;
                    int pZ = point.y + z;
                    if (pX < 0 || pX >= size.x || pZ < 0 || pZ >= size.y) continue;
                    if (!getTile(pX, pZ).passable) continue;
                    byte value = getData(Layer.PATH, pX, pZ, (byte)0);
                    if (value > next) {
                        setData(Layer.PATH, pX, pZ, next);
                        pointList.add(Pools.obtain(IVec2.class).set(pX, pZ));
                    }
                }
            }
            Pools.free(point);
        }
    }

    private void addPlayerAction(Action action) {
        playerActionQueue.insert(0, action);
    }

    /**
     * Sets the camera to the given agent's position and direction.
     * @param agent the agent to follow. Probably the player.
     */
    private void follow(Agent agent) {
        agent.getPosition(camera.position);
        camera.position.x += 0.5f;
        camera.position.y = (float)Math.floor(camera.position.y) + 1f;
        camera.position.z += 0.5f;
        camera.lookAt(
            camera.position.x + agent.direction.x,
            camera.position.y,
            camera.position.z + agent.direction.y
        );
        camera.update();
    }
}
