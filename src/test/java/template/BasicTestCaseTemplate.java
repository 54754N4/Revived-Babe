package template;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import annotations.Fast;
import annotations.Slow;
import annotations.TemplateTest;

public class BasicTestCaseTemplate {

	@BeforeEach
	void setUp() throws Exception {
		// do shit BEFORE each tests
	}

	@AfterEach
	void tearDown() throws Exception {
		// do shit AFTER each tests
	}
	
	@BeforeAll
	static void initialSetup() throws Exception {
		// do shit BEFORE all tests
	}

	@AfterAll
	static void finalTearDown() throws Exception {
		// do shit AFTER all tests
	}

	@Slow
    @TemplateTest
    public void testAddMaxInteger() {
        assertEquals(2147483646, Integer.sum(2147183646, 300000));
    }
 
    @Fast
    @TemplateTest
    public void testDivide() {
        assertThrows(ArithmeticException.class, () -> {
            Integer.divideUnsigned(42, 0);
        });
    }

}
