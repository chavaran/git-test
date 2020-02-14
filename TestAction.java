package com.uob.fm.web.action.vault;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class TestAction extends BaseDispatchAction {
	private static final Logger logger = Logger.getLogger(VaultQueueAction.class);
	
		public ActionForward viewOutBoundQueues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HashMap params = new HashMap();
		params.put(AppConstants.SHIPMENT_TYPE, "Outbound");

		try {
			java.util.Date appDate = getAppDate(request);
			retrieveAllStaticDataForShipmentSearch(request);
			VaultBusLogicDAOIfc vaultBusLogicDAOIfc = (VaultBusLogicDAOIfc) getWebApplicationContext().getBean("VaultBusLogicDAO");
			List<WorkflowStateTransitions> wft = vaultBusLogicDAOIfc.findVaultWorkflowstateTransitions(AppConstants.Outbound);
			List result = vaultBusLogicDAOIfc.findShipmentRecordsGeneral(params);

			request.setAttribute("VaultStatusJSON", constructVaultListJson(wft));
			List commisionResult = vaultBusLogicDAOIfc.findCommisionShipmentRecordsFromDeals(AppConstants.Outbound);
			request.setAttribute("ShipmentJASON", constructShipmentLegsInfoJsonSorting(result, commisionResult, appDate, AppConstants.Outbound));
			request.setAttribute("shipmentType", AppConstants.Outbound);
		} catch (BlissException be) {
			logger.error(ExceptionUtils.getStackTrace(be));
			if (be.getMessage() != null) {
				logger.error(be.getMessage());
				ActionMessages actionMessages = new ActionMessages();
				actionMessages.add(Globals.ERROR_KEY, new ActionMessage(be.getErrorKey()));
				saveErrors(request, actionMessages);
			}

		} catch (RuntimeException re) {
			logger.error(ExceptionUtils.getStackTrace(re));
			ActionMessages actionMessages = new ActionMessages();
			actionMessages.add(Globals.ERROR_KEY, new ActionMessage("VERR001"));
			saveErrors(request, actionMessages);
		}

		return mapping.findForward("viewOutBoundVaultQueue");

	}
	
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
