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
	private int articleId;
	@Min(0)
	private long date;
	@NotBlank
	private String link;
	@NotBlank
	private String title;
	@NotBlank
	@Lob
	private String content;
	private String mediaUrl;

	public ArticleDTO toResponseDTO() {
		return new ArticleDTO(this.date, this.link, this.title, this.content, this.mediaUrl);
	}

}
