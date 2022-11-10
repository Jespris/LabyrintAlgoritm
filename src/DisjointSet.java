package src;

import java.util.Arrays;

public class DisjointSet implements DisjointSetInterface{
    private final int[] group;


    public DisjointSet(final int size) {
        this.group = new int[size];
        Arrays.fill(group, -1);
    }

    @Override
    public void Union(int root1, int root2) {
        assert group[root1] < 0 && group[root2] < 0;
        if (this.group[root2] < group[root1]){
            // sizes are negative
            group[root2] += group[root1];  // add sizes
            group[root1] = root2;  // root2 becomes new root
        } else {
            // root 1 is larger tree
            group[root1] += group[root2];
            group[root2] = root1;
        }
    }

    @Override
    public int Find(int index) {
        if (group[index] < 0){
            // index is a root, return it
            return index;
        }
        return group[index] = Find(group[index]);
    }

    @Override
    public int getSize() {
        return group.length;
    }
}
