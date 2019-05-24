package com.duangframework.token;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.server.common.BootStrap;

import java.util.*;

/**
 * TokenManager.
 * @author laotang
 * @date 2019-5-22
 */
public class TokenManager {
	
	private static ITokenCache tokenCache;
	private static Random random = new Random();
	/**300 seconds ---> 5 minutes */
	private static int MIN_SECONDS_OF_TOKEN_TIME_OUT = 300;
	public static final String TOKEN_KEY_FIELD = "htmlTokenId";
	public static final String TOKEN_KEY_HEAD_FIELD = "htmlToken";
	public static final String TOKEN_KEY_HEAD_VALUE = "duang-htmlToken";



	private TokenManager() {
		
	}
	
	public static void init(ITokenCache tokenCache) {
		if (tokenCache == null) {
			return;
		}
		
		TokenManager.tokenCache = tokenCache;
		// Token最小过期时间的一半时间作为任务运行的间隔时间
		long halfTimeOut = MIN_SECONDS_OF_TOKEN_TIME_OUT * 1000 / 2;
		new Timer("TokenManager", true).schedule(
				new TimerTask() {
					@Override
					public void run() {
						removeTimeOutToken();
					}
				},halfTimeOut,halfTimeOut);
	}

	public static String createToken(String tokenName) {
		return createToken(tokenName, 0);
	}

	public static String createToken(String tokenName, int secondsOfTimeOut) {
		if (null == tokenCache) {
			String tokenId = String.valueOf(random.nextLong());
			return createTokenHiddenField(tokenName, tokenId);
		}
		else {
			return createTokenUseTokenIdGenerator(tokenName, secondsOfTimeOut);
		}
	}

	/**
	 * Create Token.
	 * @param controller
	 * @param tokenName token name
	 * @param secondsOfTimeOut seconds of time out, for ITokenCache only.
	 */
	public static void createToken(BaseController controller, String tokenName, int secondsOfTimeOut) {
		if (null == tokenCache) {
			String tokenId = String.valueOf(random.nextLong());
			controller.setValue(tokenName, tokenId);
			createTokenHiddenField(controller, tokenName, tokenId);
		}
		else {
			createTokenUseTokenIdGenerator(controller, tokenName, secondsOfTimeOut);
		}
	}

	private static String createTokenHiddenField(String tokenName, String tokenId) {
		return createTokenHiddenField(null, tokenName, tokenId);
	}
	/**
	 * Use ${token!} in view for generate hidden input field.
	 */
	private static String createTokenHiddenField(BaseController controller, String tokenName, String tokenId) {
		StringBuilder sb = new StringBuilder();
		sb.append("<input type='hidden' name='").append(tokenName).append("' id='").append(tokenName).append("' value='").append(tokenId).append("' />");
		if(null != controller) {
			controller.setValue(TOKEN_KEY_FIELD, sb.toString());
		}
		return sb.toString();
	}

	private static String createTokenUseTokenIdGenerator(String tokenName, int secondsOfTimeOut) {
		return createTokenUseTokenIdGenerator(null, tokenName, secondsOfTimeOut);
	}
	private static String createTokenUseTokenIdGenerator(BaseController controller, String tokenName, int secondsOfTimeOut) {
		if (secondsOfTimeOut < MIN_SECONDS_OF_TOKEN_TIME_OUT) {
			secondsOfTimeOut = MIN_SECONDS_OF_TOKEN_TIME_OUT;
		}
		
		String tokenId = null;
		Token token = null;
		int safeCounter = 8;
		do {
			if (safeCounter-- == 0) {
				throw new RuntimeException("Can not create tokenId.");
			}
			tokenId = String.valueOf(random.nextLong());
			token = new Token(tokenId, System.currentTimeMillis() + (secondsOfTimeOut * 1000));
		} while(tokenId == null || tokenCache.contains(token));

		tokenCache.put(token);
		if(null != controller) {
			controller.setValue(tokenName, tokenId);
		}
		return createTokenHiddenField(controller, tokenName, tokenId);
	}

	public static boolean validateToken(IRequest request, String tokenName) {
		if(!BootStrap.getInstants().isTokenHtml()) {
			return true;
		}
		String headValue = request.getHeader(TOKEN_KEY_HEAD_FIELD);
		String clientTokenId = request.getHeader(tokenName);
		if(ToolsKit.isEmpty(clientTokenId)) {
			clientTokenId = request.getParameter(tokenName);
			if(ToolsKit.isEmpty(clientTokenId)) {
				clientTokenId = request.getAttribute(tokenName)+"";
			}
		}
		if (tokenCache == null) {
			throw new NullPointerException("请先实现ITokenCache接入，并调用init方法");
		}
		if(ToolsKit.isEmpty(clientTokenId) && BootStrap.getInstants().isTokenHtml() && !TOKEN_KEY_HEAD_VALUE.equalsIgnoreCase(headValue)) {
			throw new NullPointerException("请先设置request header["+TOKEN_KEY_HEAD_FIELD+"]为: " + TOKEN_KEY_HEAD_VALUE);
		} else {
			Token token = new Token(clientTokenId);
			boolean result = tokenCache.contains(token);
			tokenCache.remove(token);
			return result;
		}

	}
	
	/**
	 * Check token to prevent resubmit.
	 * @param tokenName the token name used in view's form
	 * @return true if token is correct
	 */
	public static boolean validateToken(BaseController controller, String tokenName) {
		String clientTokenId = controller.getValue(tokenName);
		if (tokenCache == null) {
			String serverTokenId = controller.getValue(tokenName);
			return ToolsKit.isNotEmpty(clientTokenId) && clientTokenId.equals(serverTokenId);
		}
		else {
			Token token = new Token(clientTokenId);
			boolean result = tokenCache.contains(token);
			tokenCache.remove(token);
			return result;
		}
	}
	
	private static void removeTimeOutToken() {
		List<Token> tokenInCache = tokenCache.getAll();
		if (tokenInCache == null) {
			return;
		}
		
		List<Token> timeOutTokens = new ArrayList<Token>();
		long currentTime = System.currentTimeMillis();
		// find and save all time out tokens
		for (Token token : tokenInCache) {
			if (token.getExpirationTime() <=  currentTime) {
				timeOutTokens.add(token);
			}
		}
		
		// remove all time out tokens
		for (Token token : timeOutTokens) {
			tokenCache.remove(token);
		}
	}
}





