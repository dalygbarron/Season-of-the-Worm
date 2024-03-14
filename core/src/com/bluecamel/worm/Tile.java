package com.bluecamel.worm;

/**
 * Contains all the data for an archetypal tile and can generate vertices
 * for it.
 * This class is designed to be subclassed when you want to make it generate
 * tiles that have got different
 */
public abstract class Tile {
    public final boolean opaque;
    public final boolean passable;

    public Tile(boolean opaque, boolean passable) {
        this.opaque = opaque;
        this.passable = passable;
    }

    /**
     * Gives you a character that can be used to represent this tile.
     * @return a character.
     */
    public abstract char toChar();

    /**
     * Generates the vertices for this tile in local coordinates. The format of
     * the vertex data that must be generated is that it always uses triangles
     * as primitives, and it goes x,y,z,u,v for each vertex sequentially, so the
     * number of floats returned should be a multiple of 15.
     * @param north is the tile to the north or null if none.
     * @param south is the tile to the south or null if none.
     * @param east is the tile to the east or null if none.
     * @param west is the tile to the west or null if none.
     * @return array of vertices whose number should be a multiple of 3 since
     *         it is all in triangles.
     */
    public abstract Vertex[] generateVertices(
        Tile north,
        Tile south,
        Tile east,
        Tile west
    );
}
