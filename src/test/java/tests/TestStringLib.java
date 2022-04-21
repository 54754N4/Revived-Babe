package tests;

import static lib.StringLib.replaceAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import lib.StringLib;

public class TestStringLib {

	/* Utility */
	
	private <T> void transform(T[] inputs, Function<T, T> transformer) {
    	for (int i=0; i<inputs.length; i++)
    		inputs[i] = transformer.apply(inputs[i]);
    }
	
	/* Test cases */
	
	@Tag("fast")
    @Test
    public void convertTime_toMillis() {
		long[] milli = {1606146691, 1000, 1001000};
		String[] time = {"446:09:06", "00:00:01", "00:16:41"};
        for (int i=0; i<milli.length; i++)
        	assertEquals(StringLib.millisToTime(milli[i]), time[i]);
    }
	
	@Tag("fast")
	@Test
	public void splitOn_commandSeparator() {
		String[] split = StringLib.split("..e hi;..e hello", ';'),
			expected = {"..e hi", "..e hello"};
		assertArrayEquals(split, expected);
	}
 
    @Tag("fast")
    @Test
    public void consumePrefix_removed() {
    	String[] prefixes =  {"..", "hey babe ", "@Babe"},
    		input = {"..e hello", "hey babe play", "@Babe echo hi"}, 
    		expected = {"e hello", "play", "echo hi"};
    	transform(input, s -> StringLib.consumePrefix(s, prefixes));
    	assertArrayEquals(input, expected);
    }

    @Tag("fast")
    @Test
    public void onDeobfuscation_selectivelyHide() {
    	String[] input = { 
    			"~\\Audio\\Songs\\Backstreet Boys\\Millennium\\Show Me the Meaning of Being Lonely.mp3"
		}, expected = {
				StringLib.MUSIC_PATH + "\\Audio\\Songs\\Backstreet Boys\\Millennium\\Show Me the Meaning of Being Lonely.mp3"
		};
    	transform(input, StringLib::deobfuscateMusicFolder);
    	assertArrayEquals(input, expected);
    }
    
    @Tag("fast")
    @Test
    public void onQuote_unquote() {
    	String[] input = {
    			"..schedule --every=2 \\\"..queue --current\\\"",
    			"..alias --name=current --value=\"..schedule --every=2 \\\"..queue --current\\\"\""
    	}, expected = {
    			"..schedule --every=2 \"..queue --current\"",
    			"..alias --name=current --value=\"..schedule --every=2 \"..queue --current\"\""
    	};
    	transform(input, StringLib::unQuote);
    	assertArrayEquals(input, expected);
    }
    
    @Tag("fast")
    @Test
    public void onQuoteAndSpace_splitCorrectly() {
    	String command = "..schedule --every=2 \\\"..queue --current\\\"";
    	String[] input = StringLib.split(StringLib.unQuote(command), ' '),
    		expected = {"..schedule", "--every=2", "\"..queue --current\""}; 
    	assertArrayEquals(input, expected);
    }
    
    @Tag("fast")
    @Test
    public void onMentions_replaceAccordingly() {
    	String input = "..test @[DCN] Xentorno and @Satsana  werqe \\\"@[DCN] Xentorno\\\" qwer  asfd @Satsana";
    	String[] inputs = {
    			replaceAll(input, "@Satsana", "REPLACED", true),
    			replaceAll(input, "@Satsana", "REPLACED"),
    			replaceAll(input, "@[DCN] xentorno", "REPLACED", true),
    			replaceAll(input, "@[DCN] xentorno", "REPLACED")
    	}, expected = {
    			"..test @[DCN] Xentorno and REPLACED  werqe \\\"@[DCN] Xentorno\\\" qwer  asfd REPLACED",
    			"..test @[DCN] Xentorno and REPLACED  werqe \\\"@[DCN] Xentorno\\\" qwer  asfd REPLACED",
    			"..test REPLACED and @Satsana  werqe \\\"REPLACED\\\" qwer  asfd @Satsana",
    			"..test @[DCN] Xentorno and @Satsana  werqe \\\"@[DCN] Xentorno\\\" qwer  asfd @Satsana"
    	};
    	assertArrayEquals(inputs, expected);
    }
}
