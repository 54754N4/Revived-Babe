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

	@Tag("slow")
    @Test
    public void testAddMaxInteger() {
        assertEquals(2147483646, Integer.sum(2147183646, 300000));
    }
 
    @Tag("fast")
    @Test
    public void testDivide() {
        assertThrows(ArithmeticException.class, () -> {
            Integer.divideUnsigned(42, 0);
        });
    }

    
}

class Invoker {
	public static Reflections createReflections(String packageName, Class<?>[] include, Class<?>[] exclude) {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		FilterBuilder filterBuilder = new FilterBuilder();
		configurationBuilder.addUrls(ClasspathHelper.forPackage(packageName));
		for (Class<?> keep : include) filterBuilder.includePackage(keep);
		for (Class<?> skip : exclude) filterBuilder.excludePackage(skip);
		configurationBuilder.filterInputsBy(filterBuilder);
	    return new Reflections(configurationBuilder);
	}
}
