package com.zyz.socket.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zyz.socket.service.CGSocketThread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SocketConfig {

	@Value("${config.socket.serverIpTo}")
	private String serverIpTo;

	@Value("${config.socket.timeout}")
	private Integer timeout;

	@Value("${config.socket.serverportTo}")
	private int serverportTo;

	@Value("${config.socket.serverportFrom}")
	private int serverportFrom;

	@Value("${config.socket.maxpoolsize}")
	private int smaxpoolsize;

	@Bean
	public String ioAcceptor() throws Exception {
		new Thread() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				// 修改线程池大小修改为配置
				ThreadPoolExecutor threadPool = new ThreadPoolExecutor(smaxpoolsize / 2, smaxpoolsize, 0,
						TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

				ServerSocket server = null;
				try {
					log.info("启动socket服务开始");
					server = new ServerSocket(serverportFrom);
					log.info("启动socket服务成功");
				} catch (Exception e) {
					log.error("启动socket服务发生异常", e);
				}

				Socket socket = null;
				while (true) {
					int activeCount = threadPool.getActiveCount();
					int corePoolSize = threadPool.getCorePoolSize();
					int maximumPoolSize = threadPool.getMaximumPoolSize();
					// 已经激活的线程数 < 最大线程数
					if (activeCount < maximumPoolSize) {
						try {
							socket = server.accept();
						} catch (IOException e) {
							log.error("接收socket消息发生异常, e: {}", e);
							shutdownSocket(socket);
							continue;
						}
						try {
							threadPool.execute(new CGSocketThread(socket, serverIpTo, serverportTo, timeout));
						} catch (Exception e) {
							log.error("新开线程异常: e: {}", e);
							shutdownSocket(socket);
						}
					} else {
						log.warn("已激活线程数大于等于最大线程数了: 已激活线程数: {}, 核心线程数: {}, 最大线程数: {}, requestId: {}", activeCount,
								corePoolSize, maximumPoolSize);
					}
				}
			}
		}.start();

		return "Success";
	}

	/**
	 * 断开socket连接
	 */
	private void shutdownSocket(Socket socket) {
		try {
			socket.shutdownInput();
			OutputStream os = socket.getOutputStream();
			String result = "error";
			os.write(result.getBytes());
			os.flush();
			socket.shutdownOutput();
		} catch (Exception e) {
			log.error("关闭socket异常: e: {}", e);
		}
	}
}
