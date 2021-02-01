/**
 * 
 */
package com.appsdeveloperblog.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressResponse;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserResponse;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

/**
 * @author saba
 *
 */
@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;

	@GetMapping(path = "/{id}") // , produces = MediaType.APPLICATION_XML_VALUE)
	public UserResponse getUser(@PathVariable String id) {

		UserResponse returnValue = new UserResponse();
		UserDto userDto = userService.getUserByUserId(id);
		// BeanUtils.copyProperties(userDto, returnValue);
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(userDto, UserResponse.class);
		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserResponse createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserResponse returnValue = new UserResponse();

		// if(userDetails.getFirstName().isEmpty()) throw new
		// UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessages());
		// if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The
		// Object is Null");

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserResponse.class);

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

	// http://localhost:8080/mobile-app-ws/users/wZ33j1Wvef3BfpRTKxDS15bZhHGCLx
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public List<AddressResponse> getUserAddresses(@PathVariable String id) {

		List<AddressResponse> returnValue = new ArrayList<>();

		List<AddressDTO> addressesDTO = addressService.getAddresses(id);
		ModelMapper modelMapper = new ModelMapper();

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressResponse>>() {
			}.getType();
			returnValue = modelMapper.map(addressesDTO, listType);
		}
		return returnValue;
	}

	// http://localhost:8080/mobile-app-ws/users/wZ33j1Wvef3BfpRTKxDS15bZhHGCLx/addresses/IYwpWQeVdyKTZqzyi3RrY7DI2XaJSK

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	//public AddressResponse getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
	public EntityModel<AddressResponse> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDTO addressDTO = addressService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		AddressResponse returnValue = modelMapper.map(addressDTO, AddressResponse.class);
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");

		//Link userAddressLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");
		
		//methodOn method instead of .slash method
		Link userAddressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
				//.slash(userId)
				//.slash("addresses")
				.withRel("addresses");

		//Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
				//.slash(userId)
				//.slash("addresses")
				//.slash(addressId)
				.withSelfRel();
		
		//for entityModel we can remove it from here and add it directly at line 180
		//returnValue.add(userLink);
		//returnValue.add(userAddressLink);
		//returnValue.add(selfLink);
		
		//if using EntityModel of hateoas then no need to use extends RepresentationModel<AddressResponse>
		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressLink, selfLink));
		//return returnValue;
	}
}
