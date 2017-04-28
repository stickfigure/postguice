package com.voodoodyne.postguice;

import com.google.common.base.Preconditions;
import com.google.inject.persist.Transactional;
import lombok.SneakyThrows;

import javax.inject.Inject;
import java.util.concurrent.Callable;

/**
 * <p>
 *     Utilities for working with transactions. Requires static injection.
 * </p>
 */
public class Transactions {

	static class TransactionAdaptor {
		@Transactional
		@SneakyThrows
		public <R> R transact(final Callable<R> work) {
			return work.call();
		}
	}

	@Inject
	private static TransactionAdaptor adaptor;

	/**
	 * @throws IllegalStateException if we are not currently in a transaction
	 */
	public static void checkInTransaction() {
		Preconditions.checkState(EM.em().getTransaction().isActive(), "Expected to be in a transaction");
	}

	/**
	 * Execute the work in a transaction. This is functionally identical to putting the work in a method
	 * and annotating it with @Transactional.
	 */
	public static <R> R transact(final Callable<R> work) {
		return adaptor.transact(work);
	}

	/**
	 * Execute the work in a transaction. This is functionally identical to putting the work in a method
	 * and annotating it with @Transactional.
	 */
	public static void transact(final Runnable work) {
		transact(() -> {
			work.run();
			return null;
		});
	}
}
