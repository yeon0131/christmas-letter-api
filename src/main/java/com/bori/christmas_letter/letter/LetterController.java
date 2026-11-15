package com.bori.christmas_letter.letter;

import com.bori.christmas_letter.tree.ChristmasTreeService;
import com.bori.christmas_letter.tree.TreeDtos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trees/{slug}/letters")
public class LetterController {

    private final ChristmasTreeService treeService;

    public LetterController(ChristmasTreeService treeService) {
        this.treeService = treeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreeDtos.OrnamentResponse addLetter(
            @PathVariable String slug,
            @Valid @RequestBody LetterDtos.CreateLetterRequest request
    ) {
        return treeService.addLetter(slug, request);
    }

    @PostMapping("/{letterId}/guess")
    public LetterDtos.GuessResponse guessSender(
            @PathVariable String slug,
            @PathVariable Long letterId,
            @RequestHeader(value = "X-Tree-Admin-Key", required = false) String adminKey,
            @Valid @RequestBody LetterDtos.GuessRequest request
    ) {
        return treeService.guessSender(slug, letterId, adminKey, request);
    }

    @DeleteMapping("/{letterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLetter(
            @PathVariable String slug,
            @PathVariable Long letterId,
            @RequestHeader(value = "X-Tree-Admin-Key", required = false) String adminKey
    ) {
        treeService.deleteLetter(slug, letterId, adminKey);
    }
}
