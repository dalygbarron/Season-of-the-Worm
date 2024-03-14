package com.bluecamel.worm;

import com.badlogic.gdx.Gdx;

/**
 * Represents an action that an agent can take and facilitates processing
 * and displaying the action in an animated way.
 */
public abstract class Action {
    protected final Agent agent;
    protected float timer = 0;

    public Action(Agent agent) {
        this.agent = agent;
    }

    /**
     * Checks if this action can actually be done.
     * @param floor is the level the action is being performed in.
     * @return true if it can and false otherwise.
     */
    public boolean validate(Floor floor) {
        return true;
    }

    /**
     * Tells you how long this animation should go.
     *
     * @return the length of it.
     */
    public abstract float getTime();

    /**
     * Actions can be legitimate game actions or they can also be things
     * that just need to happen over time like the player rotating the
     * camera. If an action is final it means you go to the next agent next,
     * and if the action is not final then the agent gets to do another
     * action after.
     *
     * @return true iff final.
     */
    public boolean isFinal() {
        return true;
    }

    /**
     * Skips to the end if it is possible but if it is not possible it just
     * does nothing, so you still need to call next afterwards in all cases.
     */
    public void skip() {
        if (timer == 0) init();
        timer = getTime();
    }

    /**
     * Run the next step of the animation.
     *
     * @return false if there is more and true if it's finished.
     */
    public boolean next() {
        if (timer == 0) init();
        timer += Gdx.graphics.getDeltaTime();
        float limit = getTime();
        if (timer >= limit) {
            finish();
            return true;
        } else {
            step(timer / limit);
            return false;
        }
    }

    /**
     * Impolementation function for the animation.
     * @param prop is the proportion of the way through the animation.
     */
    protected abstract void step(float prop);

    /**
     * Gets run at the start of the action but when it actually begins to
     * run as opposed to the constructor which can be run just when it is
     * being queueud.
     */
    protected abstract void init();

    /**
     * Called at the end to set the values to the exact right values so we
     * don't get floating point errors etc.
     */
    protected abstract void finish();
}
