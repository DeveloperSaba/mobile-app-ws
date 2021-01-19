/**
 * 
 */
package com.appsdeveloperblog.app.ws.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.exception.UserServiceException;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserResponse;

/**
 * @author saba
 *
 */
@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping(path = "/{id}") // , produces = MediaType.APPLICATION_XML_VALUE)
	public UserResponse getUser(@PathVariable String id) {

		UserResponse returnValue = new UserResponse();
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		return returnValue;
	}

	@PostMapping
	public UserResponse createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserResponse returnValue = new UserResponse();
		// if(userDetails.getFirstName().isEmpty()) throw new
		// UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessages());
		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException("The Object is Null");
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnValue);

		return returnValue;

	}

	@PutMapping(path = "/{id}")
	public UserResponse updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserResponse returnValue = new UserResponse();
		// if(userDetails.getFirstName().isEmpty()) throw new NullPointerException("The
		// Object is Null");
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
		// return "update user was called";

	}

	@DeleteMapping(path = "/{id}")
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returnValue = new OperationStatusModel();
		// returnValue.setOperationName("DELETE");

		returnValue.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return returnValue;
		// return "delete user was called";
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserResponse> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "page", defaultValue = "25") int limit) {

		List<UserResponse> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		for (UserDto userDto : users) {
			UserResponse userModel = new UserResponse();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);

		}
		return returnValue;
	}
}
