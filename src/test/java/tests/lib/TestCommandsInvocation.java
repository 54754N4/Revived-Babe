package tests.lib;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import annotations.FastTest;
import commands.hierarchy.Command;
import commands.model.Invoker.Reflector;

public class TestCommandsInvocation {
	private static final List<String> NORMALS = Arrays.asList("e", "p", "ttt"),
			ADMINS = Arrays.asList("exit", "test");
	
	@FastTest
    public void givenNormalUser_whenExecutingCommand_matchesCommandInstance() {
		check(Reflector.Type.NORMAL, "Checking for normal users", NORMALS.toArray(new String[0]));
    }
 
    @FastTest
    public void givenAdminUser_whenExecutingCommand_matchesCommandInstance() {
    	check(Reflector.Type.ADMIN, "Checking for admin exclusive", NORMALS.toArray(new String[0]));
    	check(Reflector.Type.ALL, "Checking for admin inclusive", NORMALS.toArray(new String[0]));
    	check(Reflector.Type.ADMIN, "Checking for admin exclusive^2", ADMINS.toArray(new String[0]));
    	check(Reflector.Type.ALL, "Checking for admin inclusive exclusive", ADMINS.toArray(new String[0]));
    }

    private static void check(Reflector.Type type, String append, String...commands) {
    	Class<? extends Command> cls;
		for (String command : commands) { 
			cls = Reflector.find(command, type);
			assertNotNull(cls, append+"\nCould not find for "+command);
		}
    }
}
