package com.bluecamel.worm;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**
 * Tile that renders just as a flat plane with a single texture on it.
 */
public class GroundTile extends Tile {
    private final AtlasRegion region;

    /**
     * Creates the ground tile. They are always considered to not be opaque
     * and to be passable.
     * @param region texture region used for the ground.
     */
    public GroundTile(AtlasRegion region) {
        super(false, true);
        this.region = region;
    }

    @Override
    public char toChar() {
        return '.';
    }

    @Override
    public Vertex[] generateVertices(
        Tile north,
        Tile south,
        Tile east,
        Tile west
    ) {
        Vertex bl = new Vertex(0, 0, 0, 0, 0).subGrid(1, 0);
        Vertex br = new Vertex(1, 0, 0, 1, 0).subGrid(1, 0);
        Vertex tr = new Vertex(1, 0, 1, 1, 1).subGrid(1, 0);
        Vertex tl = new Vertex(0, 0, 1, 0, 1).subGrid(1, 0);
        Vertex ubl = new Vertex(0, 2, 0, 0, 0).grid(2);
        Vertex ubr = new Vertex(1, 2, 0, 1, 0).grid(2);
        Vertex utr = new Vertex(1, 2, 1, 1, 1).grid(2);
        Vertex utl = new Vertex(0, 2, 1, 0, 1).grid(2);
        return new Vertex[] {
            bl, br, tr, bl, tr, tl,
            utr, ubr, ubl, utl, utr, ubl
        };
    }
}
