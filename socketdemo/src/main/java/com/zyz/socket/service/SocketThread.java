package com.zyz.socket.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketThread extends Thread {

	private Socket socket;
	private String serverIp;
	private Integer serverportCG;
	private Integer timeout;

	public SocketThread(Socket socket, String serverIp, Integer serverportCG, Integer timeout) {
		this.socket = socket;
		this.serverIp = serverIp;
		this.serverportCG = serverportCG;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		String result = "";
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while ((result = br.readLine()) != null) {
				sb.append(result);
			}
			result = sb.toString();
			System.out.println("socket-client发送的文本内容:{}");
		} catch (Exception e) {
			log.error("error:{}", e);
		}

	}

}
