package Other;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author w w w. j a v a g i s t s . c o m
 *
 */
public class Node<T> {

    private T data = null;
    private List<Node<T>> children = new ArrayList<>();
    private Node<T> parent = null;
    private int depth = 1;

    public Node(T data) {
        this.data = data;
    }

    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public void addChildren(List<Node<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
        if (parent != null) {
            this.depth = parent.depth + 1;
        }
        else {
            this.depth = 1;
        }
    }

    public Node<T> getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node))
            return false;
        Node n = (Node) o;
        return data.equals(n.data);

    }

    public int getDepth() {
        return depth;
    }
}