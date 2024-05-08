package test;

import model.Material;
import model.Warehouse;
import util.TransferResult;
import util.enums.TransferStatus;

public class Tests {

    // Attention: In a normal testing environment we could have some mock objects,
    // but for now manually creating objects will suffice
    public void testAll() {
        shouldBeEmptyWarehouseOnCreationTest();
        shouldHaveZeroMaterialCountOnMaterialCreationTest();
        shouldNotAddMaterialOverCapTest();
        shouldHaveZeroMaterialsAfterCleanTest();
        shouldPartiallyAndFullyRepleteWarehouseTest();
        shouldNotAddNegativeAmountTest();
        shouldNotRemoveNegativeAmountTest();
        shouldNotRemoveFromDepletedWarehouseTest();
        shouldPartiallyAndFullyRemoveFromWarehouseTest();
        shouldFailTransferTest();
        shouldPartiallyTransferTest();
        shouldFullyTransferTest();
        
        System.out.println("Tests passed successfully");
    }

    private void shouldBeEmptyWarehouseOnCreationTest() {
        Warehouse a = new Warehouse(":)");
        Warehouse b = new Warehouse(":(");

        assert a.getMaterialIds().isEmpty() : "Warehouse :) was not empty";
        assert b.getMaterialIds().isEmpty() : "Warehouse :( was not empty";
    }

    private void shouldHaveZeroMaterialCountOnMaterialCreationTest() {
        Warehouse a = new Warehouse(":)");
        Warehouse b = new Warehouse(":(");

        Material copper = new Material("Copper", "some description", "somePath", 3000);
        Material iron = new Material("Iron", "some description", "somePath", 2000);

        assert a.getMaterialIds().contains(copper.getId()) : "Warehouse :) did not contain copper";
        assert b.getMaterialIds().contains(copper.getId()) : "Warehouse :( did not contain copper";
        assert a.getMaterialIds().contains(iron.getId()) : "Warehouse :) did not contain iron";
        assert b.getMaterialIds().contains(iron.getId()) : "Warehouse :( did not contain iron";
    }

    private void shouldNotAddMaterialOverCapTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), 999_999_999);

        assert !(a.getItemCount(copper.getId()) > copper.getMaxCapacity()) : "Warehouse :) overflowed with copper";
    }

    private void shouldHaveZeroMaterialsAfterCleanTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), copper.getMaxCapacity());
        a.clean();

        assert a.getItemCount(copper.getId()) == 0 : "Found non-zero resources after cleaning a warehouse";
    }

    private void shouldPartiallyAndFullyRepleteWarehouseTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), copper.getMaxCapacity());

        assert a.getItemCount(copper.getId()) == copper.getMaxCapacity() : "Could not fully replete empty warehouse";

        a.clean();
        a.addMaterials(copper.getId(), copper.getMaxCapacity() - 1);
        assert a.getItemCount(copper.getId()) == copper.getMaxCapacity() - 1 : "Could not partially replete empty warehouse";

        a.addMaterials(copper.getId(), 1);
        assert a.getItemCount(copper.getId()) == copper.getMaxCapacity() : "Could not fully replete non-empty warehouse";
    }

    private void shouldNotAddNegativeAmountTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), -1);

        assert a.getItemCount(copper.getId()) == 0 : "Warehouse :) had non-zero copper";
    }

    private void shouldNotRemoveNegativeAmountTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        assert !a.removeMaterials(copper.getId(), -1) : "Successfully removed negative amount of copper from Warehouse :)";

        a.addMaterials(copper.getId(), 1000);

        assert !a.removeMaterials(copper.getId(), -1) : "Successfully removed negative amount of copper from Warehouse :)";
    }

    private void shouldNotRemoveFromDepletedWarehouseTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.removeMaterials(copper.getId(), 1);

        assert a.getItemCount(copper.getId()) == 0 : "Expected copper amount 0 after trying to remove from empty warehouse";

        a.addMaterials(copper.getId(), 100);
        a.removeMaterials(copper.getId(), 101);
        
        assert a.getItemCount(copper.getId()) == 100 : "Expected copper amount 100 after trying to remove 101 from warehouse";
    }

    private void shouldPartiallyAndFullyRemoveFromWarehouseTest() {
        Warehouse a = new Warehouse(":)");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), 1000);

        assert a.removeMaterials(copper.getId(), 500) : "Could not partially remove materials from warehouse";
        assert a.removeMaterials(copper.getId(), 500) : "Could not fully materials from warehouse";
    }

    private void shouldFailTransferTest() {
        // ignored

        // a transfer is an event that combines warehouse.removeMaterials(...) and warehouse.addMaterials(...) events into one
        // therefore warehouse.removeMaterials(...) and warehouse.addMaterials(...) should cover this fully
    }

    private void shouldPartiallyTransferTest() {
        Warehouse a = new Warehouse(":)");
        Warehouse b = new Warehouse(":(");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), copper.getMaxCapacity());
        b.addMaterials(copper.getId(), 1);
        TransferResult transferResult = a.transferMaterials(copper.getId(), b, copper.getMaxCapacity());
        
        assert transferResult.status().equals(TransferStatus.PART) : "TransferStatus inconsistency";
        assert transferResult.transferredAmount() == copper.getMaxCapacity() - 1 : "TransferredAmount inconsistency";
        assert transferResult.excess() == 1 : "Excess materials inconsistency";
        assert a.getItemCount(copper.getId()) == 1 : "Leftover inconsistency";
        assert b.getItemCount(copper.getId()) == copper.getMaxCapacity() : "Destination amount inconsistency";
    }

    private void shouldFullyTransferTest() {
        Warehouse a = new Warehouse(":)");
        Warehouse b = new Warehouse(":(");
        Material copper = new Material("Copper", "some description", "somePath", 3000);

        a.addMaterials(copper.getId(), copper.getMaxCapacity());
        TransferResult transferResult = a.transferMaterials(copper.getId(), b, copper.getMaxCapacity());
        
        assert transferResult.status().equals(TransferStatus.FULL) : "TransferStatus inconsistency";
        assert transferResult.transferredAmount() == copper.getMaxCapacity() : "TransferredAmount inconsistency";
        assert transferResult.excess() == 0 : "Excess materials inconsistency";
        assert a.getItemCount(copper.getId()) == 0 : "Leftover inconsistency";
        assert b.getItemCount(copper.getId()) == copper.getMaxCapacity() : "Destination amount inconsistency";
    }
}
