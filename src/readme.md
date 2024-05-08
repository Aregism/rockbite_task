# Rockbite Games PoC Application

This is a Proof of Concept (PoC) application for Rockbite Games, featuring two main models: Warehouse and Material.

### Models:

- **Warehouse:** Represents a storage facility that is aware of material creation events and updates itself correspondingly.
- **Material:** Represents the items stored in the warehouses.

### Warehouse Operations:

- **addMaterial(int materialId, int amount):** Adds the specified amount of the material to the calling warehouse.
- **removeMaterials(int materialId, int amount):** Removes the specified amount of materials from the calling warehouse.
- **transferMaterials(int materialId, Warehouse to, int amount):** Transfers the specified amount of materials from the calling warehouse to another warehouse.

All these operations can succeed or fail, even partially. The `transferMaterials()` operation exhibits a semblance of an atomicity mechanism, though without a proper Object-Relational Mapping (ORM), it can still fail under extreme conditions.

### Design Considerations:

While all actions are declared within the Warehouse class following an Object-Oriented Programming (OOP) approach, it's suggested that a separate service class should handle these operations.

### Testing:

The 'Tests' class contains a public method `testAll()` which internally executes all the private test methods with assertions. It's important to note that this application doesn't utilize any testing libraries; assertions are manually implemented. To enable assertions and catch assertion failures, use the JVM option '-ea'. Otherwise, no exceptions will be thrown if an assertion fails.
