package enums;

public enum UserProxyChoice {

    ADMIN(1, "管理员"),
    NORMAL_GUANYU(2, "普通用户（关羽）"),
    // 修正了拼写、中文描述和对应的 value 值
    NORMAL_ZHANGFEI(3, "普通用户（张飞）"),
    EXIT(4, "退出系统"),
    ;

    // 1. 使用 private final 保证不可变性和封装性
    private final int value;
    // 2. 避免使用 name，改为 desc 防止与底层方法冲突
    private final String desc;

    // 3. 构造器默认是私有的，明确写出 private 是一种好习惯
    private UserProxyChoice(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    // 4. 提供 Getter 方法供外部安全读取
    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    // 根据数字查找 UserProxy
    public static UserProxyChoice getByValue(int value) {
        for (UserProxyChoice choice : UserProxyChoice.values()) {
            if (choice.getValue() == value) {
                return choice;
            }
        }
        return null; // 如果输入的数字找不到对应的枚举，返回 null
    }
}
