package io.github.paexception.engelsburg.api.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ArticleSaveModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int articleSaveId;

	@NotNull
	@Column(length = 16)
	private UUID userId;
	private int articleId;

}
