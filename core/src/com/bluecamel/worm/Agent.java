package com.bluecamel.worm;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Represents an enemy or the player basically.
 */
public class Agent {
    public static final IVec2[] MOVE_DIRECTIONS = new IVec2[]{
        new IVec2().set(0, 1),
        new IVec2().set(0, -1),
        new IVec2().set(-1, 0),
        new IVec2().set(1, 0)
    };
    private Decal decal = null;
    private Behaviour behaviour = Behaviour.DUMB;
    private Action nextAction = null;
    public int maxHp = 0;
    public final IVec2 pos = new IVec2();
    public float direction;
    public int hp = 0;

    /**
     * Loads all of the agents that are described in some json file.
     * @param agentAtlas is a texture atlas which contains the sprites that the
     *                   agents in the file can use.
     * @param file is the file to load the data from.
     * @return a map of all the loaded agents with their names from the json.
     */
    public static ObjectMap<String, Agent> loadFromFile(TextureAtlas agentAtlas, FileHandle file) {
        ObjectMap<String, Agent> agents = new ObjectMap<>();
        JsonValue root = new JsonReader().parse(file);
        for (JsonValue child: root) {
            Agent agent = new Agent();
            if (child.has("sprite")) {
                agent.decal = Decal.newDecal(agentAtlas.findRegion(child.getString("sprite")));
            }
            agent.behaviour = Behaviour.valueOf(child.getString("behaviour"));
            agent.maxHp = child.getInt("maxHp");
            agents.put(child.name, agent);
        }
        return agents;
    }

    /**
     * Initialises the agent by copying the values of another parent (without
     * creating references to stuff in the parent don't worry.
     * @param parent the agent to copy.
     */
    public Agent init(Agent parent) {
        maxHp = parent.maxHp;
        if (parent.decal != null) {
            decal = Decal.newDecal(parent.decal.getTextureRegion(), true);
            decal.setDimensions(1, 2);
        } else {
            decal = null;
        }
        behaviour = parent.behaviour;
        hp = parent.maxHp;
        direction = parent.direction;
        pos.set(parent.pos);
        nextAction = null;
        return this;
    }

    /**
     * gamer gamer
     * @return decal if any.
     */
    public Decal getDecal() {
        return decal;
    }

    /**
     * Lets the agent take their next action.
     * @return the action they are taking.
     */
    public Action update(Floor floor) {
        if (nextAction != null) {
            Action temp = nextAction;
            nextAction = null;
            return temp;
        }
        return behaviour.update(floor);
        switch (behaviour) {
            default:
                IVec2 maybe = null;
                byte value = floor.getData(Floor.Layer.PATH, pos.x, pos.y, Byte.MAX_VALUE);
                for (IVec2 dir: MOVE_DIRECTIONS) {
                    byte spot = floor.getData(
                        Floor.Layer.PATH,
                        pos.x + dir.x,
                        pos.y + dir.y,
                        Byte.MAX_VALUE
                    );
                    if (spot < value) {
                        return new MoveAction(
                            this,
                            dir
                        );
                    } else if (spot == value && maybe == null) {
                        maybe = dir;
                    }
                }
                if (maybe != null) return new MoveAction(this, maybe);
                return new MoveAction(this, 0, 0);
        }
    }

    /**
     * Tells you the agent's behaviour.
     * @return the agent's behaviour.
     */
    public Behaviour getBehaviour() {
        return behaviour;
    }

    /**
     * The types of behaviours that agents can follow which defines what actions
     * they can and will take in different circumstances.
     */
    public enum Behaviour {
        PLAYER,
        DUMB;

        public Action update(Floor floor) {

        }
    }
}
