package com.cloudin.commons.langs.support.velocity;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.view.ViewToolContext;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class VelocityLayoutToolbox2View extends VelocityLayoutView {
	
	private static ToolboxFactory toolboxFactory  = null;
	
	@Override
	protected Context createVelocityContext(Map<String, Object> model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Create a ChainedContext instance.
		ViewToolContext vtc;
		
		vtc = new ViewToolContext(getVelocityEngine(), request, response, getServletContext());
		
		vtc.putAll(model);
		
		if (toolboxFactory == null) {
			ToolManager toolManager = new ToolManager();
			toolManager.setVelocityEngine(getVelocityEngine());
			toolManager.configure(getServletContext().getRealPath(getToolboxConfigLocation()));
			toolboxFactory = toolManager.getToolboxFactory();
		}
		
		if (this.getToolboxConfigLocation() != null) {
			
			if (toolboxFactory.hasTools(Scope.REQUEST)) {
				vtc.addToolbox(toolboxFactory.createToolbox(Scope.REQUEST));
			}
			if (toolboxFactory.hasTools(Scope.APPLICATION)) {
				vtc.addToolbox(toolboxFactory.createToolbox(Scope.APPLICATION));
			}
			if (toolboxFactory.hasTools(Scope.SESSION)) {
				vtc.addToolbox(toolboxFactory.createToolbox(Scope.SESSION));
			}
		}
		return vtc;
	}
	
	
	
}
