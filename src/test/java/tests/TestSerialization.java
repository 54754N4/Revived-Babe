package tests;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TestSerialization {
	static int preHash;
	static Map<String, Map<String, Object>> nested, extracted;
	static File file;

	@BeforeAll
	static void setup() throws Exception {
		nested = new HashMap<>();
		nested.put("empty", new HashMap<>());
		Map<String, Object> map = new HashMap<>();
		map.put("num", 1);
		map.put("bool", true);
		map.put("obj", new ArrayList<>());
		preHash = nested.hashCode();
		file = new File("map.out");
	}
	
	@AfterEach
	public void verify() {		// check if each serialization worked
		int postHash = extracted.hashCode();
		assertEquals(preHash, postHash);
	}
	
	@Tag("fast")
	@Test
	public void serialize_gson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(nested);											// serialize
		Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
		extracted = gson.fromJson(json, type);										// deserialize
	}
	
	@SuppressWarnings("unchecked")
	@Tag("fast")
	@Test
	public void serialize_native() throws FileNotFoundException, IOException, ClassNotFoundException {
		try (
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		) {
			out.writeObject(nested);												// serialize
			extracted = (Map<String, Map<String, Object>>) in.readObject();			// deserialize
		}
	}
	
	@AfterAll
	static void teardown() {
		nested.clear();
		file.delete();
	}
}