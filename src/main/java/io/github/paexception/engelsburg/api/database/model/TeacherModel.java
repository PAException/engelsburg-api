/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.TeacherDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class TeacherModel {

	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int teacherId;

	@NotBlank
	private String abbreviation;
	@NotBlank
	private String firstname;
	@NotBlank
	private String surname;
	@NotBlank
	private String gender;
	private boolean mentionedPhD;
	private long job;

	public TeacherDTO toResponseDTO() {
		List<String> jobs = new ArrayList<>();
		Job.getJobs(this.job).forEach(currJob -> jobs.add(currJob.toString()));

		return new TeacherDTO(
				this.abbreviation,
				this.firstname,
				this.surname,
				this.gender,
				this.mentionedPhD,
				jobs
		);
	}

	public enum Job {

		MATHE(pow2(0), "Mathematik"),
		DEUTSCH(pow2(1), "Deutsch"),
		ENGLISCH(pow2(2), "Englisch"),
		LATEIN(pow2(3), "Latein"),
		FRANZOESISCH(pow2(4), "Französisch"),
		SPANISCH(pow2(5), "Spanisch"),
		KUNST(pow2(6), "Kunst"),
		MUSIK(pow2(7), "Musik"),
		POWI(pow2(8), "Politik und Wirtschaft"),
		SPORT(pow2(9), "Sport"),
		GESCHICHTE(pow2(10), "Geschichte"),
		BIOLOGIE(pow2(11), "Biologie"),
		PHYSIK(pow2(12), "Physik"),
		CHEMIE(pow2(13), "Chemie"),
		KATH_RELIGION(pow2(14), "katholische Religion"),
		EV_RELIGION(pow2(15), "evangelische Religion"),
		INFORMATIK(pow2(16), "Informatik"),
		ERDKUNDE(pow2(17), "Erdkunde"),
		PHILOSOPHIE(pow2(18), "Philosophie"),

		TASTENSCHREIBEN(pow2(19), "Tastenschreiben"),
		SCHULSOZIALARBEIT(pow2(20), "Schulsozialarbeit"),
		SOFTWARESCHULUNG(pow2(21), "Softwareschulung"),
		RELIGIONSPAEDAGOGISCHES_PRAKTIKUM_ALS_GEMEINDEASSISTENTIN(pow2(22),
				"Religionspädagogisches Praktikum als Gemeindeassistentin"),
		RUSSISCH(pow2(22), "Russisch"),
		OFFICE(pow2(23), "Office");

		int value;
		String name;

		Job(int value, String name) {
			this.value = value;
			this.name = name;
		}

		public static List<Job> getJobs(long job) {
			List<Job> jobs = new ArrayList<>();

			for (int i = Job.values().length - 1; i >= 0; i--) {
				Job currJob = Job.values()[i];
				if (job > currJob.value) {
					job -= currJob.value;
					jobs.add(currJob);
				} else if (job == currJob.value) {
					jobs.add(currJob);
					break;
				}
			}

			return jobs;
		}

		public static long getJobId(Job... jobs) {
			long jobId = 0;
			for (Job job : jobs) jobId += job.value;

			return jobId;
		}

		private static int pow2(int n) {
			return (int) Math.pow(2, n);
		}

		@Override
		public String toString() {
			return this.name;
		}

	}
}
