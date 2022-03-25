#Persistent
#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
#SingleInstance, force

class Browser {
	__New(visibility:=true, fullscreen:=false) {
		this.htmlAttribs := new HTMLAttribs()
		this.wb := ComObjCreate("InternetExplorer.Application")  	; Create an IE object
		WinWait, ahk_exe iexplore.exe								; wait for it to open 		#cleanCode
		WinGet, winId, ID, ahk_exe iexplore.exe						; you need the wb's 'ahk_id' for window title
		this.winId := winId
		
		if (fullscreen and visibility)
			this.wb.fullscreen := fullscreen
		this.wb.visible := visibility
	}
	
	__Delete() {
		this.htmlAttribs.__Delete()
		this.exit()
	}
	
	exit() {
		try, this.wb.quit()
		catch, e 
		{}
	}
	
	navigate(url) {
		this.wb.navigate(url)
		this.waitLoading()
	}
	
	waitLoading()  {
		If !this.wb    	; If wb is not a valid pointer then quit
			Return
		Loop   	 		; Otherwise sleep for .1 seconds untill the page starts loading
			Sleep,100
		Until (!this.wb.busy or not (this.wb.Document.Readystate = "Complete"))
		;Loop    		; Once it starts loading wait until completes
			;Sleep,100
		;Until (!this.wb.busy)
		;Loop    		; optional check to wait for the page to completely load
			;Sleep,100
		;Until (this.wb.Document.Readystate = "Complete")
	}
	
	extractFromName(name) {
		return this.wb.document.all[name]
	}
	
	extract(criteria, target, child:="", property:="") {
		if (criteria = "id")
			node := this.wb.document.getElementById(target)
		else if (criteria = "name")
			node := this.wb.document.getElementsByName(target)
		else if (criteria = "tag")
			node := this.wb.document.getElementsByTagName(target)
		if (child != "")
			node := node[child]
		if (property != "")
			return node[property]
		return node
	}
	
	extractForm(formName, fieldName:="", value:=false) {
		node := this.wb.document.forms[formName]
		if (fieldName != "")
			node := node[fieldName]
		if (value)
			return node.value
		return node
	}
	
	executeJS(js) {
		this.wb.navigate("javascript: " js)
	}
	
	searchGoogle(query) {
		this.navigate("www.google.com")
		node := this.extractFromName("q") 
		node.value := query
		form := this.extractForm("f")
		form.submit()
	}
	
}

;wallpaper := "file:///D:/Users/Satsana/Desktop/Workspaces/Web/Custom%20Wallpaper/AudioMatrix%201.1/index.html"
browser := new Browser()	; (true,true)
;msgbox % browser.extract("tag","a").length				; number of 'a' tags AKA links
;msgbox % browser.extract("tag","a")[0].innerText		; extract text from first 'a' tag 
;msgbox % browser.extract("tag","a",0).innerText			; same
;msgbox % browser.extractText("tag","a",0)				; same
;browser.executeJS("alert('hallow warld!')")				; execute custom javascript code
;msgbox % browser.extractForm("f","source",true)
;msgbox % browser.extract("tag", "a", 0, "innerText")		; extract text from first 'a' tag 
browser.searchGoogle("dick")
return


Escape::browser.exit()