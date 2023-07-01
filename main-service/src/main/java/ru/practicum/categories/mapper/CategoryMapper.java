package ru.practicum.categories.mapper;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

public class CategoryMapper {

    public static CategoryDto modelToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
