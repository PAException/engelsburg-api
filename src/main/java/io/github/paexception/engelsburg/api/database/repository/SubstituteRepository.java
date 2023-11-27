/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.repository;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface SubstituteRepository extends JpaRepository<SubstituteModel, Integer> {

	/**
	 * Converts the class to a like parameter.
	 * 9c  --> 9%c%
	 * 10c --> 10%c%
	 * should not be used vor E1 - Q4
	 *
	 * @param className to convert
	 * @return parsed parameter
	 */
	static String likeClassName(String className) {
		if (className.length() == 2) return className.charAt(0) + "%" + className.charAt(1) + "%";
		else return className.substring(0, 2) + "%" + className.charAt(2) + "%";
	}

	List<SubstituteModel> findAllByDate(Date date);

	List<SubstituteModel> findAllByDateGreaterThanEqual(Date date);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndClassNameIsNull(Date date);

	default List<SubstituteModel> findAllByDateGreaterThanEqualAndClassNameVariations(Date date, String className) {
		return this.findByDateGreaterThanEqualAndClassNameIsLike(date, likeClassName(className));
	}

	List<SubstituteModel> findByDateGreaterThanEqualAndClassNameIsLike(Date date, String className);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndClassNameIn(Date date, List<String> classes);

	List<SubstituteModel> findAllByDateGreaterThanEqualAndTeacherInOrDateGreaterThanEqualAndSubstituteTeacherIn(
			Date date, List<String> teacher, Date date2, List<String> substituteTeacher);

	void deleteAllByDate(Date date);
}
