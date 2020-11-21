#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.

; Build java enum code
upperCode := "public enum Emoji {`n"
lowerCode :=
(
	private long unicode;
	
	private Emoji(long unicode) {
		this.unicode = unicde;
	}
	
	public String toString() {
		return new String(Character.toChars(unicode));
	}
}
)

codeFileName := "Emoji.java"
code := top
; Scraping code
wb := new Browser(true,false)
wb.navigate("https://unicode.org/emoji/charts/emoji-list.html")
table := wb.extract("tag", "table")[0]
Loop, % table.rows.length
{
	row := table.rows[A_Index]
	if (isNumber(row.cells[0].innerText)) {
		unicode := row.cells[1].innerText
		name := row.cells[3].innerText
		msgbox %  enumify(name, unicode)
		code .= enumify(name, unicode) . ((A_Index == table.rows.length) ? ";`n" : ",`n")
	}
}
code .= bottom
FileAppend, % code, % codeFileName
Run, % A_ScriptDir
return

enumify(name,unicode) {
	StringUpper, nameUpper, name
	nameUpper := RegExReplace(nameUpper, "\s", "_")
	nameUpper :=  nameUpper "(" RegExReplace(unicode, "U\+", "0x") ")"
	return nameUpper
}

isNumber(num) {
	isNum := true
	if (num is not Integer)
		isNum := false
	Loop, parse, % num
		if (!isDigit(A_LoopField))
			isNum := false
	return isNum
}
		
isDigit(digit) {
	digits := ["0","1","2","3","4","5","6","7","8","9"]
	isDigit := false
	for key, value in digits
		if (value == digit)
			isDigit := true
	return isDigit
}

#include, Browser.ahk