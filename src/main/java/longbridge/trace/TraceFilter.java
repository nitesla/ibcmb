package longbridge.trace;

import longbridge.config.IbankingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class TraceFilter implements Filter {


	@SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceFilter.class);



	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// NOOP
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		ApplicationContext context = IbankingContext.getApplicationContext();
		TraceStore traceStore = context.getBean(TraceStore.class);
		try {
			traceStore.init();
			chain.doFilter(servletRequest, servletResponse);
		} finally {
		    traceStore.clear();
		}
	}

	@Override
	public void destroy() {
		// NOOP
	}

}