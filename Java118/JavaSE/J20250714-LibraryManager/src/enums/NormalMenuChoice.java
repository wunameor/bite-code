package enums;

public enum NormalMenuChoice {
    SEARCH_BOOKS(1, "查找图书"),
    PRINT_ALL_BOOKS(2, "打印所有的图书"),
    EXIT_SYSTEM(3, "退出系统"),
    BORROW_BOOK(4, "借阅图书"),        // 这里是 4
    RETURN_BOOK(5, "归还图书"),        // 这里是 5
    VIEW_BORROW_HISTORY(6, "查看当前个人借阅情况"); // 这里是 6

    private final int choice;
    private final String des;

    private NormalMenuChoice(int choice, String des) {
        this.choice = choice;
        this.des = des;
    }

    public int getChoice() { return choice; }
    public String getDes() { return des; }

    public static NormalMenuChoice getByValue(int choice) {
        for (NormalMenuChoice menu : NormalMenuChoice.values()) {
            if (menu.getChoice() == choice) {
                return menu;
            }
        }
        return null;
    }
}
