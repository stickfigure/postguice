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
	 * @throws IllegalStateException if we are currently in a transaction
	 */
	public static void checkNotInTransaction() {
		Preconditions.checkState(!EM.em().getTransaction().isActive(), "Expected NOT to be in a transaction");
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

	/**
	 * This is like transact(), but enforces the start of a new transaction. Since we can't actually suspend
	 * a transaction and start a new one, we simply detect and throw an exception if we are already in a txn.
	 */
	public static <R> R transactNew(final Callable<R> work) {
		checkNotInTransaction();
		return transact(work);
	}

	/**
	 * This is like transact(), but enforces the start of a new transaction. Since we can't actually suspend
	 * a transaction and start a new one, we simply detect and throw an exception if we are already in a txn.
	 */
	public static void transactNew(final Runnable work) {
		checkNotInTransaction();
		transact(work);
	}
}
