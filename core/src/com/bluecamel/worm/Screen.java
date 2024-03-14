package com.bluecamel.worm;

import com.badlogic.gdx.input.GestureDetector;

/**
 * A main thing the game does so to speak. It renders to the screen and updates
 * it's state each frame.
 */
public abstract class Screen implements GestureDetector.GestureListener {
    private Screen next = null;
    protected Assets assets;

    /**
     * Creates the screen and gives it the game assets object.
     * @param assets is the game's set of assets and crap which all screens
     *               should have a reference to because it has useful stuff.
     */
    public Screen(Assets assets) {
        this.assets = assets;
    }

    /**
     * This should be called whenever the window is resized in case the screen
     * has a camera that it needs to reconfigure and stuff like that. By
     * default though, it does nothing.
     */
    public void onResize() {}

    /**
     * Returns a scene that should be placed upon the scene stack after this
     * screen has been updated.
     * @return a screen if there is one, or null if nothing.
     */
    public Screen getNext() {
        return next;
    }

    /**
     * Updates the screen.
     * @param delta is the amount of passed time to handle.
     * @return true if the current scene should stay on the scene stack.
     */
    public abstract boolean update(float delta);

    /**
     * Renders this screen to the screen.
     */
    public abstract void render();

    /**
     * Disposes of any assets that need disposing of.
     */
    public abstract void dispose();

    /**
     * Helper function for screen transitions. Call this function and return the
     * result within the update function to pop the screen from the screen
     * stack.
     * @return false which means remove this screen from the scene stack when
     *         returned from the update function.
     */
    protected boolean pop() {
        return false;
    }

    /**
     * Helper function for screen transitions. Call this function and return the
     * result from the update function to push another screen on top of this one
     * on the screen stack.
     * @param screen the screen to push.
     * @return true which means don't remove this screen from the stack when
     *         returned from the update function.
     */
    protected boolean push(Screen screen) {
        next = screen;
        return true;
    }

    /**
     * Helper function for screen transitions.
     * @param screen is the screen to replace this one by.
     * @return false which means remove this screen from the stack when returned
     *         from the update function.
     */
    protected boolean replace(Screen screen) {
        next = screen;
        return false;
    }
}
