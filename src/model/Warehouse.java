package model;

import util.enums.TransferStatus;
import util.TransferResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static model.Material.ALL_MATERIALS;

public class Warehouse {
    private static int NEXT_ID = 0;
    public static Map<Integer, Warehouse> ALL_WAREHOUSES = new HashMap<>();
    private final int id;
    private final String name;
    //      <MaterialID, MaterialCount>
    private final Map<Integer, Integer> materials = new HashMap<>();

    public Warehouse(String name) {
        this.id = NEXT_ID++;
        this.name = name;
        ALL_WAREHOUSES.put(this.id, this);
    }

    public String getName() {
        return name;
    }


    public int getId() {
        return id;
    }

    public TransferResult addMaterials(int materialId, int amount) {
        Material material = ALL_MATERIALS.get(materialId);
        int current = this.getItemCount(materialId);
        if (amount <= 0) {
            return new TransferResult(TransferStatus.NONE, amount, 0);
        } else if (current == material.getMaxCapacity()) {
            return new TransferResult(TransferStatus.NONE, amount, 0);
        } else if (current + amount > material.getMaxCapacity()) {
            this.setItemCount(material.getId(), material.getMaxCapacity());
            return new TransferResult(TransferStatus.PART, current + amount - material.getMaxCapacity(), material.getMaxCapacity() - current);
        } else if (current + amount <= material.getMaxCapacity()) {
            this.setItemCount(material.getId(), current + amount);
            return new TransferResult(TransferStatus.FULL, 0, amount);
        }
        return new TransferResult(TransferStatus.NONE, amount, 0);
    }

    public boolean removeMaterials(int materialId, int amount) {
        Material material = ALL_MATERIALS.get(materialId);
        if (amount <= 0 || amount > material.getMaxCapacity()) {
            return false;
        }
        int current = this.getItemCount(materialId);
        if (amount > current) {
            return false;
        } else {
            this.setItemCount(materialId, current - amount);
            return true;
        }
    }

    public synchronized TransferResult transferMaterials(int materialId, Warehouse warehouseTo, int amount) {
        TransferResult result = new TransferResult(TransferStatus.NONE, amount, 0);
        int availableStorage = warehouseTo.availableStorage(materialId);
        int toBeTransferred = Math.min(amount, availableStorage);
        // we pass toBeTransferred when removing materials, to ensure that they exist in (this)
        boolean success = this.removeMaterials(materialId, toBeTransferred);
        if (success) {
            // but we pass the raw amount to add materials, to be consistent with TransferStatus requirement
            // after all partial repleting can exist
            result = warehouseTo.addMaterials(materialId, amount);
        }
        return result;
    }

    public synchronized void clean() {
        this.materials.replaceAll((k, v) -> 0);
    }

    public Set<Integer> getMaterialIds() {
        return this.materials.keySet();
    }

    public int getItemCount(int itemId) {
        return this.materials.get(itemId);
    }

    private void setItemCount(int itemId, int count) {
        this.materials.replace(itemId, count);
    }

    private int availableStorage(int materialId) {
        Material material = ALL_MATERIALS.get(materialId);
        int max = material.getMaxCapacity();
        int current = this.getItemCount(materialId);
        return max - current;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Warehouse " + this.name + " ID: " + this.id);
        for (Map.Entry<Integer, Integer> entry : this.materials.entrySet()) {
            builder
                    .append("\n#")
                    .append(entry.getKey())
                    .append(" ")
                    .append(ALL_MATERIALS.get(entry.getKey()).getName())
                    .append(": ")
                    .append(entry.getValue());
        }
        return builder.toString();
    }

    public void update(Material material) {
        materials.put(material.getId(), 0);
    }
}
