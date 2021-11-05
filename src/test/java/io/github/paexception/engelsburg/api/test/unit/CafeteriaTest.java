package io.github.paexception.engelsburg.api.test.unit;

import io.github.paexception.engelsburg.api.controller.shared.CafeteriaController;
import io.github.paexception.engelsburg.api.endpoint.dto.CafeteriaInformationDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class CafeteriaTest {

	private static final CafeteriaInformationDTO TEST_DTO = new CafeteriaInformationDTO("test_content", "test_link",
			"test_mediaURL", "test_blurHash");
	private CafeteriaController cafeteriaController;

	@BeforeEach
	public void init() {
		this.cafeteriaController = new CafeteriaController();
	}

	@Test
	public void noData() {
		Result<CafeteriaInformationDTO> dto = this.cafeteriaController.getInfo();

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.NOT_FOUND);
		assertThat(dto.getExtra()).isEqualTo("cafeteria");
	}

	@Test
	public void success() {
		this.cafeteriaController.update(TEST_DTO);

		Result<CafeteriaInformationDTO> dto = this.cafeteriaController.getInfo();

		assertThat(dto.isResultPresent()).isTrue();
		assertThat(dto.getResult().getContent()).isEqualTo("test_content");
		assertThat(dto.getResult().getLink()).isEqualTo("test_link");
		assertThat(dto.getResult().getMediaURL()).isEqualTo("test_mediaURL");
		assertThat(dto.getResult().getBlurHash()).isEqualTo("test_blurHash");
	}
}
