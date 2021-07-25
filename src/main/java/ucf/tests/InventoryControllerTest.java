package ucf.tests;

import org.junit.jupiter.api.Test;
import ucf.assignments.InventoryController;

import static org.junit.jupiter.api.Assertions.*;

class InventoryControllerTest {

    InventoryController inventoryController = new InventoryController();


    @Test
    void stringIsNotAlphanumeric_returns_correct_value() {
        // string to check
        String check = "afh429658#%%&4hdfkjh";

        // test
        var actual = inventoryController.stringIsNotAlphanumeric(check);
        assertEquals(true, actual);
    }

    @Test
    void stringIsNumbers_returns_correct_value() {
        // string to check
        String check = "#$#^%HDdfhd";

        // test
        var actual = inventoryController.stringIsNumbers(check);
        assertEquals(false, actual);
    }

    @Test
    void removeDuplicatePeriods_removes_additional_periods_from_input() {
        // string
        String check = "2456.34.3634.346";

        // test
        var actual = inventoryController.removeDuplicatePeriods(check);
        assertEquals("2456.343634346", actual);

    }

}