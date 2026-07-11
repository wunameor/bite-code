import java.util.Stack;

public class Sort {
    /**
     * 插入排序
     * @param array
     */
    public static void insertSort(int[] array) {
        insertSort(array, 0, array.length - 1);
    }

    public static void insertSort(int[] array, int start, int end) {
        for (int i = start + 1; i < end + 1; i++) {
            int j = i - 1;
            int tmp = array[i];
            for( ; j >= start; j--) {
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

    /**
     * 冒泡排序
     * @param array
     */
    public static void bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            boolean isSwap = false;
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] >= array[j + 1]) {
                    swap(array, j, j + 1);
                    isSwap = true;
                }
            }
            if (!isSwap) {
                break;
            }
        }
    }

    /**
     * 快速排序
     * @param array
     */
    public static void quickSort(int[] array) {
        quick(array, 0, array.length - 1);
    }

    private static void quick(int[] array, int start, int end) {
        // start >= end 就结束了
        if (start >= end) {
            return;
        }

        // 低于一定的数值后 剩下的数据基本有序，适用于插入排序
        if (end - start <= 15) {
            insertSort(array, start, end);
        }

        // 获取三数中中位数的下标并且交换
        int midIndex = getMidIndex(array, start, end);
        swap(array, start, midIndex);

        // 排序，然后返回中间值的下标
        int pivot = partition(array, start, end);

        // 遍历左边与右边
        quick(array, start, pivot - 1);
        quick(array, pivot + 1, end);
    }

    private static int getMidIndex(int[] array, int start, int end) {
        int mid = (end - start) / 2 + start;
        if (array[mid] > array[start]) {
            if (array[end] > array[mid]) {
                return mid;
            } else {
                // array[end] <= array[mid] && array[mid] > array[start]
                return array[end] > array[start] ? end : start;
            }
        } else {
            // array[mid] <= array[start]
            if (array[end] > array[start]) {
                return start;
            } else {
                // array[mid] <= array[start] && array[end] <= array[start]
                return array[mid] > array[end] ? mid : end;
            }
        }
    }

    // hoare 法
    private static int partition1(int[] array, int left, int right) {
        int tmp = array[left];
        int i = left; // 用来存储 left

        while (left < right) {
            while (left < right && array[right] >= tmp) {
                right--;
            }

            while (left < right && array[left] <= tmp) {
                left++;
            }

            swap(array, left, right);
        }
        // 与开始的交换
        swap(array, i, left);
        return left;
    }

    // 挖坑法
    private static int partition2(int[] array, int left, int right) {
        int tmp = array[left];

        while (left < right) {
            while (left < right && array[right] >= tmp) {
                right--;
            }
            // 右边比较小的覆盖掉左边比较大的
            array[left] = array[right];
            while (left < right && array[left] <= tmp) {
                left++;
            }
            // 左边比较大的覆盖掉右边比较小的
            array[right] = array[left];
        }

        array[left] = tmp;
        return left;
    }

    private static int partition(int[] array, int left, int right) {
        int prev = left ;
        int cur = left + 1;
        while (cur <= right) {
            if(array[cur] < array[left] && array[++prev] != array[cur]) {
                swap(array,cur,prev);
            }
            cur++;
        }
        swap(array,prev,left);
        return prev;
    }

    /**
     * 非递归的快速排序
     * @param array
     */
    public static void quickSortNor(int[] array) {
        int start = 0;
        int end = array.length - 1;

        int pivot = partition2(array, start, end);

        Stack<Integer> stack = new Stack<>();

        if (pivot - start > 1) {
            // 左边有两个及其以上
            stack.push(pivot - 1);
            stack.push(start);
        }

        if (end - pivot > 1) {
            stack.push(end);
            stack.push(pivot + 1);
        }

        while (!stack.isEmpty()) {
            start = stack.pop();
            end = stack.pop();

            pivot = partition2(array, start, end);

            if (pivot - start > 1) {
                // 左边有两个及其以上
                stack.push(pivot - 1);
                stack.push(start);
            }

            if (end - pivot > 1) {
                stack.push(end);
                stack.push(pivot + 1);
            }
        }
    }

    /**
     * 归并排序
     * @param array
     */
    public static void mergeSort(int[] array) {
        mergeSort(array, 0, array.length - 1);
    }

    public static void mergeSort(int[] array, int left, int right) {
        if (left >= right) {
            return;
        }
        int mid = (left + right) / 2;

        // 归
        mergeSort(array, left, mid);
        mergeSort(array, mid + 1, right);

        // 并
        merge(array, left, right);
    }

    private static void merge(int[] array, int left, int mid, int right) {
        int[] tmpArray = new int[right - left + 1];
        int index = 0;

        int s1 = left, e1 = mid, s2 = mid + 1, e2 = right;

        while (s1 <= e1 && s2 <= e2) {
            // 这里使用 <= 是稳定的，没有等号就是不稳定的
            if (array[s1] <= array[s2]) {
                tmpArray[index++] = array[s1++];
            } else {
                tmpArray[index++] = array[s2++];
            }
        }

        while (s1 <= e1) {
            // 第一个数组还有元素
            tmpArray[index++] = array[s1++];
        }

        while (s2 <= e2) {
            // 第二个数组还有元素
            tmpArray[index++] = array[s2++];
        }

        // 用临时数组的数据覆盖掉原数组的数据
        for (int i = 0; i < tmpArray.length; i++) {
            array[i + left] = tmpArray[i];
        }
    }


    private static void merge(int[] array, int left, int right) {
        int mid = (right + left) / 2;

        merge(array, left, mid, right);
    }

    /**
     * 非递归实现归并排序
     * @param array
     */
    public static void mergeSortNor(int[] array) {
        int gap = 1; // 用来表示当前有序数组的长度，从小到大归并
        // 不需要等号
        while (gap < array.length) {
            for (int i = 0; i < array.length; i += gap * 2) {
                int left = i;

                // 防止最后几个，跳过头导致 数组越界
                int mid = left + gap - 1;
                if (mid >= array.length) {
//                    mid = array.length - 1;
                    break; // 如果左半部分已经触底，说明右半部分元素为 0，而左半部分是不需要排序的，因为已经是有序的了
                }

                int right = left + gap * 2 - 1;
                if (right >= array.length) {
                    right = array.length - 1;
                }

                // 不能少 mid 这个参数
                merge(array, left, mid, right);
            }
            gap *= 2;
        }
    }


    /**
     * 计数排序
     * @param array
     */
    public static void countSort(int[] array) {
        // 先获取到最大最小值
        int max = array[0], min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
            } else if (min > array[i]) {
                min = array[i];
            }
        }

        int[] countArray = new int[max - min + 1];

        // 计数
        for (int i = 0; i < array.length; i++) {
            int index = array[i] - min;
            countArray[index]++;
        }

        // 赋值
        int index = 0; // array 下标
        for (int i = 0; i < countArray.length; i++) {
            while (countArray[i] != 0) {
                countArray[i]--;
                array[index++] = i + min;
            }
        }
    }
}




