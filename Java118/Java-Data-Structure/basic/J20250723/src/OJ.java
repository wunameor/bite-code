public class OJ {

    public static void main(String[] args) {
        PalindromeList.test();

    }

    // https://www.nowcoder.com/practice/d281619e4b3e4a60a2cc66ea32855bfa?tpId=49&&tqId=29370&rp=1&ru=/activity/oj&qru=/ta/2016test/question-ranking
    public static class PalindromeList {
        public static class ListNode {
            int val;
            ListNode next = null;

            ListNode(int val) {
                this.val = val;
            }
        }

        public static void test() {
            ListNode listNode = creatList(new int[]{});
            System.out.println(new PalindromeList().chkPalindrome(listNode));
        }

        public static ListNode creatList(int[] arrays) {
            ListNode head = new ListNode(-1);
            ListNode tmp = head;
            for (int num : arrays) {
                tmp.next = new ListNode(num);
                tmp = tmp.next;
            }
            return head.next;
        }

        public boolean chkPalindrome(ListNode A) {
            if (A == null || A.next == null) {
                return true;
            }
            // write code here
            // 1. 先获取中间节点
            ListNode slow = A, fast = A;
            while(fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
            }

            // 2. 调转方向
            ListNode prev = slow, cur = slow.next, next = cur.next;
            slow.next = null; // 制空，最后才不会成环
            while(cur != null) {
                cur.next = prev;
                prev = cur;
                cur = next;
                if (next != null) {
                    next = next.next;
                }
            }
            // 3. 判断
            ListNode lastTmp = prev, headTmp = A;
            while (lastTmp != null) { // lastTmp 不能用 headTmp 代替
                if (headTmp.val != lastTmp.val) {
                    return false;
                }
                headTmp = headTmp.next;
                lastTmp = lastTmp.next;
            }

            return true;
        }
    }

}
