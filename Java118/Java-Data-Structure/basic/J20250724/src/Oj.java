import java.util.Stack;

public class Oj {
    private static Oj oj = new Oj();

    public static void main(String[] args) {
        System.out.println(oj.IsPopOrder(new int[]{1, 2, 3, 4, 5}, new int[]{4,3,5,2,1}));

    }

    // https://leetcode.cn/problems/valid-parentheses/
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < s.length(); i++) {
            char member = s.charAt(i);
            if (member == '(' || member == '[' || member == '{') {
                stack.push(member);
                continue;
            }

            // 右括号
            if (stack.isEmpty()) {
                return false;
            }
            char pop = stack.peek();
            if (
                (pop == '(' && member == ')') ||
                (pop == '[' && member == ']') ||
                (pop == '{' && member == '}')
            ) {
                stack.pop();
            } else {
                stack.push(member);
            }
        }
        return stack.isEmpty();
    }

    // https://www.nowcoder.com/practice/d77d11405cc7470d82554cb392585106?tpId=13&&tqId=11174&rp=1&ru=/activity/oj&qru=/ta/coding-interviews/question-ranking
    public boolean IsPopOrder (int[] pushV, int[] popV) {
        // write code here
        Stack<Integer> stack = new Stack<>();

        int j = 0;
        for (int i = 0; i < pushV.length; i++) {
            int push = pushV[i];
            if (push == popV[j]) {
                j++;
                continue;
            }
            // 暂时不匹配
            Integer sVal = null;
            if (!stack.isEmpty()) {
                sVal = stack.peek();
            }

            if (sVal != null && sVal == popV[j]) {
                // 说明里面有值并且值是匹配的 但是 push 此时没有匹配过，那就重新循环一下 (i--)
                stack.pop();
                j++;
                i--;
                continue;
            }

            stack.push(push);
        }

        // 最后清空栈
        while(!stack.isEmpty()) {
            if (stack.pop() != popV[j++]) {
                return false;
            }
        }
        return true;
    }
}
