package com.dffan.controller;

import com.dffan.annotation.FastController;
import com.dffan.annotation.FastMapping;
import com.dffan.annotation.FastParam;

@FastController("mycontroller")
@FastMapping("/my")
public class MyController {

	@FastMapping("/info")
	public String getInfo(@FastParam(value="nm") String name,@FastParam(value="na") String nam1){
		System.out.println(name+","+nam1);              
		return "";
	}
	
}
