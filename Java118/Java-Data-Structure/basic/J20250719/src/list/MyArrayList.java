package list;

import java.util.Arrays;

public class MyArrayList implements IList{
    private int[] elems;
    private int useSize;
    private static final int DEFAULT_CAPACITY_SIZE = 5;


    public MyArrayList() {
        this(DEFAULT_CAPACITY_SIZE);
    }


    public MyArrayList(int capacitySize) {
        this.elems = new int[capacitySize];
    }

    @Override
    public void add(int data) {
        add(useSize, data);
    }

    @Override
    public void add(int pos, int data) {
        if (isFull()) {
            grow();
        }
        checkPosByAdd(pos);

        for (int i = useSize - 1; i >= pos; i--) {
            elems[i + 1] = elems[i];
        }
        elems[pos] = data;
        useSize++;
    }

    private boolean isFull() {
        return useSize == elems.length;
    }

    private void grow() {
        elems = Arrays.copyOf(elems, elems.length * 2);
    }

    private void checkPosByAdd(int pos) {
        if (pos < 0 || pos > useSize) {
            throw new PosOutOfBoundsException("添加元素时候下标越界： 传入下标 pot = " + pos +
                    " 合法下标 0 ~ " + (useSize - 1));
        }
    }

    @Override
    public boolean contains(int toFind) {
        return indexOf(toFind) != -1;
    }

    @Override
    public int indexOf(int toFind) {
        for (int i = 0; i < useSize; i++) {
            if (elems[i] == toFind) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int get(int pos) {
        checkPosByDeal(pos);
        return elems[pos];
    }

    /**
     * 处理元素包括但不限于 get set remove
     * @param pos
     */
    private void checkPosByDeal(int pos) {
        if (pos < 0 || pos >= useSize) {
            throw new PosOutOfBoundsException("处理元素时候下标越界： 传入下标 pot = " + pos +
                    " 合法下标 0 ~ " + (useSize - 1));
        }
    }

    @Override
    public void set(int pos, int value) {
        checkPosByDeal(pos);
        elems[pos] = value;
    }

    @Override
    public void remove(int toRemove) {
        int pos = indexOf(toRemove);
        if (pos == -1) {
            return;
        }
        removeByPos(pos);
    }

    public void removeByPos(int pos) {
        checkPosByDeal(pos);
        for (int i = pos; i < useSize - 1; i++) {
            elems[i] = elems[i + 1];
        }
        // 移除的时候 最后一个是要变为 null 的 （如果是对象的话）
        useSize--;
    }

    @Override
    public int size() {
        return useSize;
    }

    @Override
    public void clear() {
        // 清空的时候需要把 对象变为 null
        elems = new int[DEFAULT_CAPACITY_SIZE];
        useSize = 0;
    }

    @Override
    public void display() {
        for (int i = 0; i < useSize; i++) {
            System.out.print(elems[i] + " ");
        }
        System.out.println();
    }

    @Override
    public boolean isEmpty() {
        return useSize == 0;
    }
}
