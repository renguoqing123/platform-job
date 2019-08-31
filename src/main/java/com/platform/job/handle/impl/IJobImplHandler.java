package com.platform.job.handle.impl;

import com.platform.job.handle.IJobHandler;

public class IJobImplHandler extends IJobHandler{

	private IJobHandler iJobHandler;
	
	@Override
	public void execute(String param) throws Exception {
		iJobHandler.execute(param);
	}

}
