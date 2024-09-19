package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, String>{

	//List<Categories> getAllCategories();

}
