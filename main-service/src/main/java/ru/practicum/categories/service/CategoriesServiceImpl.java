package ru.practicum.categories.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ForbiddenException;

import java.util.Collection;

import static ru.practicum.categories.mapper.CategoryMapper.*;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository,
                                 EventRepository eventRepository) {
        this.categoriesRepository = categoriesRepository;
        this.eventRepository = eventRepository;
    }

    // Admin: Категории
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategory) {
        return modelToCategoryDto(categoriesRepository.save(newCategoryDtoToModel(newCategory)));
    }

    @Override
    public CategoryDto patchCategory(Integer categoryId, CategoryDto updatedCategory) {
        Category categoryInDB = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));
        categoryInDB.setName(updatedCategory.getName());
        return modelToCategoryDto(categoriesRepository.save(categoryDtoToModel(updatedCategory)));
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));
        if (eventRepository.findByCategory(categoryId).size() > 0) {
            throw new ForbiddenException("Невозможно удалить категорию с привязанным событием");
        }
        categoriesRepository.deleteById(categoryId);
    }

    // Public: Категории
    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest request = PageRequest.of(from / size, size);
        return modelToCategoryDtos(categoriesRepository.findAll(request).toList());
    }

    @Override
    public CategoryDto getCategory(Integer categoryId) {

        return modelToCategoryDto(categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId))));
    }
}
