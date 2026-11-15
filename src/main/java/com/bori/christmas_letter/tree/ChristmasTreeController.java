package com.bori.christmas_letter.tree;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trees")
public class ChristmasTreeController {

    private final ChristmasTreeService treeService;

    public ChristmasTreeController(ChristmasTreeService treeService) {
        this.treeService = treeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreeDtos.CreateTreeResponse createTree(
            @Valid @RequestBody TreeDtos.CreateTreeRequest request
    ) {
        return treeService.createTree(request);
    }

    @GetMapping("/{slug}")
    public TreeDtos.PublicTreeResponse getTree(@PathVariable String slug) {
        return treeService.getPublicTree(slug);
    }

    @GetMapping("/{slug}/owner")
    public TreeDtos.OwnerTreeResponse getOwnerTree(
            @PathVariable String slug,
            @RequestHeader(value = "X-Tree-Admin-Key", required = false) String adminKey
    ) {
        return treeService.getOwnerTree(slug, adminKey);
    }
}
