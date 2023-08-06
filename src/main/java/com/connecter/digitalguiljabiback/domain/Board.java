package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    //사용자가 탈퇴했을 때 알수없음 처리를 위해 nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", referencedColumnName = "pk")
    private Users user;

    @NotNull
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "board_status")
    private BoardStatus status = BoardStatus.WAITING;

    @NotNull
    private String title;

    @CreationTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt = LocalDateTime.now();

    @Column(name = "thumbnail_url", length = 99999)
    private String thumbnailUrl = null;

    @Lob
    @Column(length = 999999999)
    private String introduction;

    //출처
    @Column(length = 99999999)
    private String sources;

    //수정요청 or 거부 사유
    @Column(length = 99999999)
    private String reason;

    @NotNull
    @Column(name = "report_cnt")
    private int reportCnt = 0; //신고횟수는 5회 이상이 넘어가지 않기 때문에 Long보단 int가 좋을 것 같음

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    private List<BoardCategory> boardCategories = new ArrayList<>();

    @NotNull
    @Column(name = "like_cnt")
    private Long likeCnt = 0L;

    @NotNull
    @Column(name = "bookmark_cnt")
    private Long bookmarkCnt = 0L;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true) //boardcontent를 삭제하지 않아도 삭제됨 @TODO 테스트
    private List<BoardContent> contents = new ArrayList<BoardContent>();


    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardTag> boardTags = new ArrayList<BoardTag>();

    //얘는 너무 길어서 builder 사용
    @Builder
    public Board(Users user, String title, String thumbnailUrl, String introduction, String sources) {
        this.user = user;
        this.title = title;
        this.thumbnailUrl = Objects.nonNull(thumbnailUrl)?thumbnailUrl : this.thumbnailUrl;
        this.introduction = introduction;
        this.sources = sources;
    }

    public void setInfo(List<BoardTag> boardTags, List<BoardContent> contents) {
        this.boardTags = boardTags;
        this.contents = contents;
    }

    public void setBoardCategories(List<BoardCategory> boardCategories) {
        this.boardCategories = boardCategories;
    }

    public void approve() {
        this.status = BoardStatus.APPROVED;
    }

    public void reject(String rejReason) {
        this.reason = rejReason;
        this.status = BoardStatus.REFUSAL;
    }

    public void initReportCnt() { this.reportCnt = 0; }

    public void edit(String title, String thumbnailUrl, String introduction, String sources, List<BoardTag> boardTags, List<BoardContent> contents) {
        this.boardTags.clear();
        this.contents.clear();

        this.title = title;
        this.thumbnailUrl = Objects.nonNull(thumbnailUrl)?thumbnailUrl:this.thumbnailUrl;
        this.introduction = introduction;
        this.sources = sources;
        this.boardTags.addAll(boardTags);
        this.contents.addAll(contents);
    }

    public void addReportCnt() {
        this.reportCnt += 1;
        if(reportCnt >= 5) {
            this.status = BoardStatus.RESTRICTED;
        }
    }

    public void deleteReport() {
        this.reportCnt --;
    }

    public void addLikeCnt() {
        this.likeCnt += 1;
    }

    public void addBookmarkCnt() {
        this.bookmarkCnt += 1;
    }
}
