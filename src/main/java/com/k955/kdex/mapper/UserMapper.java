package com.k955.kdex.mapper;

import com.k955.kdex.dtos.User.UserProfileResponse;
import com.k955.kdex.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileResponse toUserProfileResponse(User user);

}
