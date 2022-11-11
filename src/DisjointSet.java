package src;

import java.util.Arrays;

public final class DisjointSet implements DisjointSetInterface{
    private final int[] s;  // our set, name doesn't matter
    public DisjointSet(final int size) {
        this.s = new int[size];
        Arrays.fill(s, -1); // initialize all values as -1, since all indexes are roots with size itself, aka -1
    }

    @Override
    public void Union(int root1, int root2) {
        assert s[root1] < 0 && s[root2] < 0;
        if (this.s[root2] < s[root1]){  // Union by size
            // sizes are negative
            s[root2] += s[root1];  // add sizes
            s[root1] = root2;  // root2 becomes new root
        } else {
            // root 1 is larger tree
            s[root1] += s[root2];  // add sizes
            s[root2] = root1;  // root1 becomes new root
        }
    }

    @Override
    public int Find(int index) {
        // this method found in moodle
        if (s[index] < 0){
            // index is a root, return it back up the recursion
            return index;
        }
        return s[index] = Find(s[index]);  // path compression, recursively get parent index value
    }
}
