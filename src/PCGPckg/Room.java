package PCGPckg;

import Colliders.BoxCollider;
import Colliders.Collider;
import Other.Item;
import Other.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private static final int UP = 0;
    private static final int BOTTOM = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    public static int HALF_CON_LEN = PCG.CONNECTOR_LENGTH/2;
    private static int CON_W = PCG.CONNECTOR_WIDTH;

    private Tile[][] tilePositions;
    private int tileSize;
    private PVector position;
    private float width;
    private float height;
    private List<BoxCollider> cols;
    private boolean createdConn = false;
    private List<Room> adjRooms;
    private List<Collider> triggers;
    private Item item = null;

    public Room(Tile[][] tilePositions, int tileSize, PVector position, float width, float height,
                List<BoxCollider> cols, boolean createdConn,
                List<Room> adjRooms, List<Collider> triggers) {
        this.tilePositions = tilePositions;
        this.tileSize = tileSize;
        this.position = position;
        this.width = width;
        this.height = height;
        this.cols = cols;
        this.createdConn = createdConn;
        this.adjRooms = adjRooms;
        this.triggers = triggers;
    }

    public Room(List<Rectangle> rects, int tileSize){
        this.tileSize = tileSize;
        this.position = rects.get(0).getPosition().copy();
        this.width = rects.get(0).getWidth() + 2*HALF_CON_LEN*tileSize;
        this.height = rects.get(0).getHeight() + 2*HALF_CON_LEN*tileSize;
        this.triggers = new ArrayList<>();

        addMainColliders(rects);
        setArray(rects.get(0));
        addMainRoom(rects.get(0));
        for (int i = 1; i < rects.size(); i++) {
            addConnector(rects.get(0), rects.get(i));
        }
        setColliders(false);
    }

    /**
     * add colliders to walls of main rooms given as list of Rectangle objects
     * @param rects main rooms given as list of Rectangle objects
     */
    private void addMainColliders(List<Rectangle> rects){
        this.cols = new ArrayList<>();
        Rectangle mainRoom = rects.get(0);
        //upper wall
        cols.add(UP, new BoxCollider(position.x, position.y,
                mainRoom.getWidth() + 2*HALF_CON_LEN*tileSize,
                HALF_CON_LEN*tileSize));
        //lower wall
        cols.add(BOTTOM, new BoxCollider(position.x, position.y + mainRoom.getHeight() + HALF_CON_LEN*tileSize,
                mainRoom.getWidth() + 2*HALF_CON_LEN*tileSize,
                HALF_CON_LEN*tileSize));
        //left wall
        cols.add(LEFT, new BoxCollider(position.x, position.y + HALF_CON_LEN*tileSize,
                HALF_CON_LEN*tileSize,
                mainRoom.getHeight()));
        //right wall
        cols.add(RIGHT, new BoxCollider(position.x + mainRoom.getWidth() + HALF_CON_LEN*tileSize,
                position.y + HALF_CON_LEN*tileSize,
                HALF_CON_LEN*tileSize,
                mainRoom.getHeight()));
        setColliders(false);
    }

    /**
     * set all collider enabled values to given value
     * @param val given value
     */
    private void setColliders(boolean val){
        for (int i = 0; i < cols.size(); i++) {
            cols.get(i).enabled = val;
        }
    }

    /**
     * split wall to 2 colliders if there is connector in the wall
     * @param coord1 first coordinate of the connector
     * @param coord2 second coordinate of the connector
     * @param side side on which the wall is
     */
    private void splitWall(int coord1, int coord2, int side) {
        BoxCollider box = cols.get(side);
        List<BoxCollider> splits = box.splitCollider(coord1, coord2, side < 2);
        for (int i = 0; i < splits.size(); i++) {
            splits.get(i).enabled = false;
        }
        cols.addAll(splits);
    }

    /**
     * set all tile positions in array to being wall
     * the tiles which are not, will be changed later
     * @param mainRect
     */
    private void setArray(Rectangle mainRect){
        int numOfWidthTiles = (int) (mainRect.getWidth() / tileSize);
        int numOfHeightTiles = (int) (mainRect.getHeight() / tileSize);
        tilePositions = new Tile[numOfHeightTiles + 2*HALF_CON_LEN][numOfWidthTiles + 2*HALF_CON_LEN];
        for (int i = 0; i < tilePositions.length; i++) {
            for (int j = 0; j < tilePositions[0].length; j++) {
                tilePositions[i][j] = Tile.WALL;
            }
        }
    }

    /**
     * change tile values in the array according to given room
     * @param mainRect room rectangle
     */
    private void addMainRoom(Rectangle mainRect){
        int numOfWidthTiles = (int) (mainRect.getWidth() / tileSize);
        int numOfHeightTiles = (int) (mainRect.getHeight() / tileSize);
        for (int i = HALF_CON_LEN; i < HALF_CON_LEN + numOfHeightTiles; i++) {
            for (int j = HALF_CON_LEN; j < HALF_CON_LEN + numOfWidthTiles; j++) {
                tilePositions[i][j] = Tile.FLOOR;
            }
        }
    }

    /**
     * add connector to given room rectangle
     * @param mainRect room rectangle to add connector to
     * @param connector connector rectangle
     */
    private void addConnector(Rectangle mainRect, Rectangle connector){
        PVector mainPos = mainRect.getPosition();
        PVector conPos = connector.getPosition();
        int diffX = (int) ((conPos.x - mainPos.x) / tileSize);
        int diffY = (int) ((conPos.y - mainPos.y) / tileSize);
        int numOfWidthTiles = (int) (mainRect.getWidth() / tileSize);
        int numOfHeightTiles = (int) (mainRect.getHeight() / tileSize);
        int xStart = -1;
        int yStart = -1;
        int xEnd = -1;
        int yEnd = -1;

        //from right
        if (conPos.x == mainPos.x + mainRect.getWidth()){
            xStart = HALF_CON_LEN + numOfWidthTiles;
            yStart = HALF_CON_LEN + diffY;
            xEnd = xStart + HALF_CON_LEN;
            yEnd = yStart + CON_W;
            splitWall((int) (position.y + yStart*tileSize), (int) (position.y + yEnd*tileSize), RIGHT);
        }
        //from left
        else if (conPos.x == mainPos.x - connector.getWidth()){
            xStart = 0;
            yStart = HALF_CON_LEN + diffY;
            xEnd = xStart + HALF_CON_LEN;
            yEnd = yStart + CON_W;
            splitWall((int) (position.y + yStart*tileSize), (int) (position.y + yEnd*tileSize), LEFT);
        }
        //from bottom
        else if (conPos.y == mainPos.y + mainRect.getHeight()){
            xStart = HALF_CON_LEN + diffX;
            yStart = HALF_CON_LEN + numOfHeightTiles;
            xEnd = xStart + CON_W;
            yEnd = yStart + HALF_CON_LEN;
            splitWall((int) (position.x + xStart*tileSize), (int) (position.x + xEnd*tileSize), BOTTOM);
        }
        //from up
        else if (conPos.y == mainPos.y - connector.getHeight()){
            xStart = HALF_CON_LEN + diffX;
            yStart = 0;
            xEnd = xStart + CON_W;
            yEnd = yStart + HALF_CON_LEN;
            splitWall((int) (position.x + xStart*tileSize), (int) (position.x + xEnd*tileSize), UP);
        }

        for (int i = yStart; i < yEnd; i++) {
            for (int j = xStart; j < xEnd; j++) {
                tilePositions[i][j] = Tile.CONN;
            }
        }
    }

    /**
     * set rooms adjacent to this room
     * @param allRooms list of all rooms in the game
     */
    public void setAdjacentRooms(List<Room> allRooms){
        adjRooms = new ArrayList<>();
        for (int i = 0; i < allRooms.size(); i++) {
            if (allRooms.get(i).position.equals(position))
                continue;
            PVector oPos = allRooms.get(i).position;
            float oWidth = allRooms.get(i).width;
            float oHeight = allRooms.get(i).height;
            if (position.x - (oWidth + 2*HALF_CON_LEN*tileSize) <= oPos.x
                    && position.x + (width + 2*HALF_CON_LEN*tileSize) >= oPos.x
                    && position.y - (oHeight + 2*HALF_CON_LEN*tileSize) <= oPos.y
                    && position.y + (height + 2*HALF_CON_LEN*tileSize) >= oPos.y){
                adjRooms.add(allRooms.get(i));
            }
        }
    }

    /**
     * draw this room
     * @param pApplet
     */
    public void drawRoom(PApplet pApplet){
        setColliders(true);
        pApplet.noStroke();

        for (int i = 0; i < triggers.size(); i++) {
            triggers.get(i).update();
        }

        for (int i = 0; i < tilePositions.length; i++) {
            for (int j = 0; j < tilePositions[0].length; j++) {
                if (tilePositions[i][j] == Tile.FLOOR) {
                    pApplet.fill(255);
                    pApplet.rect(position.x + j * tileSize, position.y + i * tileSize, tileSize, tileSize);
//                    pApplet.image(floor,position.x + j*tileSize, position.y + i*tileSize, tileSize, tileSize);
                }
                else if (tilePositions[i][j] == Tile.CONN) {
                    pApplet.fill(200);
                    pApplet.rect(position.x + j * tileSize, position.y + i * tileSize, tileSize, tileSize);
                    if (!createdConn) {
                        Collider col = new BoxCollider(position.x + j * tileSize, position.y + i * tileSize, tileSize, tileSize);
                        col.setTrigger(true);
                        triggers.add(col);
                    }
                }
                else {
                    pApplet.fill(0);
                    pApplet.rect(position.x + j * tileSize, position.y + i * tileSize, tileSize, tileSize);
//                    pApplet.image(wall, position.x + j*tileSize, position.y + i*tileSize, tileSize, tileSize);
                }
            }
        }
        createdConn = true;
    }

    /**
     * @param playerPos player position as PVector
     * @return room object in which the player is
     */
    public Room getCurrentRoom(PVector playerPos){
        if (isInConnector()) {
            for (int i = 0; i < adjRooms.size(); i++) {

                if (adjRooms.get(i).isInRoom(playerPos)){
                    return adjRooms.get(i);
                }
            }
        }

        return this;
    }

    /**
     * @param pos PVector position
     * @return true if given position is in room, false otherwise
     */
    public boolean isInRoom(PVector pos) {
        PVector oPos = this.position;
        float oWidth = this.width;
        float oHeight = this.height;

        if (pos.x <= oPos.x + oWidth
                && pos.x >= oPos.x
                && pos.y <= oPos.y + oHeight
                && pos.y >= oPos.y) {
            return true;
        }

        return false;
    }

    public PVector getCentre() {
        return new PVector(position.x + width/2, position.y + height/2);
    }

    /**
     * @param fromEdge position should be fromEdge distance from any edge
     * @return random PVector position in this room
     */
    public PVector getRandPosition(int fromEdge) {
        float x = (float) (position.x + Room.HALF_CON_LEN*tileSize + fromEdge + Math.random()*(width -
                (2* Room.HALF_CON_LEN*tileSize + 2*fromEdge)));
        float y = (float) (position.y + Room.HALF_CON_LEN*tileSize + fromEdge + Math.random()*(height -
                (2* Room.HALF_CON_LEN*tileSize + 2*fromEdge)));
        return new PVector(x, y);
    }

    /**
     * @return true if something is in connector, false otherwise
     */
    private boolean isInConnector(){
        for (int i = 0; i < triggers.size(); i++) {
            if (triggers.get(i).isCollided())
                return true;
        }

        return false;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public PVector getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public List<Room> getAdjRooms() {
        return adjRooms;
    }

    public int getTileSize() {
        return tileSize;
    }

    public Room copy(){
        Room copyRoom = new Room(tilePositions.clone(), tileSize, position.copy(),
                                width, height, cols,
                                false, adjRooms, triggers);
        return copyRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room))
            return false;
        Room r = (Room) o;
        return r.position.equals(position);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Room\n");
        String f = "XX";
        String t = "  ";
        for (int i = 0; i < tilePositions.length; i++) {
            for (int j = 0; j < tilePositions[0].length; j++) {
                if (tilePositions[i][j] == Tile.FLOOR || tilePositions[i][j] == Tile.CONN)
                    builder.append(t);
                else
                    builder.append(f);
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
