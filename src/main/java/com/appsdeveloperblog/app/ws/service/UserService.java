/**
 * 
 */
package com.appsdeveloperblog.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

/**
 * @author saba
 *
 */
public interface UserService extends UserDetailsService{
	
	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
	UserDto updateUser(String userId, UserDto users);
	void deleteUser(String userId);
	List<UserDto>getUsers(int page, int limit);

}
