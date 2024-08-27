package com.localreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.localreview.entity.Store;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    private final StoreService storesv;

    @Autowired
    public IndexController(StoreService storesv) {
        this.storesv = storesv;
    }

    @GetMapping("/index")
    public String store(Model model) {
        List<Store> list = storesv.getAllStores();
        model.addAttribute("stores", list); 
        return "index";
    }
    
    @GetMapping("/storedetail")
    public String Storedetail() {
        return "detailstore";
    }
    
}

