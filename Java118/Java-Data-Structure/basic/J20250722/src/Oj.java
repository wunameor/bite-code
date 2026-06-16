public class Oj {



    static class Solution2 {
        // https://leetcode.cn/problems/reverse-linked-list/
        // Definition for singly-linked list.

        public class ListNode {
            int val;
            ListNode next;

            ListNode() {
            }

            ListNode(int val) {
                this.val = val;
            }

            ListNode(int val, ListNode next) {
                this.val = val;
                this.next = next;
            }
        }

        private ListNode ret = null;
        // 使用递归的方法
        public ListNode reverseList(ListNode head) {
            if (head == null || head.next == null) {
                return head;
            }
            ret = null;

            ListNode tmp = head.next;
            head.next = null;
            reverse(head, tmp);
            return ret;
        }

        private void reverse(ListNode head, ListNode next) {
            if (next == null) {
                ret = head;
                return;
            }
            ListNode tmp = next.next;
            next.next = head;
            reverse(next, tmp);
        }
    }

    public class Partition {
        public static class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        // https://www.nowcoder.com/practice/0e27e0b064de4eacac178676ef9c9d70?tpId=8&&tqId=11004&rp=2&ru=/activity/oj&qru=/ta/cracking-the-coding-interview/question-ranking
        public ListNode partition(ListNode pHead, int x) {
            if (pHead == null || pHead.next == null) {
                return pHead;
            }
            ListNode tmp = pHead;
            ListNode maxList = new ListNode(-1), minList = new ListNode(-1);
            ListNode maxLast = maxList, minLast = minList;
            while(tmp != null) {
                if (tmp.val < x) {
                    minLast.next = new ListNode(tmp.val);
                    minLast = minLast.next;
                } else  {
                    // >= x
                    maxLast.next = new ListNode(tmp.val);
                    maxLast = maxLast.next;
                }
                tmp = tmp.next;
            }
            // 拼接
            minLast.next = maxList.next;
            // 不需要处理尾巴的循环，因为我之前是 new 的，默认是 null
            return minList.next;
        }
    }
}
