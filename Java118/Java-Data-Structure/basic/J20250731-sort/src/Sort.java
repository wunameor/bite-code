public class Sort {
    /**
     * 插入排序
     * @param array
     */
    public static void insertSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int j = i - 1;
            int tmp = array[i];
            for( ; j >= 0; j--) {
                // 不要写为 >= 不然就是不稳定的
                if (array[j] > tmp) {
                    array[j + 1] = array[j];
                } else {
                    break;
                }
            }
            // j + 1 是最后一个元素的下标
            array[j + 1] = tmp;
        }
    }

    /**
     * 希尔排序（选择排序的优化）
     * @param array
     */
    public static void shellSort(int[] array) {
        int gap = array.length / 2; // gap 变化逻辑可以自己定义，但是最终必须为 1
        while (gap >= 1) {
            shell(array, gap);
            gap /= 2;
        }
    }

    private static void shell(int[] array, int gap) {
        for (int i = gap; i < array.length; i++) {
            int j = i - gap; // 同一个组的前一个元素的下标
            int tmp = array[i];
            for( ; j >= 0; j -= gap) {
                // 希尔排序是不稳定的
                if (array[j] > tmp) {
                    array[j + gap] = array[j];
                } else {
                    break;
                }
            }
            // j + 1 是最后一个元素的下标
            array[j + gap] = tmp;
        }
    }


    public static void selectSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[minIndex] > array[j]) {
                    minIndex = j;
                }
            }
            // minIndex 存储最小值的下标
            swap(array, i, minIndex);
        }
    }

    private static void swap(int[] array, int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static void heapSort(int[] array) {
        // 建大根堆 O(N)
        createHeap(array);

        // 排序 O(N * logN)
        int end = array.length - 1;
        while (end > 0) {
            swap(array, 0, end);
            siftDown(array, 0, end);
            end--;
        }
    }

    private static void createHeap(int[] array) {
        for (int parent = (array.length - 1 - 1) / 2; parent >= 0; parent--) {
            siftDown(array, parent, array.length);
        }
    }

    private static void siftDown(int[] array, int parent, int useSize) {
        int child = parent * 2 + 1;
        while (child < useSize) {
            if (child + 1 < useSize && array[child] < array[child + 1]) {
                child++;
            }
            // 此时 child 指向的下标是孩子节点的最大值
            if (array[child] > array[parent]) {
                swap(array, child, parent);
                parent = child;
                child = parent * 2 + 1;
            } else {
                // 剩下的都是大根堆
                break;
            }
        }
    }
}
