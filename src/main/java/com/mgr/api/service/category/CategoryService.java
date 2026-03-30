package com.mgr.api.service.category;

import com.mgr.api.dto.ErrorCode;
import com.mgr.api.dto.category.CategoryDto;
import com.mgr.api.exception.BadRequestException;
import com.mgr.api.exception.NotFoundException;
import com.mgr.api.form.category.CreateCategoryForm;
import com.mgr.api.form.category.UpdateCategoryForm;
import com.mgr.api.mapper.CategoryMapper;
import com.mgr.api.model.Category;
import com.mgr.api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto create(CreateCategoryForm form) {
        categoryRepository.findFirstByName(form.getName()).ifPresent(c -> {
            throw new BadRequestException("Category name existed", ErrorCode.CATEGORY_ERROR_NAME_EXISTED);
        });
        Category category = categoryMapper.fromCreateFormToEntity(form);
        return categoryMapper.fromEntityToDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto update(UpdateCategoryForm form) {
        Category category = categoryRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

        if (!category.getName().equalsIgnoreCase(form.getName())) {
            categoryRepository.findFirstByName(form.getName()).ifPresent(c -> {
                throw new BadRequestException("Category name existed", ErrorCode.CATEGORY_ERROR_NAME_EXISTED);
            });
        }
        categoryMapper.mappingUpdateFormToEntity(form, category);
        return categoryMapper.fromEntityToDto(categoryRepository.save(category));
    }
}