package com.mgr.api.mapper;

import com.mgr.api.dto.news.NewsDto;
import com.mgr.api.form.news.CreateNewsForm;
import com.mgr.api.form.news.UpdateNewsForm;
import com.mgr.api.model.News;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class})
public interface NewsMapper {

    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    News fromCreateFormToEntity(CreateNewsForm form);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "status", target = "status")
    void mappingUpdateFormToEntity(UpdateNewsForm form, @MappingTarget News news);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
        // ĐÃ XÓA MAPPING createdBy VÀ modifiedBy VÌ DTO KHÔNG CÓ TRƯỜNG NÀY
    NewsDto fromEntityToDto(News news);

    @IterableMapping(elementTargetType = NewsDto.class)
    List<NewsDto> fromEntityListToDtoList(List<News> list);
}