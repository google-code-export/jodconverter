//
// JODConverter - Java OpenDocument Converter
// Copyright 2011 Art of Solving Ltd
// Copyright 2004-2011 Mirko Nasato
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package org.artofsolving.jodconverter.office;

import java.io.File;

import org.artofsolving.jodconverter.process.ProcessManager;
import org.artofsolving.jodconverter.process.SigarProcessManager;

class ManagedOfficeProcessSettings {

    public static final long DEFAULT_RETRY_TIMEOUT = 120000L;
    public static final long DEFAULT_RETRY_INTERVAL = 250L;

    private final UnoUrl unoUrl;
    private File officeHome = OfficeUtils.getDefaultOfficeHome();
    private String[] runAsArgs;
    private File templateProfileDir;
    private File instanceProfileDir;
    private ProcessManager processManager = new SigarProcessManager();
    private long retryTimeout = DEFAULT_RETRY_TIMEOUT;
    private long retryInterval = DEFAULT_RETRY_INTERVAL;
    private boolean autokillOpenPipes = false;

    public ManagedOfficeProcessSettings(UnoUrl unoUrl) {
        this.unoUrl = unoUrl;
    }

    public UnoUrl getUnoUrl() {
        return unoUrl;
    }

    public File getOfficeHome() {
        return officeHome;
    }

    public void setOfficeHome(File officeHome) {
        this.officeHome = officeHome;
    }

    public String[] getRunAsArgs() {
		return runAsArgs;
	}

    public void setRunAsArgs(String[] runAsArgs) {
		this.runAsArgs = runAsArgs;
	}

    public File getTemplateProfileDir() {
        return templateProfileDir;
    }

    public void setTemplateProfileDir(File templateProfileDir) {
        this.templateProfileDir = templateProfileDir;
    }

    public File getInstanceProfileDir() {
		return instanceProfileDir;
	}

	public void setInstanceProfileDir(File instanceProfileDir) {
		this.instanceProfileDir = instanceProfileDir;
	}

	public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public long getRetryTimeout() {
        return retryTimeout;
    }

    public void setRetryTimeout(long retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }
    
    public boolean isAutokillOpenPipes() {
    	return autokillOpenPipes;
    }
    	 
    public void setAutokillOpenPipes(boolean autokillOpenPipes) {
    	this.autokillOpenPipes = autokillOpenPipes;
    }

}
