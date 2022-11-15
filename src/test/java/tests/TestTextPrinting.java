package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import commands.level.normal.Help;
import lib.PrintBooster;

class TestTextPrinting {

	@Test
	void givenMarkdown_whenPositionedAtEnd_splitsCorrectly() {
		StringBuilder sb = new StringBuilder()
			.append(Help.helpGeneral())
			.append(Help.helpGlobal())
			.append(Help.helpRead());
		String expected = sb.toString();
		List<String> list = PrintBooster.split(sb.toString());
		assertEquals(expected, String.join("", list));
	}

}
