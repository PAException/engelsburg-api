package io.github.paexception.engelsburg.api.spring;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Naming strategy of tables.
 */
public class PrefixNamingStrategy extends SpringPhysicalNamingStrategy {

	private static final Pattern REPLACE_PATTERN = Pattern.compile("(Model)");
	private static final Set<String> RESERVED = new HashSet<>(Collections.singletonList("DTYPE"));

	private static Identifier rename(Identifier name) {
		if (name == null) return null;
		if (RESERVED.contains(name.getText())) return name;

		String tableName = removeUnnecessary(name.getText());

		return new Identifier(Character.toLowerCase(tableName.charAt(0)) + tableName.substring(1), name.isQuoted());
	}

	private static String removeUnnecessary(String s) {
		return REPLACE_PATTERN.matcher(s).replaceAll("");
	}

	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return rename(name);
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

}
