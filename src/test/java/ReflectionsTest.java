import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

class ReflectionsTest {
//	private Map<>

	@BeforeAll
	static void initialSetup() throws Exception {
//		reflectionsAdmin = createReflections(); 
	}

	@AfterAll
	static void finalTearDown() throws Exception {
		// do shit AFTER all tests
	}

	
//	@Tag("slow")
//    @Test
//    public void testAddMaxInteger() {
//        assertEquals(2147483646, Integer.sum(2147183646, 300000));
//    }
// 
//    @Tag("fast")
//    @Test
//    public void testDivide() {
//        assertThrows(ArithmeticException.class, () -> {
//            Integer.divideUnsigned(42, 0);
//        });
//    }

    
}