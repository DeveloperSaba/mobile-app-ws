/**
 * 
 */
package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.UserResponse;

/**
 * @author saba
 *
 */
//Service Layer Class

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepository.findUserByEmail(user.getEmail()) != null)
			throw new RuntimeException("Record already exist");

		// UserEntity userEntity = new UserEntity();
		// BeanUtils.copyProperties(user, userEntity);

		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}

		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);

		UserEntity savedUserDetails = (UserEntity) userRepository.save(userEntity);
		// UserDto returnValue = new UserDto();

		// BeanUtils.copyProperties(savedUserDetails, returnValue);
		UserDto returnValue = modelMapper.map(savedUserDetails, UserDto.class);

		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findUserByEmail(email);
		
		if (userEntity == null)
			throw new UsernameNotFoundException("User Not found");
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
				true, true, true,  new ArrayList<>());
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findUserByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("User with ID: " + userId + " not found");
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto users) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());

		userEntity.setFirstName(users.getFirstName());
		userEntity.setLastName(users.getLastName());

		UserEntity updatedUserDetails = (UserEntity) userRepository.save(userEntity);

		BeanUtils.copyProperties(updatedUserDetails, returnValue);

		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {

		List<UserDto> returnValue = new ArrayList<>();
		// for pagination
		if (page > 0)
			page = page - 1;
		PageRequest pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = userPage.getContent();

		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);

		}
		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {

		boolean returnValue = false;

		// Find user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

		if (userEntity != null) {
			boolean hastokenExpired = Utils.hasTokenExpired(token);
			if (!hastokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		return returnValue;
	}

}
