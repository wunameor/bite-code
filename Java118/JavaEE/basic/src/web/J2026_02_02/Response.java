package web.J2026_02_02;

public class Response {
    private double num;

    public Response(double num) {
        this.num = num;
    }

    public Response() {
    }

    public static Response unSerialized(String str) {
        String[] split = str.split(",");
        Response response = new Response();
        response.num = Double.parseDouble(split[0]);
        return response;
    }

    public String serialized() {
        return String.format("%f\n", num);
    }

    public double getNum() {
        return num;
    }
}
