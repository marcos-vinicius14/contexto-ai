package com.contextoia.identityaccess.mapper;

import org.mapstruct.Mapper;

import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.domain.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDto(User user);

    User userDtoToUser(UserDTO userDTO);

}
