package vatsify.io.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vatsify.io.inventory.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, Integer quantity);
}
