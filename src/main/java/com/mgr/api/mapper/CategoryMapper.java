package com.mgr.api.mapper;

import com.mgr.api.dto.category.CategoryDto;
import com.mgr.api.form.category.CreateCategoryForm;
import com.mgr.api.form.category.UpdateCategoryForm;
import com.mgr.api.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category fromCreateFormToEntity(CreateCategoryForm form);

    @Mapping(target = "id", ignore = true)
    void mappingUpdateFormToEntity(UpdateCategoryForm form, @MappingTarget Category category);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdDate", target = "createdDate")
//    @Mapping(source = "modifiedBy", target = "modifiedBy")
//    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToDto")
    CategoryDto fromEntityToDto(Category category);

    @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "fromEntityToDto")
    List<CategoryDto> fromEntityListToDtoList(List<Category> list);
}