package inf112.skeleton.common.utility;

public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the first value in the pair
     * @return first value of the pair
     */
    public K getKey() {
        return key;
    }

    /**
     * Get the second value of the pair
     * @return second value of the pair
     */
    public V getValue() {
        return value;
    }

}
