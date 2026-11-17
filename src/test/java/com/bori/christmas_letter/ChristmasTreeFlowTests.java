package com.bori.christmas_letter;

import com.bori.christmas_letter.common.ApiException;
import com.bori.christmas_letter.letter.LetterDtos;
import com.bori.christmas_letter.letter.OrnamentType;
import com.bori.christmas_letter.tree.ChristmasTreeService;
import com.bori.christmas_letter.tree.TreeDtos;
import com.bori.christmas_letter.tree.TreeTheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ChristmasTreeFlowTests {

    @Autowired
    private ChristmasTreeService treeService;

    @Test
    void treeOwnerCanCreateTreeAndFriendCanHangLockedLetter() {
        TreeDtos.CreateTreeResponse created = treeService.createTree(
                new TreeDtos.CreateTreeRequest("션팡", TreeTheme.CLASSIC)
        );

        treeService.addLetter(
                created.slug(),
                new LetterDtos.CreateLetterRequest(
                        "루돌프",
                        "올해도 같이 개발 공부해서 즐거웠어!",
                        OrnamentType.RED_BAUBLE
                )
        );

        TreeDtos.PublicTreeResponse publicTree = treeService.getPublicTree(created.slug());
        TreeDtos.OwnerTreeResponse ownerTree = treeService.getOwnerTree(created.slug(), created.adminKey());

        assertThat(publicTree.letterCount()).isEqualTo(1);
        assertThat(publicTree.ornaments()).hasSize(1);
        assertThat(publicTree.locked()).isTrue();
        assertThat(ownerTree.letters()).isEmpty();
        assertThat(ownerTree.letterCount()).isEqualTo(1);
    }

    @Test
    void wrongAdminKeyCannotOpenOwnerTree() {
        TreeDtos.CreateTreeResponse created = treeService.createTree(
                new TreeDtos.CreateTreeRequest("션팡", TreeTheme.SNOWY)
        );

        assertThatThrownBy(() -> treeService.getOwnerTree(created.slug(), "wrong-key"))
                .isInstanceOf(ApiException.class)
                .hasMessage("트리 관리 키가 올바르지 않습니다.");
    }
}
