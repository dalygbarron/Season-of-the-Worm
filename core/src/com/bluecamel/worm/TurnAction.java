package com.bluecamel.worm;

import com.badlogic.gdx.math.Vector2;

/**
 * Animates the player turning.
 */
public class TurnAction extends Action {
    private final Vector2 start = new Vector2();
    private final Vector2 delta = new Vector2();
    private final boolean clockwise;

    public TurnAction(Agent agent, boolean clockwise) {
        super(agent);
        this.clockwise = clockwise;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public float getTime() {
        return 0.3f;
    }

    @Override
    protected void step(float prop) {
        agent.direction.set(start).mulAdd(delta, prop);
    }

    @Override
    protected void init() {
        start.set(agent.direction);
        delta.set(start);
        Util.rotate(delta, clockwise);
        delta.sub(start);
    }

    @Override
    protected void finish() {
        agent.direction.set(start.x + delta.x, start.y + delta.y);
    }
}
