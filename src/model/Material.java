package model;

import java.util.HashMap;
import java.util.Map;

import static model.Warehouse.ALL_WAREHOUSES;

public class Material {
    private static int nextId = 0;
    public static Map<Integer, Material> ALL_MATERIALS = new HashMap<>();

    // In a normal application one or more of these could be unique and handled by ORM
    private final int id;
    private String name;
    private String description;
    private String iconPath;
    private int maxCapacity;

    public static Material getMaterial(int itemId) {
        return ALL_MATERIALS.get(itemId);
    }

    public Material() {
        this.id = nextId++;
        ALL_MATERIALS.put(this.id, this);
        this.notifyWarehouses();
    }

    public Material(String name, String description, String iconPath, int maxCapacity) {
        this();
        this.name = name;
        this.description = description;
        this.iconPath = iconPath;
        this.maxCapacity = maxCapacity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    private void notifyWarehouses() {
        for (Warehouse warehouse : ALL_WAREHOUSES.values()){
            warehouse.update(this);
        }
    }

}
