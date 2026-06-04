package enums;

public enum AdminMenuChoice {
    SEARCH_BOOKS(1, "查找图书"),
    PRINT_ALL_BOOKS(2, "打印所有的图书"),
    EXIT_SYSTEM(3, "退出系统"),
    ADD_BOOK(4, "上架图书"),
    UPDATE_BOOK(5, "修改图书"),
    DELETE_BOOK(6, "下架图书"),
    CALCULATE_BOOK_BORROW_COUNT(7, "统计借阅次数"),
    SEARCH_MOST_WELCOME_BOOKS(8, "查看最受欢迎的前K本书"),
    CHECK_INVENTORY_STATUS(9, "查看库存状态"),
    SEARCH_BOOKS_BY_CATEGORY(10, "按类别统计图书"),
    SEARCH_BOOKS_BY_AUTHOR(11, "按作者统计图书"),
    SEARCH_BOOKS_MORE_ONE_YEAR(12, "检查超过一年未下架的图书");

    private final int choice;
    private final String des;

    private AdminMenuChoice(int choice, String des) {
        this.choice = choice;
        this.des = des;
    }

    public int getChoice() { return choice; }
    public String getDes() { return des; }

    // 反向查找方法
    public static AdminMenuChoice getByValue(int choice) {
        for (AdminMenuChoice menu : AdminMenuChoice.values()) {
            if (menu.getChoice() == choice) {
                return menu;
            }
        }
        return null;
    }
}
