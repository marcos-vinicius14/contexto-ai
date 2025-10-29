package com.contextoia.identityaccess.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.contextoia.identityaccess.api.dto.UserDTO;
import com.contextoia.identityaccess.domain.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDto(User user);

    User userDtoToUser(UserDTO userDTO);

}
