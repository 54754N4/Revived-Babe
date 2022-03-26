package lambda;

@FunctionalInterface
public interface VersionCheckHandler {
	void handle(String name, String latest, boolean isUpdated);
}