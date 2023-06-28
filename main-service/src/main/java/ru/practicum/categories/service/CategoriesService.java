package ru.practicum.categories.service;

import ru.practicum.categories.model.Category;

import java.util.Collection;

public interface CategoriesService {

    // Администраторская часть
    Category createCategory(Category newCategory);

    Category patchCategory(Integer categoryId, Category updatedCategory);

    void deleteCategory(Integer categoryId);

    // Публичная часть
    Collection<Category> getCategories(Integer from, Integer size);

    Category getCategory(Integer categoryId);
}
