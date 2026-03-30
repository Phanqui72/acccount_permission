package com.mgr.api.mapper;

import com.mgr.api.dto.user.UserDto;
import com.mgr.api.form.user.UpdateUserForm;
import com.mgr.api.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AccountMapper.class})
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account", target = "account")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
        // ĐÃ XÓA MAPPING createdBy VÀ modifiedBy VÌ DTO KHÔNG CÓ TRƯỜNG NÀY
    UserDto fromEntityToDto(User user);

    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "status", target = "status")
    void mappingUpdateFormToEntity(UpdateUserForm form, @MappingTarget User user);

    @IterableMapping(elementTargetType = UserDto.class)
    List<UserDto> fromEntityListToDtoList(List<User> list);
}