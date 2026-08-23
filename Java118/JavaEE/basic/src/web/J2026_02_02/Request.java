package web.J2026_02_02;

public class Request {
    private String operators;
    private double num1;
    private double num2;

    public Request(String operators, double num1, double num2) {
        this.operators = operators;
        this.num1 = num1;
        this.num2 = num2;
    }

    public Request() {
    }

    public static Request unSerialized(String str) {
        String[] split = str.split(",");
        Request request = new Request();
        request.operators = split[0];
        request.num1 = Double.parseDouble(split[1]);
        request.num2 = Double.parseDouble(split[2]);
        return request;
    }

    public String serialized() {
        return String.format("%s,%f,%f\n", operators, num1, num2);
    }

    public double calculate() {
        switch (operators) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                return num1 / num2;
            default:
                throw new RuntimeException("计算错误：" + serialized());
        }
    }
}
