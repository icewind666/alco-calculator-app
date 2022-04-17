package simo.com.alco.api;

public class ApiSubcategory {
    public int id;
    public String name;
    public int categoryId;
    public int processId;
    public String categoryName;

    public ApiCategory category;

    public static ApiSubcategory fromJson(String jsonStr) {
        return null;
    }

    public String toJson() {
        return "";
    }

}
