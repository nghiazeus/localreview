package com.localreview.service;

import java.util.Optional;

import com.localreview.entity.Categories;

public interface CategoriesService {
    Optional<Categories> getCategoryById(String categoryId);
}

