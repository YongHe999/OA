package com.seu.main.service;


import com.seu.main.dto.LoginUser;
import com.seu.main.dto.TokenDto;
import com.seu.util.entity.Token;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Token管理器<br>
 *
 */
@Service
public interface TokenService {

	TokenDto save(LoginUser loginUser);

	void refresh(LoginUser loginUser);

	LoginUser getLoginUser(String token);


	boolean delete(String token);

	List<Token> listTokens();

	boolean deleteTokenById(String id);

}
