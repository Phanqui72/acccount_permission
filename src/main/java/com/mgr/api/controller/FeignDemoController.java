package com.mgr.api.controller;

import com.mgr.api.dto.ApiMessageDto;
import com.mgr.api.dto.external.ExternalPostDto;
import com.mgr.api.external.JSONPlaceHolderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/feign-demo")
public class FeignDemoController extends ABasicController {

    @Autowired
    private JSONPlaceHolderClient jsonPlaceHolderClient;

    @GetMapping("/posts")
    public ApiMessageDto<List<ExternalPostDto>> testGetPosts() {
        List<ExternalPostDto> posts = jsonPlaceHolderClient.getPosts();
        return makeSuccessResponse(posts, "Lấy dữ liệu từ Feign thành công!");
    }

    @GetMapping("/posts/{id}")
    public ApiMessageDto<ExternalPostDto> testGetDetail(@PathVariable Long id) {
        ExternalPostDto post = jsonPlaceHolderClient.getPostById(id);
        return makeSuccessResponse(post, "Lấy chi tiết từ Feign thành công!");
    }
}