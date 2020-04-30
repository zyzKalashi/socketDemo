package com.zyz.socket.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

/**
 * Socket报文常用通讯工具
 */
@Slf4j
public class SocketUtils {

	private static final String encoding = "utf-8";

	//public static final int defaultTimeOut = 60 * 1000;

	/**
	 * 建立socket连接
	 * 
	 * @param hostname   - 主机名
	 * @param port       - 端口
	 * @param so_timeout - 读数据超时时间（百万分之一秒），milseconds
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws SocketException
	 */
	public static Socket open(String hostname, int port, int so_timeout)
			throws UnknownHostException, IOException, SocketException {
		Socket sc = null;
		try {
			sc = new Socket(hostname, port);
		} catch (UnknownHostException e) {
			log.error("无效的主机名 '" + hostname + "', " + e.getMessage());
			throw e;
		} catch (IOException e) {
			log.error("连接服务器 '" + hostname + ":" + port + "' 失败, " + e.getMessage());
			throw e;
		}

		try {
			sc.setSoTimeout(so_timeout);
		} catch (SocketException e) {
			log.error("设置连接超时时间失败, " + e.getMessage());
			throw e;
		}
		log.info("连接socket服务器成功:{}", sc);
		return sc;
	}
	
	
	public static void heartCheck() {
		int timeout = 3000; //每隔三秒发送心跳
		int num = 3; //3次心跳均未响应重连
		
		
			      
//			      timeoutObj: null,
//			      serverTimeoutObj: null,
			      start: function(){
			        var _this = this;
			        var _num = this.num;
			        this.timeoutObj && clearTimeout(this.timeoutObj);
			        this.serverTimeoutObj && clearTimeout(this.serverTimeoutObj);
			        this.timeoutObj = setTimeout(function(){
			              //这里发送一个心跳，后端收到后，返回一个心跳消息，
			              //onmessage拿到返回的心跳就说明连接正常
			              ws.send("123456789"); // 心跳包
			              _num--;
			              //计算答复的超时次数
			              if(_num === 0) {
			                   ws.colse();
			              }
			        }, this.timeout)
			      }
			}
	}
	

	/**
	 * 从socket接收数据
	 * 
	 * @param sc  - 套接字
	 * @param len - 接收数据长度
	 * @return
	 * @throws IOException
	 */
	public static byte[] receive(Socket sc, final int len) throws IOException {
		byte buf[] = new byte[len];

		int i;
		int offset = 0;

		InputStream in = sc.getInputStream();
		while (offset < len) {
			i = in.read(buf, offset, len - offset);
			if (i > 0) {
				offset += len;
			} else if (i == 0) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					log.info("wait InterruptedException", e);
				}
			} else {
				if (offset > 0) {
					log.info("socket fail, read return -1, read data=" + new String(buf, 0, offset));
				}
				throw new IOException("socket fail, read return -1");
			}
		}
		return buf;
	}

	/**
	 * 发送数据
	 * 
	 * @param sc   - socket套接字
	 * @param data - 数据
	 * @throws IOException
	 */
	public static void send(Socket sc, byte[] data) throws IOException {
		OutputStream out = sc.getOutputStream();
		log.info("开始发送数据...");
		out.write(data);
		out.flush();
		sc.shutdownOutput();
		log.info("发送结束...");
	}

	public static void send(Socket sc, String data) throws IOException {
		OutputStream out = sc.getOutputStream();
		PrintWriter pw = new PrintWriter(out);
		log.info("开始发送数据...");
		pw.write(data);
		pw.flush();
		sc.shutdownOutput();
		log.info("发送结束...");
	}

	/**
	 * 从数据流中读取指定长度的数据
	 * 
	 * @param in  - 输入流
	 * @param len - 需要读取的数据长度
	 * @return 读取到的固定长度数据
	 * @throws IOException 超时、输入流被关闭、...
	 */
	public static byte[] receive(InputStream in, final int len) throws IOException {
		byte buf[] = new byte[len];
		int i;
		int offset = 0;
		while (offset < len) {
			i = in.read(buf, offset, len - offset);
			if (i > 0) {
				offset += len;
			} else if (i == 0) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					log.info("wait InterruptedException", e);
				}
			} else {
				if (offset > 0) {
					log.info("socket fail, read return -1, read data=" + new String(buf, 0, offset));
				}
				throw new IOException("socket fail, read return -1");
			}
		}
		return buf;
	}

	public static byte[] receive(InputStream in) throws IOException {
		BufferedInputStream bufin = new BufferedInputStream(in);
		int buffSize = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);

		byte[] temp = new byte[buffSize];
		int size = 0;
		while ((size = bufin.read(temp)) != -1) {
			out.write(temp, 0, size);
		}
		bufin.close();

		byte[] content = out.toByteArray();
		return content;
	}

	/**
	 * 关闭socket连接
	 * 
	 * @param socket - 套接字
	 */
	public static void close(Socket socket) {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				log.error("关闭socket失败， " + e.getMessage(), e);
			}
		}
	}

	/**
	 * socket 发送
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @param timeout
	 * @param message
	 * 
	 * @author zyz
	 * @return
	 */
	public static String sendAndReceive(String serverIp, int serverPort, int timeout, String message) {
		Socket socket = null;
		InputStream in = null;
		BufferedReader reader = null;
		StringBuilder receiveData = new StringBuilder();
		String response = null;
		try {
			socket = open(serverIp, serverPort, timeout);
			log.info("===调用高速Socket服务请求信息:{}", message);

			send(socket, message.getBytes(encoding));
			in = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in, encoding));
			String receiveDataString = reader.readLine();
			while (receiveDataString != null && !"".equals(receiveDataString.trim())) {
				receiveData.append(receiveDataString);
				receiveDataString = reader.readLine();
			}

			response = receiveData.toString();
			log.info("===接收===接收返回数据：{}", response);

		} catch (UnknownHostException e) {
			log.error("地址解析异常, ", e);
		} catch (SocketException e) {
			log.error("设置连接超时时间失败, ", e);
		} catch (IOException e) {
			log.error("从服务器 '" + serverIp + ":" + serverPort + "' 读取数据异常, ", e);
		} catch (Exception e) {
			log.error("加解密失败 ", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (in != null)
					in.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				log.error("关闭异常", e);
			}
		}
		return response;
	}
}