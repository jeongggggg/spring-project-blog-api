package com.estsoft.springproject.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Comment {
    // comment_id: Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    // article_id: 외래 키로 Article과 연관
    @ManyToOne
    @JoinColumn(name = "article_id") // 외래 키 설정
    private Article article;

    // body: 댓글 내용
    @Column
    private String body;

    // created_at: 댓글 작성 시간
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructor
    public Comment(String body, Article article) {
        this.body = body;
        this.article = article;
    }

    // 댓글 본문 수정 메서드
    public void updateBody(String body) {
        this.body = body;
    }

//    // 연관된 Article 설정 메서드
//    public void setArticle(Article article) {
//        this.article = article;
//    }
}
