package mac78;
import java.util.ArrayList;

/**
 * Trees without explicit links.
 * Notice that various fields/methods have the protected modifier
 * when normally they would/should be private.
 * The reason is that this supports whitebox testing.
 *
 * @author Stefan Kahrs, Mark Crump
 * @version 1
 */
//note the constraint on A is a slight generalisation of A extends Comparable<A>
//and is generally recommended when one wants a comparison operation
//it basically allows that the comparison op is implemented at a supertype
//of A, instead of A itself;
//for the assessment itself it makes no discernable difference
public class LinklessTree<A extends Comparable<? super A>> {
    //sizes of subtrees at that node index
    protected int[] sizes;
    protected Object[] elems;
    protected int[] heights;
    //for annoying technical reason this has to be an array of objects

    /**
     * Constructor for objects of class LinklessTree
     * calls the assign height method
     */
    private static final int STARTSIZE = 15;

    public LinklessTree() {
        assert STARTSIZE > 0;
        elems = freshElemArray(STARTSIZE);
        sizes = new int[STARTSIZE];
        sizes[0]=0;
        heights = new int[elems.length];
        assignHeight();
    }

    /**
     * Assigns a height to each node node
     */
    protected void assignHeight() {
        //the index of the node
        int index = 0;
        // the height of the node
        int height = 1;
        //the element that is being looked at
        int element = 0;



        while (index < elems.length -1) {
            if (sizes[index] == 1) {
                while (element != 0) {
                    if (element % 2 ==0) {
                            heights[element] = height;
                            element = (element -2)/2;
                            height++;
                    }
                    else {
                            heights[element] = height;
                            element = (element -1)/2;
                            height++;
                    }
                }
            }
            index ++;
            element = index;
            if (sizes[index] !=0) {
                heights[element] = height;
            }
            height =1;

        }
        if (heights[1] > heights[2]) {
            heights[0] = heights[1] + 1;
        }
        else {
            heights[0] = heights[2] +1;
        }

    }

    //size of whole tree is the size of the subtree rooted at 0
    //@return the size of the tree
    public int size() {
        return getSize(0);
    }

    //returns a type A elems
    //@param the index of the node
    //@return the elems as a type A
    public A getValue(int index) {
        return (A) elems[index];
    }

    //auxiliary methods to index the arrays out of bounds too
    //they may help to reduce case distinctions
    //@param the index of the node
    //@return the value of the node
    protected A getKey(int subtree) {
        if (subtree >= elems.length) return null; // out of bounds
        return getValue(subtree);
    }

    protected int getSize(int subtree) {
        if (subtree >= elems.length) return 0; // out of bounds
        return sizes[subtree];
    }

    //@param the size of the array
    //@return an array with the size capacity
    //encapsulates the cast on the allocation
    protected Object[] freshElemArray(int capacity) {
        return new Object[capacity];
    }

    //remainder needs to be modified

    //@param the value to find
    //@return the index of the search value
    //find index position of val in tree, if there, or where it goes, if not there
    protected int findIndex(A val) {

        int count = 0;
        while (getKey(count) != null) {
            if (getKey(count).compareTo(val) == +1) {
                count = count * 2 + 1;
            } else if (getKey(count).compareTo(val) == -1) {
                count = count * 2 + 2;
            } else if (getKey(count).compareTo(val) == 0) {
                System.out.println(count);
                return count;

            }
        }

        System.out.println(count);
        return count;
    }

    //@param a value that the tree might contain
    //@return a true or false depending on if the value is found in the tree
    //is value in tree
    public boolean contains(A val) {
        int count = 0;
        do {
            if (getKey(count).compareTo(val) == +1) {
                count = count * 2 + 1;
            } else if (getKey(count).compareTo(val) == -1) {
                count = count * 2 + 2;
            } else if (getKey(count).compareTo(val) == 0) {
                return true;
            }
        }
        while (getKey(count) != null);
        return false;
    }

    //grow the space in which we can place the tree, so that at least one insertion will succeed
    //balances the tree then doubles the tree after it has been balanced
    protected void grow() {

        int index = 0;
        int element = index;
        A temp;
        ArrayList<A>subTree1 = new ArrayList<>();
        ArrayList<A>subTree2 = new ArrayList<>();
        ArrayList<A>subTree3 = new ArrayList<>();
        ArrayList<A>subTree4 = new ArrayList<>();

        while (index < elems.length) {
            if (getKey(index) != null && sizes[index] > 2) {
                //this is for left leaning
                if (heights[(index*2+ 2)] - heights[(index *2+1)] < -1) {
                    element = element * 2 + 1;
                    //left leaning with right sub tree
                    if (heights[(element * 2+ 2)]  - heights[element *2+1] > 1) {
                        subTree1 = subTree(element*2+1);
                        subTree2 = subTree((element*2+2)*2+1);
                        subTree2 = subTree((element*2+2)*2+2);
                        subTree4 = subTree(index*2+2);

                        temp = getKey(index);
                        elems[index] = elems[element*2+2];
                        elems[element*2+2] = null;
                        sizes[element*2+2] = 0;
                        elems[index*2+2] = temp;
                        sizes[index] = 3;
                        sizes[index*2+2] = 1;
                        sizes[index*2+1] = 1;

                        for(A tree: subTree1) {
                            insert(tree);
                        }
                        for(A tree: subTree2) {
                            insert(tree);
                        }
                        for(A tree: subTree3) {
                            insert(tree);
                        }
                        for(A tree: subTree4) {
                            insert(tree);
                        }
                        subTree1.clear();
                        subTree2.clear();
                        subTree3.clear();
                        subTree4.clear();
                        heights = new int[elems.length];
                        assignHeight();
                    }
                    //left leaning with left sub tree
                    else {

                        subTree1 = subTree(element*2+1);
                        subTree2 = subTree((index*2+2));
                        subTree3 = subTree(element*2+2);

                        temp = getKey(index);
                        elems[index] = elems[element];
                        elems[index*2+2] = temp;
                        elems[index*2+1] = null;
                        sizes[index*2+1] = 0;
                        sizes[index] = 2;
                        sizes[index*2+2] = 1;

                        for(A tree: subTree1) {
                            insert(tree);
                        }
                        for(A tree: subTree2) {
                            insert(tree);
                        }
                        for(A tree: subTree3) {
                            insert(tree);
                        }
                        subTree1.clear();
                        subTree2.clear();
                        subTree3.clear();
                        heights = new int[elems.length];
                        assignHeight();
                    }
                }
                //this is for right leaning
                else if ((heights[index *2+ 2] - heights[index *2+ 1] > 1)) {
                    element = element * 2 + 2;
                    //right leaning with left sub tree
                    if ((heights[element * 2+2] - heights[element *2+1]) < -1) {
                        subTree1 = subTree(element*2+2);
                        subTree2 = subTree((element*2+1)*2+2);
                        subTree2 = subTree((element*2+1)*2+1);
                        subTree4 = subTree(index*2+1);

                        temp = getKey(index);
                        elems[index] = elems[(index*2+2)*2+1];
                        elems[(index*2+2)*2+1] = null;
                        sizes[(index*2+2)*2+1] = 0;
                        elems[index*2+1] = temp;
                        sizes[index] = 3;
                        sizes[index*2+2] = 1;
                        sizes[index*2+1] = 1;

                        for(A tree: subTree1) {
                            insert(tree);
                        }
                        for(A tree: subTree2) {
                            insert(tree);
                        }
                        for(A tree: subTree3) {
                            insert(tree);
                        }
                        for(A tree: subTree4) {
                            insert(tree);
                        }
                        subTree1.clear();
                        subTree2.clear();
                        subTree3.clear();
                        subTree4.clear();
                        heights = new int[elems.length];
                        assignHeight();
                    }
                    //right leaning with right sub tree
                    else {
                        subTree1 = subTree(element*2+2);
                        subTree2 = subTree((index*2+1));
                        subTree3 = subTree(element*2+1);

                        temp = getKey(index);
                        elems[index] = elems[element];
                        elems[index*2+1] = temp;
                        elems[index*2+2] = null;
                        sizes[index*2+2] = 0;
                        sizes[index] = 2;
                        sizes[index*2+2] = 1;

                        for(A tree: subTree1) {
                            insert(tree);
                        }
                        for(A tree: subTree2) {
                            insert(tree);
                        }
                        for(A tree: subTree3) {
                            insert(tree);
                        }
                        subTree1.clear();
                        subTree2.clear();
                        subTree3.clear();
                        heights = new int[elems.length];
                        assignHeight();
                    }
                }
            }
            index++;
            element = index;
        }
        Object[] copyElems = new Object[elems.length * 2];
        int[] copySizes = new int[sizes.length * 2];
        System.arraycopy(elems, 0, copyElems, 0, elems.length);
        System.arraycopy(sizes, 0, copySizes, 0, sizes.length);

        elems = copyElems;
        sizes = copySizes;
    }

    //@param a starting node
    //@return an array list with all the children of the starting node
    //saves each sub tree as an arraylist to be inserted later
    protected ArrayList<A> subTree(int element) {

        ArrayList<A>subTree = new ArrayList<>();
        int subNode = 0;
        int biggest = element;
        int smallest = element;

            while (subNode < elems.length) {

                if (elems[subNode] != null && subNode <= biggest && subNode >= smallest) {
                    subTree.add(getKey(subNode));
                    elems[subNode] = null;
                    sizes[subNode] = 0;
                }
                subNode++;
                if (subNode > biggest) {
                    smallest = smallest * 2 + 1;
                    biggest = biggest * 2 + 2;
                }
            }
            return subTree;
    }

    //fetch the i-th element, in comparision order
    //@param the ith biggest element to fetch
    //@return the value
    public A get(int i) {

        boolean loop = true;
        int count = 0;
        int left = 1;
        int input = i + 1;

        while (loop) {
            if (sizes[0] < input) {
                return null;
            } else if (input <= sizes[left]) {
                count = count * 2 + 1;
                left = count * 2 + 1;
            } else if (input > sizes[left] + 1) {
                input = input - sizes[left] - 1;
                count = count * 2 + 2;
                left = count * 2 + 1;
            } else {
                loop = false;
            }
        }
        return getValue(count);
    }

    //@param value to add to tree
    //@return true or false if the value was added
    //add x to tree, return true if tree was modified
    //we do not allow multiple copies of the equal objects in tree
    //equality is decided by using compareTo
    public boolean insert(A x) {

        int count = 0;
        boolean loop = false;
        int[] copySizes = new int[sizes.length];
        System.arraycopy(sizes,0,copySizes,0,sizes.length);
        sizes[count]++;

        while (getKey(count) != null && !loop) {
            if (getKey(count).compareTo(x) == 1) {
                count = count * 2 + 1;
                sizes[count]++;
            } else if (getKey(count).compareTo(x) == -1) {
                count = count * 2 + 2;
                sizes[count]++;
            } else if (getKey(count).compareTo(x) == 0) {
                loop = true;
            }
        }

        while (count > elems.length) {
                grow();
        }

        if (count < elems.length && getKey(count) == null) {
            elems[count] = x;

            return true;
        }
        System.out.println(getKey(count) + " this is the number already here");
        sizes = copySizes;
        return false;
    }

    //@param delete the value if found
    //@return true or false depending on if thr object was deleted
    //remove x from tree, return true if tree was modified
    public boolean delete(A x) {

        int count = 0;
        boolean loop = false;
        int[] copySizes = new int[sizes.length];
        System.arraycopy(sizes, 0, copySizes, 0, sizes.length);
        sizes[count]--;

        while (getKey(count) != null && !loop) {
            if (getKey(count).compareTo(x) == 1) {
                count = count * 2 + 1;
                sizes[count]--;
            } else if (getKey(count).compareTo(x) == -1) {
                count = count * 2 + 2;
                sizes[count]--;
            } else if (getKey(count).compareTo(x) == 0) {
                loop = true;
            }
        }

        if (count < elems.length && getKey(count) != null) {

            int leftSteps = 0;
            int rightSteps = 0;
            int rightSub = count *2+2;
            int leftSub = count *2+1;

            if (getKey(leftSub)!= null) {
                leftSteps++;
                while (getKey(leftSub*2+2) != null) {
                    leftSteps++;
                    leftSub = leftSub * 2 + 2;
                }
            }

            if (getKey(rightSub)!= null) {
                rightSteps++;
                while (getKey(rightSub*2+1) != null) {
                    leftSteps++;
                    rightSub = rightSub * 2 + 1;
                }
            }

            if (getKey(rightSub) != null && getKey(leftSub) == null){
                elems[count] = null;
            }
            else if (rightSteps > leftSteps) {
                elems[count] = getKey(rightSub);
                elems[rightSub] = null;
            }
            else {
                elems[count] = getKey(leftSub);
                elems[leftSub] = null;
            }

            for (Object elem:elems) {
                System.out.println(elem);
            }
            return true;
        }
        for (Object elem:elems) {
            System.out.println(elem);
        }

        return false;
    }

    //not requested, but these might be useful auxiliary ops for delete
    private A deleteLargest(int subtree) { return null; }

    private A deleteSmallest(int subtree) { return null; }
}