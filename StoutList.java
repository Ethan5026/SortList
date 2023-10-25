package edu.iastate.cs228.hw3;

import java.util.*;

/**
 * Implementation of the list interface based on linked nodes
 * that store multiple items per node.  Rules for adding and removing
 * elements ensure that each node (except possibly the last one)
 * is at least half full.
 *
 * @author Ethan Gruening
 */
public class StoutList<E extends Comparable<? super E>> extends AbstractSequentialList<E> {
    /**
     * Default number of elements that may be stored in each node.
     */
    private static final int DEFAULT_NODESIZE = 4;

    /**
     * Number of elements that can be stored in each node.
     */
    private final int nodeSize;

    /**
     * Dummy node for head.
     */
    public Node head;

    /**
     * Dummy node for tail.
     */
    private Node tail;

    /**
     * Number of elements in the list.
     */
    private int size;

    /**
     * Constructs an empty list with the default node size.
     */
    public StoutList() {
        this(DEFAULT_NODESIZE);
    }

    /**
     * Constructs an empty list with the given node size.
     *
     * @param nodeSize number of elements that may be stored in each node, must be
     *                 an even number
     */
    public StoutList(int nodeSize) {
        if (nodeSize <= 0 || nodeSize % 2 != 0) throw new IllegalArgumentException();

        // dummy nodes
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.previous = head;
        this.nodeSize = nodeSize;
    }

    /**
     * Constructor for grading only.  Fully implemented.
     *
     * @param head
     * @param tail
     * @param nodeSize
     * @param size
     */
    public StoutList(Node head, Node tail, int nodeSize, int size) {
        this.head = head;
        this.tail = tail;
        this.nodeSize = nodeSize;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E item) {

        boolean contained = true;

        if (contains(item)) {
            contained = false;
        }
        if (item == null) {
            throw new NullPointerException();
        }

        //check if the list is empty
        if (size() == 0) {
            //create new node
            Node newNode = new Node();

            //set where it is in the list
            head.next = newNode;
            newNode.previous = head;
            tail.previous = newNode;
            newNode.next = tail;
        }
        //check if the tail node is full
        else if (tail.previous.count == nodeSize) {

            //create a new previous tail node
            Node newNode = new Node();

            //assign where it is in the list
            tail.previous.next = newNode;
            newNode.previous = tail.previous;
            tail.previous = newNode;
            newNode.next = tail;
        }

        //add the item to the tail node
        tail.previous.addItem(item);

        //update size
        size++;
        return contained;
    }

    @Override
    public void add(int pos, E item) {

        ListIterator addList = listIterator(pos);
        addList.add(item);

    }

    @Override
    public E remove(int pos) {

        if (pos >= size) {
            throw new IllegalArgumentException();
        }

        ListIterator removeList = listIterator(pos);
        E removedVal = (E) removeList.next();
        removeList.remove();
        return removedVal;
    }

    /**
     * Sort all elements in the stout list in the NON-DECREASING order.
     */
    public void sort() {
        //create the comparator
        Comparator<E> comp = new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {

                //if o1 is less than o2
                if (o1.compareTo(o2) > 0) {
                    return -1;
                }
                //if they are equal
                if (o1.equals(o2)) {
                    return 0;
                }
                //if o1 is more than o2
                return 1;
            }
        };

        //fill the array
        E[] arr = (E[]) new Comparable[size];
        ListIterator sortIter = listIterator();
        for (int i = 0; i < size; i++) {
            arr[i] = (E) sortIter.next();
            System.out.println(arr[i]);
        }

        //call the insertion sort to sort the array
        insertionSort(arr, comp);

        //Delete the old Nodes
        head.next = tail;
        tail.previous = head;
        size = 0;

        //add all the new sorted elements
        for (int i = 0; i < arr.length; i++) {
            add(arr[i]);
        }
    }

    /**
     * Sort all elements in the stout list in the NON-INCREASING order.
     */
    public void sortReverse() {
        //fill the array
        E[] arr = (E[]) new Comparable[size];
        ListIterator sortIter = listIterator();
        for (int i = 0; i < size; i++) {
            arr[i] = (E) sortIter.next();
            System.out.println(arr[i]);
        }

        //call the insertion sort to sort the array
        bubbleSort(arr);

        //Delete the old Nodes
        head.next = tail;
        tail.previous = head;
        size = 0;

        //add all the new sorted elements
        for (int i = 0; i < arr.length; i++) {
            add(arr[i]);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new StoutListIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new StoutListIterator(index);
    }

    /**
     * Returns a string representation of this list showing
     * the internal structure of the nodes.
     */
    public String toStringInternal() {
        return toStringInternal(null);
    }

    /**
     * Returns a string representation of this list showing the internal
     * structure of the nodes and the position of the iterator.
     *
     * @param iter an iterator for this list
     */
    public String toStringInternal(ListIterator<E> iter) {
        int count = 0;
        int position = -1;
        if (iter != null) {
            position = iter.nextIndex();
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Node current = head.next;
        while (current != tail) {
            sb.append('(');
            E data = current.data[0];
            if (data == null) {
                sb.append("-");
            } else {
                if (position == count) {
                    sb.append("| ");
                    position = -1;
                }
                sb.append(data.toString());
                ++count;
            }

            for (int i = 1; i < nodeSize; ++i) {
                sb.append(", ");
                data = current.data[i];
                if (data == null) {
                    sb.append("-");
                } else {
                    if (position == count) {
                        sb.append("| ");
                        position = -1;
                    }
                    sb.append(data.toString());
                    ++count;

                    // iterator at end
                    if (position == size && count == size) {
                        sb.append(" |");
                        position = -1;
                    }
                }
            }
            sb.append(')');
            current = current.next;
            if (current != tail)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Node type for this list.  Each node holds a maximum
     * of nodeSize elements in an array.  Empty slots
     * are null.
     */
    private class Node {
        /**
         * Array of actual data elements.
         */
        // Unchecked warning unavoidable.
        public E[] data = (E[]) new Comparable[nodeSize];

        /**
         * Link to next node.
         */
        public Node next;

        /**
         * Link to previous node;
         */
        public Node previous;

        /**
         * Index of the next available offset in this node, also
         * equal to the number of elements in this node.
         */
        public int count;

        /**
         * Adds an item to this node at the first available offset.
         * Precondition: count < nodeSize
         *
         * @param item element to be added
         */
        void addItem(E item) {

            if (count >= nodeSize) {
                return;
            }
            data[count] = item;
            count++;
            //useful for debugging
            //System.out.println("Added " + item.toString() + " at index " + count + " to node "  + Arrays.toString(data));
        }

        /**
         * Adds an item to this node at the indicated offset, shifting
         * elements to the right as necessary.
         * <p>
         * Precondition: count < nodeSize
         *
         * @param offset array index at which to put the new element
         * @param item   element to be added
         */
        void addItem(int offset, E item) {
            if (count >= nodeSize) {
                return;
            }
            for (int i = count - 1; i >= offset; --i) {
                data[i + 1] = data[i];
            }
            ++count;
            data[offset] = item;
            //useful for debugging
            //System.out.println("Added " + item.toString() + " at index " + offset + " to node: "  + Arrays.toString(data));
        }

        /**
         * Deletes an element from this node at the indicated offset,
         * shifting elements left as necessary.
         * Precondition: 0 <= offset < count
         *
         * @param offset
         */
        void removeItem(int offset) {
            E item = data[offset];
            for (int i = offset + 1; i < nodeSize; ++i) {
                data[i - 1] = data[i];
            }
            data[count - 1] = null;
            --count;
        }
    }

    private class StoutListIterator implements ListIterator<E> {
        /**
         * shows where the cursor is for the index of the node
         */
        private int indexCursor;


        /**
         * shows the current node that the cursor is pointing to
         */
        private Node currentNode;

        /**
         * shows the position of the current value
         */
        private int position;

        /**
         * signifies what the last call of the iterator was.
         * 0 = no call
         * 1 = previous
         * 2 = next
         * 3 = remove
         * 4 = add
         */
        private int lastCall = 0;

        /**
         * Default constructor
         */
        public StoutListIterator() {
            //starting cursor at beginning
            if (size() != 0) {
                currentNode = head.next;
                indexCursor = 0;

            } else {
                currentNode = head;
                indexCursor = -1;
            }
            position = 0;
        }

        /**
         * Constructor finds node at a given position.
         *
         * @param pos
         */
        public StoutListIterator(int pos) {
            if (size() != 0) {
                currentNode = head.next;
                indexCursor = 0;
            } else {
                currentNode = head;
                indexCursor = -1;
            }


            position = 0;
            for (int i = 0; i < pos; i++) {
                next();
            }
        }

        @Override
        public boolean hasNext() {
            //if it's an empty list it returns false
            if (size == 0) {
                return false;
            }
            if (position < size) {
                return true;
            }
            //reaches here if there is no more non-null elements in the list
            return false;
        }

        @Override
        public E next() {

            //if it doesn't have a next return null
            if (!hasNext()) {
                return null;
            }

            //start the search at the next index
            int tempCursor = indexCursor;
            if (currentNode == head) {
                currentNode = head.next;
            }
            Node tempNode = currentNode;

            while (true) {

                //check if the node holds the next value
                for (int i = tempCursor; i < nodeSize; i++) {
                    if (tempNode.data[i] != null) {

                        //update cursors and return the next item
                        currentNode = tempNode;
                        indexCursor = i + 1;
                        position++;
                        lastCall = 2;
                        return tempNode.data[i];
                    }
                }

                //sets cursor back to start and update tempNode
                tempCursor = 0;
                tempNode = tempNode.next;
            }
        }

        @Override
        public boolean hasPrevious() {
            if (position > 0) {
                return true;
            }
            if (size == 0) {
                return false;
            }

            //reaches here if there is no more non-null elements in the list
            return false;
        }

        @Override
        public E previous() {

            //if it doesn't have a previous return null
            if (!hasPrevious()) {
                return null;
            }

            //start the search at the previous index
            int tempCursor = indexCursor - 1;
            Node tempNode = currentNode;

            while (true) {

                //check if the node holds the previous value
                for (int i = tempCursor; i >= 0; i--) {
                    if (tempNode.data[i] != null) {

                        //update cursors and return the next item
                        currentNode = tempNode;
                        indexCursor = i;
                        if (indexCursor == nodeSize - 1) {
                            currentNode = currentNode.previous;
                        }
                        position--;
                        if (position == 0) {
                            currentNode = head;
                        }
                        lastCall = 1;
                        return tempNode.data[i];
                    }
                }

                //sets cursor to end of previous node and set tempNode
                tempCursor = nodeSize - 1;
                tempNode = tempNode.previous;
            }
        }

        @Override
        public int nextIndex() {

            //if there is no index, returns size of list
            if (!hasNext()) {
                return size();
            }

            //returns the next position of the next element
            return position;
        }

        @Override
        public int previousIndex() {
            //returns -1 if at the beginning of the list
            if (!hasPrevious()) {
                return -1;
            }

            //returns the previous position
            return position - 1;
        }


        @Override
        public void remove() {
            //throw an exception if there is no direction
            if (lastCall == 0 || lastCall == 4) {
                throw new IllegalStateException();
            }

            //don't do anything if there isn't a next index
            if (!hasPrevious() && lastCall == 1) {
                return;
            }

            //shift to delete the item to the left
            if (lastCall == 1) {
                next();
                lastCall = 1;
            }


            //look at the next index to remove
            indexCursor -= 1;

            //if node is last node and has only one element
            if (currentNode == tail.previous && currentNode.count == 1) {

                //remove the node and restructure list
                currentNode = currentNode.previous;
                currentNode.next = tail;
                tail.previous = currentNode;
                indexCursor = nodeSize - 1;
            }
            //if its the last node, or if node is more than half full
            else if (currentNode == tail.previous || currentNode.count > nodeSize / 2) {

                //take out element and shift others
                currentNode.removeItem(indexCursor);
            }
            //node is at most half full
            else {

                //if next node has more than half full
                if (currentNode.next.count > nodeSize / 2) {

                    //remove and shift current
                    //take out element and shift others
                    currentNode.removeItem(indexCursor);

                    //add the next node's item
                    currentNode.addItem(currentNode.next.data[0]);

                    //shift the next node accordingly
                    currentNode.next.removeItem(0);

                }
                //full merge
                else {
                    //take out and shift elements in currentNode
                    currentNode.removeItem(indexCursor);

                    //add the next node's contents
                    for (int i = 0; i < currentNode.count; i++) {
                        currentNode.addItem(currentNode.next.data[i]);
                    }

                    //delete the next node
                    currentNode.next = currentNode.next.next;
                    currentNode.next.previous = currentNode;
                }
                indexCursor--;
            }

            //shift position,size
            size--;
            if (size == 0) {
                indexCursor = 0;
                currentNode = head;
            }
            lastCall = 3;
        }

        @Override
        public void set(E e) {
            if (e == null) {
                throw new NullPointerException();
            }

            //checks if remove or add has been called since the last next or previous calls
            if (lastCall == 2 || lastCall == 1 && size > 0) {

                //if next was last to be called
                if (lastCall == 2) {
                    if (indexCursor == 0) {
                        currentNode.previous.data[nodeSize - 1] = e;
                    } else {
                        currentNode.data[indexCursor - 1] = e;
                    }
                }

                //if previous was last to be called
                else {
                    next();
                    set(e);
                    previous();
                }
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public void add(E e) {
            if (e == null) {
                throw new NullPointerException();
            }
/*
            //check if element is in the list already
            if (contains(e)) {
                return;
            }
            */
            //update currentNode if cursor is at the end of a node
            if (indexCursor == nodeSize) {
                currentNode = currentNode.next;
                indexCursor = 0;
            } else if (indexCursor == -1) {
                currentNode = currentNode.next;
                indexCursor = 0;
            }

            int offset = indexCursor % nodeSize;

            //if you are in the tail Node
            if (currentNode == tail) {

                currentNode = tail.previous;
                Node newNode = new Node();

                newNode.previous = currentNode;
                newNode.next = tail;
                currentNode.next = newNode;
                tail.previous = newNode;
                currentNode = newNode;

                currentNode.addItem(e);
                indexCursor = 1;
            }
            //if list is empty, create new node and place in offset 0
            else if (size == 0) {

                Node newNode = new Node();
                newNode.next = tail;
                tail.previous = newNode;
                head.next = newNode;
                newNode.previous = head;
                currentNode = newNode;

                currentNode.addItem(e);
                indexCursor = 1;
            }

            //if offset is 0 and previous list is not head
            else if (offset == 0 && currentNode.previous != head && currentNode.previous.count < nodeSize) {
                currentNode.previous.addItem(e);
                currentNode = currentNode.previous;
                indexCursor = currentNode.count % nodeSize;
                if (indexCursor == 0) {
                    currentNode = currentNode.next;
                }

            }
            //if there is room in the current node
            else if (currentNode.count != nodeSize) {
                currentNode.addItem(offset, e);
                indexCursor = (offset + 1) % nodeSize;
                if (indexCursor == 0) {
                    currentNode = currentNode.next;
                }
            }

            //split operation
            else if (currentNode.count == nodeSize) {

                Node newNode = new Node();
                newNode.next = currentNode.next;
                newNode.next.previous = newNode;
                newNode.previous = currentNode;
                currentNode.next = newNode;

                //copy and remove half of current node to new Node
                for (int i = 0; i < nodeSize / 2; i++) {
                    newNode.addItem(currentNode.data[nodeSize / 2]);
                    currentNode.removeItem(nodeSize / 2);
                }

                if (offset <= nodeSize / 2) {
                    currentNode.addItem(offset, e);
                    indexCursor = offset + 1;

                } else {
                    newNode.addItem(offset - nodeSize / 2, e);
                    indexCursor = (offset - nodeSize / 2 + 1) % nodeSize;
                    if (indexCursor == 0) {
                        currentNode = currentNode.next;
                    } else {
                        currentNode = newNode;
                    }
                }
            }
            size++;
            position++;
            lastCall = 4;
        }
    }

    /**
     * Sort an array arr[] using the insertion sort algorithm in the NON-DECREASING order.
     *
     * @param arr  array storing elements from the list
     * @param comp comparator used in sorting
     */
    private void insertionSort(E[] arr, Comparator<? super E> comp) {
        int j;
        for (int i = 1; i < size(); i++) {
            j = i;

            //shifts until next point is in position
            while (comp.compare(arr[j], arr[j - 1]) > 0) {

                //swap elements
                E temp = arr[j];
                arr[j] = arr[j - 1];
                arr[j - 1] = temp;

                j--;
                if (j == 0) {
                    break;
                }
            }
        }
    }

    /**
     * Sort arr[] using the bubble sort algorithm in the NON-INCREASING order.
     *
     * @param arr array holding elements from the list
     */
    private void bubbleSort(E[] arr) {
        //move limit down
        for (int i = arr.length - 1; i > 0; i--) {

            //check next bubble
            for (int j = 0; j < i; j++) {

                //if the right side of the bubble is greater than the left, swap
                if (arr[j].compareTo(arr[j + 1]) < 0) {

                    //swap elements
                    E temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }
}