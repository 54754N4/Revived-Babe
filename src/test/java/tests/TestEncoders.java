package tests;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;

import annotations.FastTest;
import lib.encode.Encoder;
import lib.encode.Encoder.Type;

public class TestEncoders {
	private static String[] INPUTS = {
			"hello world",
			"i am a secret input",
			"The greatest glory in living lies not in never falling, but in rising every time we fall",
			"If you set your goals ridiculously high and it's a failure, you will fail above everyone else's success",
			"The way to get started is to quit talking and begin doing",
			"If life were predictable it would cease to be life, and be without flavor",
			"If you look at what you have in life, you'll always have more. If you look at what you don't have in life, you'll never have enough"
	};
	private boolean ignoreCase = false;
	private Encoder.Type type;

	@AfterEach
	void assertInputsValidate() throws Exception {
		for (String input : INPUTS) {
			System.out.printf("%s : %s -> %s%n", type, input, type.encoder.apply(ignoreCase ? input.toLowerCase() : input));
			assertEquals(
				ignoreCase ? input.toLowerCase() : input,
				type.decoder.apply(type.encoder.apply(ignoreCase ? input.toLowerCase() : input)),
				type.name()+" failed validation.");
			
		}
		ignoreCase = false;
	}

	@FastTest
    public void givenValidInputs_MorseCoders_shouldValidate() {
        type = Type.MORSE;
        ignoreCase = true;
    }
 
	@FastTest
    public void givenValidInputs_Base64Coders_shouldValidate() {
        type = Type.BASE64;
    }

	@FastTest
    public void givenValidInputs_ChuckNorrisCoders_shouldValidate() {
        type = Type.CHUCK_NORRIS;
    }
	
	@FastTest
    public void givenValidInputs_URLCoders_shouldValidate() {
        type = Type.URL;
    }
}
