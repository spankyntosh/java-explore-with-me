package ru.practicum.categories.mapper;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;

import java.util.Collection;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDto modelToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Collection<CategoryDto> modelToCategoryDtos(Collection<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::modelToCategoryDto)
                .collect(Collectors.toList());
    }

    public static Category categoryDtoToModel(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static Category newCategoryDtoToModel(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}
