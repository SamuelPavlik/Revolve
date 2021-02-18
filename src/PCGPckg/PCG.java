package PCGPckg;

import Other.Node;
import Other.Rectangle;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PCG {
    private static float MULTIPLIER = 2;

    private float screenWidth;
    private float screenHeight;
    private float tileSize;
    private float minWidth;
    private float minHeight;
    public static final int CONNECTOR_LENGTH = 4;
    public static final int CONNECTOR_WIDTH = 2;

    public PCG(int screenWidth, int screenHeight, int tileSize) {
        this.screenWidth = screenWidth*MULTIPLIER;
        this.screenHeight = screenHeight*MULTIPLIER;
        this.tileSize = tileSize;
        this.minWidth = screenWidth/2;
        this.minHeight = screenHeight/2;
    }

    /**
     * get all rooms and connectors as Room objects
     * @return list of all rooms and connectors as Room objects
     */
    public List<Room> getRoomObjects(){
        List<Node<Rectangle>> mainRooms = getLeafNodes();
        List<Node<Rectangle>> connectors = getAllConnectors(mainRooms);
        List<Room> roomList = new ArrayList<>();

        for (int i = 0; i < mainRooms.size(); i++) {
            List<Rectangle> rects = new ArrayList<>();
            Rectangle room = mainRooms.get(i).getData();
            PVector roomPos = room.getPosition();
            rects.add(room);
            for (int j = 0; j < connectors.size(); j++) {
                Rectangle con = connectors.get(j).getData();
                PVector conPos = con.getPosition();
                if (roomPos.x - con.getWidth() <= conPos.x
                        && roomPos.x + room.getWidth() >= conPos.x
                        && roomPos.y - con.getHeight() <= conPos.y
                        && roomPos.y + room.getHeight() >= conPos.y){
                    rects.add(con);
                }
            }
            Room newRoom = new Room(rects, (int) tileSize);
            roomList.add(newRoom);
        }

        //set adjacent rooms for each room
        for (int i = 0; i < roomList.size(); i++) {
            roomList.get(i).setAdjacentRooms(roomList);
        }

        return roomList;
    }

    /**
     * The whole room is split multiple times by PCG algorithm that creates a tree of split rooms.
     * The leaf nodes are actual rooms of the dungeon.
     * @return return list of leaf nodes that make up the PCG tree of the whole room
     */
    private List<Node<Rectangle>> getLeafNodes(){
        screenWidth = screenWidth - (screenWidth % tileSize);
        screenHeight = screenHeight - (screenHeight % tileSize);
        Node<Rectangle> root = new Node<>(new Rectangle(new PVector(0,0),
                                            screenWidth,
                                            screenHeight));
        List<Node<Rectangle>> leafNodes = new ArrayList<>();
        expandNode(root, leafNodes, tileSize);

        return leafNodes;
    }

    /**
     * @param rooms rooms to connect
     * @return list of all connectors between the rooms as Rectangle objects
     */
    private List<Node<Rectangle>> getAllConnectors(List<Node<Rectangle>> rooms){
        Queue<Node<Rectangle>> queue = new LinkedList<>();
        List<Node<Rectangle>> connectors = new ArrayList<>();

        for (int i = 0; i < rooms.size(); i++) {
            Node<Rectangle> parent = rooms.get(i).getParent();
            if (parent != null && !queue.contains(parent))
                queue.add(parent);
        }

        while (!queue.isEmpty()){
            Node<Rectangle> node = queue.remove();
            if (node.getParent() != null && !queue.contains(node.getParent()))
                queue.add(node.getParent());
            List<Node<Rectangle>> children = node.getChildren();
            //jump over if already connected
            if (children.size() == 2){
                Rectangle rect = getConnector(children.get(0), children.get(1), tileSize);
                if (rect != null) {
                    Node<Rectangle> connectNode = new Node<>(rect);
                    if (!connectors.contains(connectNode)) {
                        connectors.add(connectNode);
                    }
                }
            }
        }

        return connectors;
    }

    /**
     * @param node1
     * @param node2
     * @param tileSize tile size
     * @return connector between two rooms as Rectangle object
     */
    private Rectangle getConnector(Node<Rectangle> node1, Node<Rectangle> node2, float tileSize){
        Rectangle rect1 = node1.getData();
        Rectangle rect2 = node2.getData();
        PVector pos1 = rect1.getPosition();
        PVector pos2 = rect2.getPosition();
        Rectangle conRect = null;

        if (pos1.x == pos2.x){
            PVector coord = new PVector(pos1.x, pos1.y < pos2.y ? pos1.y + rect1.getHeight() : pos2.y + rect2.getHeight());
            conRect = new Rectangle(coord,
                                    CONNECTOR_WIDTH*tileSize,
                                    CONNECTOR_LENGTH*tileSize);
        }
        else if (pos1.y == pos2.y) {
            PVector coord = new PVector(pos1.x < pos2.x ? pos1.x + rect1.getWidth() : pos2.x + rect2.getWidth(), pos1.y);
            conRect = new Rectangle(coord,
                                CONNECTOR_LENGTH*tileSize,
                                CONNECTOR_WIDTH*tileSize);
        }

        return conRect;
    }

    /**
     * @param node
     * @return list of all child rectangle objects
     */
    private List<Rectangle> getChildRects(Node<Rectangle> node){
        List<Rectangle> rects = new ArrayList<>();
        getRects(rects, node);

        return rects;
    }

    /**
     * populate given list with all child rectangle objects
     * @param rects
     * @param node
     */
    private void getRects(List<Rectangle> rects, Node<Rectangle> node){
        if (node.getChildren().size() == 0){
            rects.add(node.getData());
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            getRects(rects, node.getChildren().get(i));
        }
    }

    /**
     * split given rectangle node further if still not of minimal size
     * @param node node to split
     * @param leafNodes current list of all leaf nodes
     * @param tileSize tile size
     */
    private void expandNode(Node<Rectangle> node, List<Node<Rectangle>> leafNodes, float tileSize){
        Rectangle data = node.getData();
        if ((data.getWidth() < 2*minWidth - 100 && data.getHeight() < 2*minHeight - 100)){
            leafNodes.add(node);
            return;
        }

        splitRectangle(node, tileSize);
        List<Node<Rectangle>> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            expandNode(children.get(i), leafNodes, tileSize);
        }
    }

    /**
     * split rectangle to 2 rectangle objects randomly according to some rules
     * @param toSplitNode rectangle node to split
     * @param tileSize tile size
     */
    private void splitRectangle(Node<Rectangle> toSplitNode, float tileSize){
        double rand = Math.random();
        Rectangle toSplit = toSplitNode.getData();

        if (toSplit.getWidth() < toSplit.getHeight()) {
            rand += 1;
        }
        else {
            rand -= 1;
        }

        Rectangle rect1;
        Rectangle rect2;
        PVector splitPos = toSplit.getPosition();
        float splitWidth = toSplit.getWidth();
        float splitHeight = toSplit.getHeight();
        double split;
        if (rand > 0.5f){
            split = minHeight + Math.random()*(splitHeight - 2*minHeight);
            split = split - (split % tileSize);
            rect1 = new Rectangle(splitPos, splitWidth, (float) split - (CONNECTOR_LENGTH /2) *tileSize);
            rect2 = new Rectangle(new PVector(splitPos.x, splitPos.y + rect1.getHeight() + CONNECTOR_LENGTH *tileSize),
                                    splitWidth,
                                    (float) (splitHeight - split - (CONNECTOR_LENGTH /2) *tileSize));
        }
        else {
            split = minWidth + Math.random()*(toSplit.getWidth() - 2* minWidth);
            split = split - (split % tileSize);
            rect1 = new Rectangle(splitPos, (float) split - (CONNECTOR_LENGTH /2) *tileSize, splitHeight);
            rect2 = new Rectangle(new PVector(splitPos.x + rect1.getWidth() + CONNECTOR_LENGTH *tileSize, splitPos.y),
                                    (float) (splitWidth - split - (CONNECTOR_LENGTH /2) *tileSize),
                                    splitHeight);
        }

        toSplitNode.addChild(new Node<>(rect1));
        toSplitNode.addChild(new Node<>(rect2));
    }
}
