package com.zyz.socket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zyz.socket.service.SocketClientService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SocketClientController {
	
	
	@Autowired
	private SocketClientService socketClientService;
	
	
	@ResponseBody
	@PostMapping(value = "/sendMsg")
	public String sendMsg(String msg) {
		log.info("msg = {}",msg);
		
		
		
		
		
		return "";
	}
	
	
	
	

}
