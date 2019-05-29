package com.dffan.controller.web;

import com.dffan.annotation.FastMapping;


@FastMapping("/web")
public class WebController {
	
	@FastMapping("/info")
	public String getInfo(){
		return "";
	}
}
