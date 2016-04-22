/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

public class Heap<E extends Comparable<E>> {

    private Object S[];
    private int last;
    private int capacity;

    public Object[] getS() {
        return S;
    }
    public Object getS(int pos) {
        if(pos>this.last)
            return null;
        
        return S[pos];
    }

    public Heap() {
        S = new Object[11];
        last = 0;
        capacity = 7;
    }

    public Heap(int cap) {
        S = new Object[cap + 1];
        last = 0;
        capacity = cap;
    }

    public int size() {
        return last;
    }
    public boolean isFull() {
        return capacity==last;            
    }

    //
    // returns the number of elements in the heap
    //

    public boolean isEmpty() {
        return size() == 0;
    }

    //
    // is the heap empty?
    //

    public E min() throws Exception {
        if (isEmpty())
            throw new Exception("The heap is empty.");
        else
            return (E) S[1];
    }

    //
    // returns element with smallest key, without removal
    //

    private int compare(Object x, Object y) {
        return ((E) x).compareTo((E) y);
    }

    public void insert(E e) throws Exception {
        if (size() == capacity)
            throw new Exception("Heap overflow.");
        else{
            last++;
            S[last] = e;
            upHeapBubble();
        }       
    }

    // inserts e into the heap
    // throws exception if heap overflow
    //

    public E removeMin() throws Exception {
        if (isEmpty())
            throw new Exception("Heap is empty.");
        else {
            E min = min();
            S[1] = S[last];
            last--;
            downHeapBubble();
            return min;
        }
    }

    //
    // removes and returns smallest element of the heap
    // throws exception is heap is empty
    //

    /**
     * downHeapBubble() method is used after the removeMin() method to reorder the elements
     * in order to preserve the Heap properties
     */
    private void downHeapBubble(){
        int index = 1;
        while (true){
            int child = index*2;
            if (child > size())
                break;
            if (child + 1 <= size()){
                //if there are two children -> take the smalles or
                //if they are equal take the left one
                child = findMin(child, child + 1);
            }
            if (compare(S[index],S[child]) <= 0 )
                break;
            swap(index,child);
            index = child;
        }
    }

    /**
     * upHeapBubble() method is used after the insert(E e) method to reorder the elements
     * in order to preserve the Heap properties 
     */
    private void upHeapBubble(){
        int index = size();
        while (index > 1){
            int parent = index / 2;
            if (compare(S[index], S[parent]) >= 0)
                //break if the parent is greater or equal to the current element
                break;
            swap(index,parent);
            index = parent;
        }       
    }

    /**
     * Swaps two integers i and j
     * @param i
     * @param j
     */
    private void swap(int i, int j) {
        Object temp = S[i];
        S[i] = S[j];
        S[j] = temp;
    }

    /**
     * the method is used in the downHeapBubble() method
     * @param leftChild
     * @param rightChild
     * @return min of left and right child, if they are equal return the left
     */
    private int findMin(int leftChild, int rightChild) {
        if (compare(S[leftChild], S[rightChild]) <= 0)
            return leftChild;
        else
            return rightChild;
    }

    public String toString() {
        String s = "[";
        for (int i = 1; i <= size(); i++) {
            s += S[i];
            if (i != last)
                s += ",";
        }
        return s + "]";
    }
    //
    // outputs the entries in S in the order S[1] to S[last]
    // in same style as used in ArrayQueue
    //

}

