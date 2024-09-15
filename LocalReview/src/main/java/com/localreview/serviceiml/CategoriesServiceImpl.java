package com.localreview.serviceiml;


import com.localreview.entity.Categories;
import com.localreview.repository.CategoriesRepository;
import com.localreview.service.CategoriesService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public Optional<Categories> getCategoryById(String categoryId) {
        return categoriesRepository.findById(categoryId);
    }
}

