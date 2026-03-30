package com.mgr.api.controller;

import com.mgr.api.dto.ApiMessageDto;
import com.mgr.api.dto.ErrorCode;
import com.mgr.api.dto.ResponseListDto;
import com.mgr.api.dto.news.NewsDto;
import com.mgr.api.exception.NotFoundException;
import com.mgr.api.form.news.CreateNewsForm;
import com.mgr.api.form.news.UpdateNewsForm;
import com.mgr.api.mapper.NewsMapper;
import com.mgr.api.model.Category;
import com.mgr.api.model.News;
import com.mgr.api.model.criteria.NewsCriteria;
import com.mgr.api.repository.CategoryRepository;
import com.mgr.api.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/news")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class NewsController extends ABasicController {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private NewsMapper newsMapper;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_C')")
    @Transactional
    public ApiMessageDto<NewsDto> create(@Valid @RequestBody CreateNewsForm createNewsForm, BindingResult bindingResult) {
        Category category = categoryRepository.findById(createNewsForm.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

        News news = newsMapper.fromCreateFormToEntity(createNewsForm);
        news.setCategory(category);

        return makeSuccessResponse(newsMapper.fromEntityToDto(newsRepository.save(news)), "Create news success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_U')")
    @Transactional
    public ApiMessageDto<NewsDto> update(@Valid @RequestBody UpdateNewsForm updateNewsForm, BindingResult bindingResult) {
        News news = newsRepository.findById(updateNewsForm.getId())
                .orElseThrow(() -> new NotFoundException("News not found", "ERROR-NEWS-001"));

        Category category = categoryRepository.findById(updateNewsForm.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

        newsMapper.mappingUpdateFormToEntity(updateNewsForm, news);
        news.setCategory(category);

        return makeSuccessResponse(newsMapper.fromEntityToDto(newsRepository.save(news)), "Update news success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_V')")
    public ApiMessageDto<NewsDto> get(@PathVariable("id") Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found", "ERROR-NEWS-001"));
        return makeSuccessResponse(newsMapper.fromEntityToDto(news), "Get news success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_L')")
    public ApiMessageDto<ResponseListDto<List<NewsDto>>> list(NewsCriteria criteria, Pageable pageable) {
        Page<News> page = newsRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<NewsDto>> response = makeResponseListDto(page, list -> newsMapper.fromEntityListToDtoList(list));
        return makeSuccessResponse(response, "Get list news success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NEWS_D')")
    @Transactional
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        if (!newsRepository.existsById(id)) {
            throw new NotFoundException("News not found", "ERROR-NEWS-001");
        }
        newsRepository.deleteById(id);
        return makeSuccessResponse("Delete news success");
    }
}