package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoriesService {

    // Администраторская часть
    CategoryDto createCategory(NewCategoryDto newCategory);

    CategoryDto patchCategory(Integer categoryId, CategoryDto updatedCategory);

    void deleteCategory(Integer categoryId);

    // Публичная часть
    Collection<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Integer categoryId);
}
