package com.mgr.api.controller;

import com.mgr.api.dto.ApiMessageDto;
import com.mgr.api.dto.ErrorCode;
import com.mgr.api.dto.ResponseListDto;
import com.mgr.api.dto.category.CategoryDto;
import com.mgr.api.exception.NotFoundException;
import com.mgr.api.external.InternalClient;
import com.mgr.api.form.category.CreateCategoryForm;
import com.mgr.api.form.category.UpdateCategoryForm;
import com.mgr.api.mapper.CategoryMapper;
import com.mgr.api.model.Category;
import com.mgr.api.model.criteria.CategoryCriteria;
import com.mgr.api.repository.CategoryRepository;
import com.mgr.api.service.category.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired private InternalClient internalClient;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAT_C')")
    public ApiMessageDto<CategoryDto> create(@Valid @RequestBody CreateCategoryForm createCategoryForm, BindingResult bindingResult) {
        // Lưu ý: Không cần check bindingResult.hasErrors() vì BindingErrorsHandler Aspect đã làm thay bạn.
        return makeSuccessResponse(categoryService.create(createCategoryForm), "Create category success.");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAT_U')")
    public ApiMessageDto<CategoryDto> update(@Valid @RequestBody UpdateCategoryForm updateCategoryForm, BindingResult bindingResult) {
        return makeSuccessResponse(categoryService.update(updateCategoryForm), "Update category success.");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAT_V')")
    public ApiMessageDto<CategoryDto> get(@PathVariable("id") Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
        return makeSuccessResponse(categoryMapper.fromEntityToDto(category), "Get category success.");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAT_L')")
    public ApiMessageDto<ResponseListDto<List<CategoryDto>>> list(CategoryCriteria criteria, Pageable pageable) {
        Page<Category> page = categoryRepository.findAll(criteria.getSpecification(), pageable);

        // Sử dụng hàm makeResponseListDto cực mạnh từ ABasicController
        ResponseListDto<List<CategoryDto>> responseListDto = makeResponseListDto(page, list -> categoryMapper.fromEntityListToDtoList(list));

        return makeSuccessResponse(responseListDto, "Get list category success.");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CAT_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
        return makeSuccessResponse("Delete category success.");
    }

    @GetMapping("/list-internal")
    public ApiMessageDto<ResponseListDto<List<CategoryDto>>> listInternal() {
        // Tự gọi chính mình để demo Feign
        // Lấy token hiện tại của người đang gọi để truyền vào Feign
        String token = "Bearer " + getCurrentToken();
        return internalClient.getCategories(token);
    }
}