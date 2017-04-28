package com.voodoodyne.postguice;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.persist.jpa.WizardBridgeModule;
import com.google.inject.util.Modules;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * <p>This module provides a little bit of glue between guice-persist and the rest of gwizard.
 * The main benefit is the ability to configure JPA through a guice-friendly config mechanism.
 * Just provide a DatabaseConfig object.</p>
 *
 * <p>Unfortunately there is no way around requiring your app to have a META-INF/persistence.xml
 * file in the same jar that has your entity classes. However, this can be a minimal skeleton
 * file which contains only the &lt;provider&gt; definition of org.hibernate.jpa.HibernatePersistenceProvider.
 * See the gwizard-example application for a demonstration.</p>
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=false, of={})	// makes installation of this module idempotent
public class PostguiceModule extends AbstractModule {

	private final String persistenceUnitName;

	/** Assume a default persistence unit name of "persistence-unit" */
	public PostguiceModule() {
		this("persistence-unit");
	}

	@Override
	protected void configure() {
		install(Modules.override(new JpaPersistModule(persistenceUnitName)).with(new WizardBridgeModule()));

		requestStaticInjection(EM.class);
		requestStaticInjection(Transactions.class);
	}
}
