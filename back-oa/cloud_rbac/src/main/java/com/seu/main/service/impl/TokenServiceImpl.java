package com.seu.main.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.seu.main.mapper.TokenMapper;
import com.seu.main.dto.LoginUser;
import com.seu.main.dto.TokenDto;
import com.seu.main.service.LogService;
import com.seu.main.service.TokenService;
import com.seu.util.entity.Token;
import com.seu.util.sysEnum.LoggerFlagEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

/**
 * token存到数据库的实现类
 * 
 * @author Ajie
 *
 */
@Service
public class TokenServiceImpl implements TokenService {

	private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);
	/**
	 * token过期秒数
	 */
	@Value("${token.expire.seconds}")
	private Integer expireSeconds;
	@Autowired
	private TokenMapper tokenMapper;
	@Autowired
	private LogService logService;
	/**
	 * 私钥
	 */
	@Value("${token.jwtSecret}")
	private String jwtSecret;

	private static Key KEY = null;
	private static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";

	@Override
	public TokenDto save(LoginUser loginUser) {
		loginUser.setToken(UUID.randomUUID().toString());
		loginUser.setLoginTime(System.currentTimeMillis());
		loginUser.setExpireTime(loginUser.getLoginTime() + expireSeconds * 1000);

		Token token = new Token();
		token.setId(loginUser.getToken());
		token.setCreateTime(DateUtil.date());
		token.setUpdateTime(DateUtil.date());
		token.setExpireTime(new Date(loginUser.getExpireTime()));
		token.setVal(JSONObject.toJSONString(loginUser));

		tokenMapper.insert(token);
		// 登陆日志
		logService.save("", LoggerFlagEnum.NORMAL.getValue(),"登录成功");

		String jwtToken = createJWTToken(loginUser);

		return new TokenDto(jwtToken, loginUser.getLoginTime());
	}

	/**
	 * 生成jwt
	 * 
	 * @param loginUser
	 * @return
	 */
	private String createJWTToken(LoginUser loginUser) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(LOGIN_USER_KEY, loginUser.getToken());// 放入一个随机字符串，通过该串可找到登陆用户

		String jwtToken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, getKeyInstance())
				.compact();

		return jwtToken;
	}

	@CacheEvict(value = "Token", allEntries=true)
	@Override
	public void refresh(LoginUser loginUser) {
		loginUser.setLoginTime(System.currentTimeMillis());
		loginUser.setExpireTime(loginUser.getLoginTime() + expireSeconds * 1000);

		Token token = tokenMapper.selectById(loginUser.getToken());
		token.setUpdateTime(DateUtil.date());
		token.setExpireTime(new Date(loginUser.getExpireTime()));
		token.setVal(JSONObject.toJSONString(loginUser));

		tokenMapper.updateById(token);
	}


	@Cacheable(value = "Token", key = "'getLoginUser'",unless="#result == null")
	@Override
	public LoginUser getLoginUser(String jwtToken) {

		String uuid = getUUIDFromJWT(jwtToken);

		if (uuid != null) {
			Token token = tokenMapper.selectById(uuid);
			return toLoginUser(token);
		}
		return null;
	}


	@Caching(evict={@CacheEvict(value = "Token",allEntries=true),
			@CacheEvict(value = "UserDetails",allEntries=true)})
	@Override
	public boolean delete(String jwtToken) {

		String uuid = getUUIDFromJWT(jwtToken);
		if (uuid != null) {
			Token token = tokenMapper.selectById(uuid);
			LoginUser loginUser = toLoginUser(token);
			if (loginUser != null) {
				return tokenMapper.deleteById(uuid)>0;
			}
		}
		return false;
	}

	@Override
	public List<Token> listTokens() {
		return tokenMapper.selectList(null);
	}

	@CacheEvict(value = "Token", allEntries=true)
	@Override
	public boolean deleteTokenById(String id) {
		return tokenMapper.deleteById(id) > 0;
	}


	private LoginUser toLoginUser(Token token) {
		if (token == null) {
			return null;
		}
		// 校验是否已过期
		if (token.getExpireTime().getTime() > System.currentTimeMillis()) {
			return JSONObject.parseObject(token.getVal(), LoginUser.class);
		}

		return null;
	}

	private Key getKeyInstance() {
		if (KEY == null) {
			synchronized (TokenServiceImpl.class) {
				if (KEY == null) {// 双重锁
					byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
					KEY = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
				}
			}
		}

		return KEY;
	}

	private String getUUIDFromJWT(String jwt) {
		if ("null".equals(jwt) || StringUtils.isBlank(jwt)) {
			return null;
		}
		Map<String, Object> jwtClaims = null;
		try {
			jwtClaims = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(jwt).getBody();
			return MapUtils.getString(jwtClaims, LOGIN_USER_KEY);
		} catch (ExpiredJwtException e) {
			log.error("{}已过期", jwt);
		} catch (Exception e) {
			log.error("{}", e);
		}
		return null;
	}

}
