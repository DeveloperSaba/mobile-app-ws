/**
 * 
 */
package com.appsdeveloperblog.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;

/**
 * @author saba
 *
 */

@Repository
public interface UserRepository<T> extends PagingAndSortingRepository<UserEntity, Long> { // CrudRepository<UserEntity, Long> {
	
	UserEntity findUserByEmail(String email);

	UserEntity findByUserId(String userId);
	

}
