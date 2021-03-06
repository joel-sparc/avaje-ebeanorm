package com.avaje.ebeaninternal.server.autofetch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avaje.ebean.config.AutofetchConfig;
import com.avaje.ebean.config.GlobalProperties;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.server.lib.BackgroundThread;
import com.avaje.ebeaninternal.server.querydefn.OrmQueryDetail;

/**
 * Handles the logging aspects for the DefaultAutoFetchListener.
 * <p>
 * Note that java util logging loggers generally should not be serialised and
 * that is one of the main reasons for pulling out the logging to this class.
 * </p>
 */
public class DefaultAutoFetchManagerLogging {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAutoFetchManagerLogging.class);

	private final DefaultAutoFetchManager manager;

	private final boolean traceUsageCollection;

	public DefaultAutoFetchManagerLogging(ServerConfig serverConfig, DefaultAutoFetchManager profileListener) {

		this.manager = profileListener;

		AutofetchConfig autofetchConfig = serverConfig.getAutofetchConfig();

		traceUsageCollection = GlobalProperties.getBoolean("ebean.autofetch.traceUsageCollection", false);
		
		int updateFreqInSecs = autofetchConfig.getProfileUpdateFrequency();
		BackgroundThread.add(updateFreqInSecs, new UpdateProfile());
	}

	private final class UpdateProfile implements Runnable {
		public void run() {
			manager.updateTunedQueryInfo();
		}
	}

  public void logInfo(String msg, Throwable e) {
    logger.info(msg, e);
  }

  public void logError(String msg, Throwable e) {
    logger.error(msg, e);
  }

	public void logSummary(String summaryInfo) {
		
		String msg = "\"Summary\",\""+summaryInfo+"\",,,,";		
		logger.debug(msg);
	}

	public void logChanged(TunedQueryInfo tunedFetch, OrmQueryDetail newQueryDetail) {
		
		String msg = tunedFetch.getLogOutput(newQueryDetail);
		logger.debug(msg);
	}

	public void logNew(TunedQueryInfo tunedFetch) {

		String msg = tunedFetch.getLogOutput(null);
	  logger.debug(msg);
	}

	public boolean isTraceUsageCollection() {
		return traceUsageCollection;
	}
	
}
