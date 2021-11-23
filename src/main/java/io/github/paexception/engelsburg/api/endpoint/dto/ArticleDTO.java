package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

	private int articleId;
	private long date;
	private String link;
	private String title;
	private String content;
	private String contentHash;
	private String mediaUrl;
	private String blurHash;

}
