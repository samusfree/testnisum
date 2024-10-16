package com.nisum.testnisum.mappers;

import com.nisum.testnisum.data.entity.Phone;
import com.nisum.testnisum.data.entity.User;
import com.nisum.testnisum.dto.PhoneDTO;
import com.nisum.testnisum.dto.UserDTO;
import com.nisum.testnisum.dto.UserRequestDTO;
import com.nisum.testnisum.security.CustomUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User fromRequest(UserRequestDTO request, String token);

    Phone fromPhoneDTO(UUID userId, PhoneDTO phoneDTO);

    PhoneDTO fromPhone(Phone phone);

    @Mapping(target = "created", source = "user.createdAt")
    @Mapping(target = "modified", source = "user.updatedAt")
    @Mapping(target = "lastLogin", source = "user.lastLogin")
    UserDTO fromUser(User user, List<PhoneDTO> phones);

    CustomUserDetails customUserDetailsFromUser(User user);
}
