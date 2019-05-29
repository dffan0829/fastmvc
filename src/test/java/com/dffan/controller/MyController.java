package com.dffan.controller;

import com.dffan.annotation.FastController;
import com.dffan.annotation.FastMapping;

@FastController("mycontroller")
@FastMapping("/my")
public class MyController {

	@FastMapping("/info")
	public String getInfo(){
		return "";
	}
	
}
