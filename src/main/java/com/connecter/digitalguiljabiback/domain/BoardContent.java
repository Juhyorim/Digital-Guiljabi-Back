package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board_content")
public class BoardContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_pk", referencedColumnName = "pk")
    private Board board;

    //subtitle은 null이 될 수 있음 -> nullable
    private String title;

    @Column(name = "img_url")
    private String imgUrl;

    private String content;

    public static BoardContent makeBoardContent(Board board, String title, String imgUrl, String content) {
        BoardContent boardContent = new BoardContent();
        boardContent.board = board;
        boardContent.title = title;
        boardContent.imgUrl = imgUrl;
        boardContent.content = content;

        return boardContent;
    }

    public void edit(String subTitle, String imgUrl, String content) {
        this.title = Objects.nonNull(subTitle) ? subTitle : this.title;
        this.imgUrl = Objects.nonNull(imgUrl) ? imgUrl : this.imgUrl;
        this.content = Objects.nonNull(content) ? content : this.content;
    }
}
