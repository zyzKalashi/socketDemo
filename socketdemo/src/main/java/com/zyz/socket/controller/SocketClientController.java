package com.zyz.socket.controller;

import org.apache.commons.lang3.StringUtils;
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
		if (StringUtils.isNotEmpty(msg)) {
			this.socketClientService.sendToServer(transNoEnum, body);
		}
		return "";
	}
	
	
	
	

}
