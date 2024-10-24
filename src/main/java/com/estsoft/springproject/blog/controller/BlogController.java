package com.estsoft.springproject.blog.controller;


import com.estsoft.springproject.blog.domain.dto.AddArticleRequest;
import com.estsoft.springproject.blog.domain.Article;
import com.estsoft.springproject.blog.domain.dto.ArticleResponse;
import com.estsoft.springproject.blog.domain.dto.ArticleWithCommentsResponseDTO;
import com.estsoft.springproject.blog.domain.dto.UpdateArticleRequest;
import com.estsoft.springproject.blog.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // 로깅 어노테이션
@RestController // REST API를 만들기 위한 RESTController 선언
@RequestMapping("/api")
@Tag(name = "블로그 저장/수정/삭제/조회용 API", description = "API 설명을 이곳에 작성하면 됩니다!")
public class BlogController {
    private final BlogService service;

    public BlogController(BlogService service) {
        this.service = service;
    }
    // RequestMapping (특정 url   POST/articles) 글을 작성해서  db에 저장하고 싶기 때문에 post
    // @RequestMapping(method = RequestMethod.POST) => PostMapping 과 같음
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공했습니다.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "블로그 전체 목록 보기", description = "블로그 메인 화면에서 보여주는 전체 목록")
    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> writeArticle(@RequestBody AddArticleRequest request){
        Article article = service.saveArticle(request);
        // System.out.println(request.getTitle());
        // System.out.println(request.getContent());
        // return ResponseEntity.status(HttpStatus.OK).build(); // 2xx

        // logging
        // 로깅 레벨은 trace, debug, info, warn, error
        log.debug("{}, {}",request.getTitle(), request.getContent());

        // CREATED : 성공 201, 요청으로 인해서 리소스가 생성이 되었다는 의미
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(article.convert());

        // RequestBody로 받으면 객체로 json안에 들어있는 각각의 속성들이 그 값에 맞게 들어간다. title, content로 정의 되어 있는 것들을 특정 값으로 파싱해주는 역할을 하는 어노테이션
    }

    // Request Mapping 조회 : HTTP METHOD ? GET
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공했습니다.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "Request Mapping 조회", description = "Request Mapping 조회")
    @Parameter(name = "id", description = "블로그 글 ID", example = "45")
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponse>> fineAll() {
        // List<Article> articleList = service.findAll();
        // List<ArticleResponse> 형태로 변환해서 응답으로 보내기
        List<ArticleResponse> list = service.findAll().stream()
                // .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
                // .map(article -> article.convert())
                .map(Article::convert)
                .toList();
        return ResponseEntity.ok(list);
    }

    // 단건 조회 API (Request mapping)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공했습니다.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "블로그 단건 조회", description = "블로그 메인 화면에서 보여주는 단건 목록")
    @Parameter(name = "id", description = "블로그 글 ID", example = "45")
    @GetMapping("/articles/{id}")
//    public ResponseEntity<Article> findById(@RequestParam Long id) {
//        Article article = service.findById(id);
//        return ResponseEntity.ok(article);
//    }
    public ResponseEntity<ArticleResponse> findById(@PathVariable Long id) {
        Article article = service.findById(id); // Exception(5xx server error) => 4xx Status Code
        // Article => ArticleResponse
        return ResponseEntity.ok(article.convert());
    }

    // DELETE / articles/{id}
    //@RequestMapping(method = RequestMethod.DELETE, value = "/articles/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공했습니다.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "블로그 글 삭제", description = "블로그 글 삭제")
    @Parameter(name = "id", description = "블로그 글 ID", example = "45")
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteBy(id);
        return ResponseEntity.ok().build();
    }

    // PUT /articles/{id} 수정 API body
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공했습니다.", content = @Content(mediaType = "application/json"))
    })
    @Operation(summary = "블로그 글 수정", description = "블로그 글 수정")
    @Parameter(name = "id", description = "블로그 글 ID", example = "45")
    @PutMapping("/articles/{id}")
    public ResponseEntity<ArticleResponse> updateById(@PathVariable Long id,
                                                      @RequestBody UpdateArticleRequest request) {
        Article updateArticle = service.update(id, request);
        return ResponseEntity.ok(updateArticle.convert());
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    private ResponseEntity<String> HandlerIllegalArgumentException(IllegalArgumentException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // reason : ""
//    }

    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<ArticleWithCommentsResponseDTO> getArticleWithComments(@PathVariable Long articleId) {
        ArticleWithCommentsResponseDTO response = service.findArticleWithComments(articleId);
        return ResponseEntity.ok(response);
    }
}
