package src;

public interface DisjointSetInterface {

    // interface for declaring what methods a disjoint set class should have
    void Union(final int root1, final int root2);
    int Find(final int index);
}
