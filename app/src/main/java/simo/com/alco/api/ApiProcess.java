package simo.com.alco.api;

public class ApiProcess {
    public int id;
    public String name;

    public ApiProcess() {

    }

    public ApiProcess(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public static ApiProcess fromJson(String jsonStr) {
        return null;
    }

    public String toJson() {
        return "";
    }

}
