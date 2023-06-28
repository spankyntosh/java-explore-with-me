package ru.practicum.categories.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.Collection;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    // Администраторская часть
    @Override
    public Category createCategory(Category newCategory) {
        return categoriesRepository.save(newCategory);
    }

    @Override
    public Category patchCategory(Integer categoryId, Category updatedCategory) {
        Category categoryInDB = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));
        categoryInDB.setName(updatedCategory.getName());
        return categoriesRepository.save(categoryInDB);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));
        categoriesRepository.deleteById(categoryId);
    }

    // Публичная часть
    @Override
    public Collection<Category> getCategories(Integer from, Integer size) {
        PageRequest request = PageRequest.of(from / size, size);
        return categoriesRepository.findAll(request).toList();
    }

    @Override
    public Category getCategory(Integer categoryId) {
        return categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));
    }
}
