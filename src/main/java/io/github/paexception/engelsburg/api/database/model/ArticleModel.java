package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.ArticleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ArticleModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Min(0)
	private int articleId; //Article id of WP-API
	@Min(0)
	private long date;
	@NotBlank
	private String link;
	@NotBlank
	private String title;
	@NotBlank
	@Lob
	private String content;
	@NotBlank
	private String contentHash;
	private String mediaUrl;
	private String blurHash;

	public ArticleDTO toResponseDTO() {
		return new ArticleDTO(
				this.articleId,
				this.date,
				this.link,
				this.title,
				this.content,
				this.contentHash,
				this.mediaUrl,
				this.blurHash
		);
	}

	public ArticleModel update(ArticleDTO dto) {
		this.date = dto.getDate();
		this.link = dto.getLink();
		this.title = dto.getTitle();
		this.content = dto.getContent();
		this.contentHash = dto.getContentHash();
		this.mediaUrl = dto.getMediaUrl();
		this.blurHash = dto.getBlurHash();

		return this;
	}
}
