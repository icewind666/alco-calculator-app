package simo.com.alco.api;

public class ApiCategory {
    public int id;
    public String name;
    public int process_id;
    public String processName;

    public ApiCategory() {

    }

    public ApiCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ApiCategory(int id, String name, int process_id, String processName) {
        this.id = id;
        this.name = name;
        this.process_id = process_id;
        this.processName = processName;
    }

    public static ApiCategory fromJson(String jsonStr) {
        return null;
    }

    public String toJson() {
        return "";
    }
}
