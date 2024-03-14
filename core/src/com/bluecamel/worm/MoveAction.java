package com.bluecamel.worm;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;

/**
 * Move an agent from here to there relatively.
 */
public class MoveAction extends Action {
    private final IVec2 start = new IVec2();
    private final IVec2 direction = new IVec2();

    /**
     * Give them their agent and the direction but by compontents.
     * @param agent the agent.
     * @param x x direction.
     * @param y y direction.
     */
    public MoveAction(Agent agent, float x, float y) {
        super(agent);
        this.direction.set(x, y);
    }

    /**
     * Give them their agent and the direction.
     * @param agent     the agent.
     * @param direction the direction to go in.
     */
    public MoveAction(Agent agent, IVec2 direction) {
        super(agent);
        this.direction.set(direction);
    }

    @Override
    public float getTime() {
        return 0.4f;
    }

    @Override
    protected void step(float prop) {
        agent.setPosition(start.x + direction.x * prop, start.y, start.z + direction.y * prop);
    }

    @Override
    protected void init() {
        agent.getPosition(start);
        Util.rotate(direction, agent.direction);
    }

    @Override
    protected void finish() {
        start.set(start.x + direction.x, start.y, start.z + direction.y);
        agent.setPosition(start);
    }
}
