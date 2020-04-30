package com.zyz.socket.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SocketClientService {
	/**
	 * 作为客户端像重高发请求
	 * 
	 * @author zyz
	 * @param typeCode: 交易码值
	 * @param body: 发送信息
	 * @return
	 */
	public static  sendToServer( String body) {
		
		result = SocketUtils.sendAndReceive(config.serverIp, config.serverport, config.timeout, rsaStr);
		
		// 发送请求
		StringBuilder sb = new StringBuilder();
		String header = ApiEnum.CONSTANT_BANK_CODE.getCode() + transNoEnum.getValue() + "1";
		try {
			if (transNoEnum != TransNoEnum.BILL_COMPARE_NOTIFY) {
				sb.append(String.format("%04d", (header + body).getBytes(GCStringUtils.charset).length));
				sb.append(header);
			}
			sb.append(body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String code, message, data;
		JsonResponse jr = null;
		try {
			logger.info(logHead + " --> 报文请求信息 ==> {}", sb.toString());
			rsaStr = RSA.encrypt(sb.toString(), config.publicKey);// 加密发送
			
			logger.info(logHead + " --> 接收重高返回数据 <== {}", result);
			result = RSA.decrypt(result, config.privateKey);
			logger.info(logHead + " --> 解密重高返回数据 <== {}", result);

			if (transNoEnum.equals(TransNoEnum.JSON_DEAL)) {
				if (StringUtils.isNotEmpty(result) && result.contains("{")) {
					String ss = result.substring(result.indexOf("{"), result.length());
					Map<String, Object> map = JsonUtil.fromJson(ss);
					code = map.get("ret_code").toString();
					message = map.get("ret_mesg").toString();
					data = result;
				} else {
					return JsonResponse.fail(transNoEnum, ErrorCode.SERVER_EXCEPTION);
				}
			} else if (transNoEnum.equals(TransNoEnum.CARD_INFO)) {
				Message msg = new Message(result);
				code = ApiEnum.CG_RETURN_SUCCESS_CODE.getCode();
				message = data = msg.getBody();
			} else {
				Message msg = new Message(result);
				String[] bodySplit = msg.getBody().split(",");
				code = bodySplit[0];
				message = bodySplit[1];
				data = msg.getBody();
			}

			jr = new JsonResponse(transNoEnum);
			jr.setCode(code);
			jr.setMessage(message);
			jr.setData(data);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResponse.fail(transNoEnum, ErrorCode.SERVER_EXCEPTION);
		}
		return jr;
	}
}

public class CgScoketClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(CgScoketClientUtils.class);

	public static void main(String[] args) {
		String result;
		String rsaStr = "H6n0mbU5zY1SbdxMwgWVeR0aWTpKmRkSSNLldfRJggYjFNeXSV6iRebURfmK/CPtdWvygDzvxce3On7MDw6dTOu8qkx4agksQDFH2ABUtGOzl5mD3rI+w1YVsxmUIJ+AnuRcBOl7H+osgAqfN6I0HfNWaKiZzPPKD9QLHVXSiK4=";
		result = SocketUtils.sendAndReceive("192.168.190.226", 8889, 111111, rsaStr);

		System.out.println(result);
	}
}
