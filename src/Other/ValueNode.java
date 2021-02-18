package Other;

public class ValueNode<T> implements Comparable {
    private float value;
    private T data;

    public ValueNode(float value, T data) {
        this.value = value;
        this.data = data;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ValueNode)){
            return 0;
        }
        return (int) (((ValueNode) o).value - this.value);
    }

    public T getData() {
        return data;
    }
}
