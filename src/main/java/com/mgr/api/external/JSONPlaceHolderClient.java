package com.mgr.api.external;

import com.mgr.api.dto.external.ExternalPostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "placeholder-client", url = "https://jsonplaceholder.typicode.com")
public interface JSONPlaceHolderClient {

    @GetMapping("/posts")
    List<ExternalPostDto> getPosts();

    @GetMapping("/posts/{id}")
    ExternalPostDto getPostById(@PathVariable("id") Long id);
}