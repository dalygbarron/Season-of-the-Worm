package com.bluecamel.worm;

public class WallTile extends Tile {
    public WallTile() {
        super(true, false);
    }

    @Override
    public char toChar() {
        return '#';
    }

    @Override
    public Vertex[] generateVertices(Tile north, Tile south, Tile east, Tile west) {
        Vertex lfl = new Vertex(0, 0, 0, 0, 1).grid(0);
        Vertex lfr = new Vertex(1, 0, 0, 1, 1).grid(0);
        Vertex lbr = new Vertex(1, 0, 1, 0, 1).grid(0);
        Vertex lbl = new Vertex(0, 0, 1, 1, 1).grid(0);
        Vertex ufl = new Vertex(0, 2, 0, 0, 0).grid(0);
        Vertex ufr = new Vertex(1, 2, 0, 1, 0).grid(0);
        Vertex ubr = new Vertex(1, 2, 1, 0, 0).grid(0);
        Vertex ubl = new Vertex(0, 2, 1, 1, 0).grid(0);
        return new Vertex[] {
            lfl, lfr, ufr, lfl, ufr, ufl,
            lfr, lbr, ubr, lfr, ubr, ufr,
            lbr, lbl, ubl, lbr, ubl, ubr,
            lbl, lfl, ufl, lbl, ufl, ubl
        };
    }
}
