package com.uob.fm.web.action.vault;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class TestAction extends BaseDispatchAction {
	private static final Logger logger = Logger.getLogger(VaultQueueAction.class);

	public String execute(DealsForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String accessCheckViolated = (String) request.getAttribute(AppConstants.ACCESS_VIOLATED);
		PrintWriter out = response.getWriter();
		if (accessCheckViolated != null && accessCheckViolated.equalsIgnoreCase(AppConstants.YES)) {
			// For Ajax Calls
			if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
				StringBuffer strBuffer = new StringBuffer("{errorMsg: ");
				strBuffer.append("'");
				strBuffer.append("NO ACCESS TO THE OPERATION");
				strBuffer.append("'");
				strBuffer.append("}");
				out.print(strBuffer.toString());
				return null;
			} else {
				throw new Exception("static.userfunction.accessviolation");
			}
		}
		return super.execute(request, response);
	}

}
