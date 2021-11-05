package io.github.paexception.engelsburg.api.test.unit;

import io.github.paexception.engelsburg.api.controller.shared.SolarSystemController;
import io.github.paexception.engelsburg.api.endpoint.dto.SolarSystemDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class SolarSystemTest {

	private SolarSystemController solarSystemController;

	@BeforeEach
	public void init() {
		this.solarSystemController = new SolarSystemController();
	}

	@Test
	public void noData() {
		Result<SolarSystemDTO> dto = this.solarSystemController.info();

		assertThat(dto.isErrorPresent()).isTrue();
		assertThat(dto.getError()).isEqualTo(Error.NOT_FOUND);
		assertThat(dto.getExtra()).isEqualTo("solar_system");
	}

	@Test
	public void success() {
		this.solarSystemController.update("test_date", "test_energy", "test_co2", "test_payment");
		this.solarSystemController.updateText("test_text");

		Result<SolarSystemDTO> dto = this.solarSystemController.info();

		assertThat(dto.isResultPresent()).isTrue();
		assertThat(dto.getResult().getDate()).isEqualTo("test_date");
		assertThat(dto.getResult().getEnergy()).isEqualTo("test_energy");
		assertThat(dto.getResult().getCo2avoidance()).isEqualTo("test_co2");
		assertThat(dto.getResult().getPayment()).isEqualTo("test_payment");
		assertThat(dto.getResult().getText()).isEqualTo("test_text");
	}

}
