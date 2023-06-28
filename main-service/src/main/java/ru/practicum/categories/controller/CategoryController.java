package ru.practicum.categories.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoriesService;
import ru.practicum.categories.service.CategoriesServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@Validated
@Slf4j
public class CategoryController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@Valid @RequestBody Category newCategory) {
        log.info("Пришёл запрос на создание новой категории. {}", newCategory);
        return categoriesService.createCategory(newCategory);
    }

    @PatchMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public Category patchCategory(@PathVariable Integer catId, @Valid @RequestBody Category updatedCategory) {
        log.info("Пришёл запрос на обновление категории с id {}", catId);
        return categoriesService.patchCategory(catId, updatedCategory);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer catId) {
        log.info("Пришёл запрос на удаление категории с id {}", catId);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Category> getCategories(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришёл запрос на получение категорий. from - {}, size - {}", from, size);
        return categoriesService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public Category getCategoryById(@PathVariable Integer catId) {
        log.info("Пришёл запрос на получение категории с id {}", catId);
        return categoriesService.getCategory(catId);
    }
}
