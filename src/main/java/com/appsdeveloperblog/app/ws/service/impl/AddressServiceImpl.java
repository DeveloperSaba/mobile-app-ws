/**
 * 
 */
package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;

/**
 * @author saba
 *
 */

@Service
public class AddressServiceImpl implements AddressService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDTO> getAddresses(String userId) {
		
		List<AddressDTO> returnValue = new ArrayList<>();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null ) return returnValue;
		
		Iterable<AddressEntity> address = addressRepository.findAllByUserDetails(userEntity);
		ModelMapper modelMapper = new ModelMapper();
		for(AddressEntity addressEntity: address)
        {
            returnValue.add( modelMapper.map(addressEntity, AddressDTO.class) );
        }
		
		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String addressId) {
		
		AddressDTO returnValue = new AddressDTO();
		ModelMapper modelMapper = new ModelMapper();
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if(addressEntity!= null) {
			returnValue = modelMapper.map(addressEntity, AddressDTO.class);
		}
		
		return returnValue;
	}

}
