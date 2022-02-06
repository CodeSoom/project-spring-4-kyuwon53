package com.kyuwon.booklog.dto.posts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("게시물 요청 데이터는")
class PostsSaveRequestDataTest {

    @Test
    public void it_return_post() {
        String title = "title";
        String content = "content";
        String author = "author";

        PostsSaveRequestData requestData = new PostsSaveRequestData(title, content, author);

        assertThat(requestData.getTitle()).isEqualTo(title);
        assertThat(requestData.getContent()).isEqualTo(content);
        assertThat(requestData.getAuthor()).isEqualTo(author);
    }
}
