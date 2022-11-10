package src;

public interface DisjointSetInterface {

    void Union(final int root1, final int root2);
    int Find(final int index);

    int getSize();
}
