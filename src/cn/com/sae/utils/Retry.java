package cn.com.sae.utils;

/**
 * リトライを行なうための機能セット。
 */
public class Retry {

	/**
	 * リトライする。
	 * 
	 * <ul>
	 * <li>{@link Logic}を実行し、発生した例外または戻り値がリトライ対象である場合、リトライする。</li>
	 * <li>リトライ対象かどうかは、{@link Condition}で評価する。</li>
	 * <li>
	 * 指定回数リトライしても、{@link Logic}がリトライするべき結果を返す場合、
	 * 最後の実行結果を返す。または、最後に発生した例外をスローする。</li>
	 * <li>
	 * {@link Logic}の戻り値、または発生した例外がリトライ対象でない場合、 戻り値をそのまま返し(または例外をスローし)、即座に復帰する。</li>
	 * </ul>
	 * 
	 * @param <R>
	 *            戻り値型
	 * @param <E>
	 *            発生する例外の基底クラス
	 * 
	 * @param retryCount
	 *            リトライ回数
	 * @param condition
	 *            リトライ条件
	 * @param logic
	 *            処理
	 * @return 最終的な処理の実行結果
	 * @throws E
	 *             リトライしても発生した最後の例外。
	 */
	public static final <R, E extends Exception> R retry(int retryCount,
			Condition<R, E> condition, Logic<R, E> logic) throws E {
		// 最後に実行した処理と発生した例外。
		// リトライ回数が指定回数を超えた場合、この値を返す。
		R lastresult = null;
		E lastException = null;
		for (int i = 0; i <= retryCount; i++) {
			try {
				lastException = null;
				lastresult = null;
				lastresult = logic.call(i);
				if (!condition.isRetry(lastresult)) {
					return lastresult;
				}
			} catch (ContinueException e) {
				// リトライ
			} catch (BreakException e) {
				// リトライを即刻中止
				try {
					return (R) e.get();
				} catch (Throwable t) {
					if (t instanceof RuntimeException) {
						throw (RuntimeException) t;
					}
					if (t instanceof Error) {
						throw (Error) t;
					}
					throw (E) t;
				}
			} catch (Exception ex) { // catch で型パラメータは使えないので仕方なく。
				lastException = (E) ex;
				if (!condition.isRetry((E) ex)) {
					throw (E) ex;
				}
			}
		}
		// リトライ回数が指定値を超えた場合、最後の実行結果を返す。
		if (lastException != null) {
			throw lastException;
		}
		return lastresult;
	}

	/**
	 * リトライする条件
	 * 
	 * @param <R>
	 *            戻り値型
	 * @param <E>
	 *            発生する例外の基底クラス
	 */
	public static interface Condition<R, E extends Exception> {
		/**
		 * 正常終了した結果をもとに、リトライするか評価する。
		 * 
		 * @param result
		 *            実行結果
		 * @return リトライする場合true
		 */
		boolean isRetry(R result);

		/**
		 * 発生した例外をもとに、リトライするか評価する。
		 * 
		 * @param error
		 *            発生した例外
		 * @return リトライする場合true
		 */
		boolean isRetry(E error);
	}

	/**
	 * リトライする処理
	 * 
	 * @param <R>
	 *            戻り値型
	 * @param <E>
	 *            発生する例外の基底クラス
	 */
	public static interface Logic<R, E extends Exception> {
		/**
		 * 処理を実行
		 * 
		 * @param count
		 *            リトライした回数
		 * @return 実行結果
		 * @throws E
		 *             発生する例外
		 */
		R call(int count) throws E;

	}

	/**
	 * リトライする処理の抽象基底クラス
	 * 
	 * @param <R>
	 *            戻り値型
	 * @param <E>
	 *            発生する例外の基底クラス
	 */
	public static abstract class AbstractLogic<R, E extends Exception> implements
			Logic<R, E> {
		/**
		 * 処理を中断する。
		 * 
		 * @param result
		 *            処理の戻り値
		 */
		void break_(R... result) {
			throw new BreakException(
					result != null && result.length > 0 ? result[0] : null);
		}

		/**
		 * 処理を中断する。
		 * 
		 * @param error
		 *            処理で発生したエラー
		 */
		void break_(E error) {
			throw new BreakException(error);
		}

		/**
		 * 以降の処理を中断し、次のループへ。
		 */
		void continue_() {
			throw new ContinueException();
		}
	}

	/**
	 * 処理の中断を示す例外 例外派生クラスはパラメータ化できない
	 */
	private static final class BreakException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -768388250622943173L;
		final Object returnValue;
		final Throwable t;

		BreakException(Object returnValue) {
			super();
			this.returnValue = returnValue;
			this.t = null;
		}

		BreakException(Throwable t) {
			super(t);
			this.returnValue = null;
			this.t = t;
		}

		/**
		 * 結果を取得する。
		 * 
		 * @return 結果
		 * @throws Throwable
		 *             処理が例外により中断された場合。
		 */
		Object get() throws Throwable {
			if (t != null)
				throw t;
			return returnValue;
		}
	};

	/**
	 * 処理の継続を示す例外
	 */
	private static final class ContinueException extends RuntimeException {
	};
}