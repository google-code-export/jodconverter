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

import org.artofsolving.jodconverter.process.SigarProcessManager;

import com.google.common.base.Preconditions;

public class DefaultOfficeManagerConfiguration {

    private File officeHome = OfficeUtils.getDefaultOfficeHome();
    private OfficeConnectionProtocol connectionProtocol = OfficeConnectionProtocol.SOCKET;
    private int[] portNumbers = new int[] { 2002 };
    private String[] pipeNames = new String[] { "office" };
    private String[] runAsArgs = null;
    private File templateProfileDir = null;
    private File instanceProfileDir = null;
    private long taskQueueTimeout = 30000L;  // 30 seconds
    private long taskExecutionTimeout = 120000L;  // 2 minutes
    private int maxTasksPerProcess = 200;
    private boolean autokillOpenPipes = false;
    private long retryTimeout = ManagedOfficeProcessSettings.DEFAULT_RETRY_TIMEOUT;
    
    public DefaultOfficeManagerConfiguration setOfficeHome(String officeHome) throws NullPointerException, IllegalArgumentException {
        Preconditions.checkNotNull("officeHome", officeHome);
        return setOfficeHome(new File(officeHome));
    }

    public DefaultOfficeManagerConfiguration setOfficeHome(File officeHome) throws NullPointerException, IllegalArgumentException  {
        Preconditions.checkNotNull("officeHome", officeHome);
        Preconditions.checkArgument(officeHome.isDirectory(), "officeHome must exist and be a directory");
        this.officeHome = officeHome;
        return this;
    }

    public DefaultOfficeManagerConfiguration setConnectionProtocol(OfficeConnectionProtocol connectionProtocol) throws NullPointerException {
        Preconditions.checkNotNull("connectionProtocol", connectionProtocol);
        this.connectionProtocol = connectionProtocol;
        return this;
    }

    public DefaultOfficeManagerConfiguration setPortNumber(int portNumber) {
        this.portNumbers = new int[] { portNumber };
        return this;
    }

    public DefaultOfficeManagerConfiguration setPortNumbers(int... portNumbers) throws NullPointerException, IllegalArgumentException {
        Preconditions.checkNotNull("portNumbers", portNumbers);
        Preconditions.checkArgument(portNumbers.length > 0, "portNumbers must not be empty");
        this.portNumbers = portNumbers;
        return this;
    }

    public DefaultOfficeManagerConfiguration setPipeName(String pipeName) throws NullPointerException {
        Preconditions.checkNotNull("pipeName", pipeName);
        this.pipeNames = new String[] { pipeName };
        return this;
    }

    public DefaultOfficeManagerConfiguration setPipeNames(String... pipeNames) throws NullPointerException, IllegalArgumentException {
        Preconditions.checkNotNull("pipeNames", pipeNames);
        Preconditions.checkArgument(pipeNames.length > 0, "pipeNames must not be empty");
        this.pipeNames = pipeNames;
        return this;
    }

    public DefaultOfficeManagerConfiguration setRunAsArgs(String... runAsArgs) {
		this.runAsArgs = runAsArgs;
		return this;
	}

    public DefaultOfficeManagerConfiguration setTemplateProfileDir(File templateProfileDir) throws IllegalArgumentException {
        if (templateProfileDir != null) {
        	Preconditions.checkArgument(templateProfileDir.isDirectory(), "templateProfileDir must exist and be a directory");
        }
        this.templateProfileDir = templateProfileDir;
        return this;
    }
    
    /**
     * Default, this uses the systems default tmp directory.
     * Use this method to change the directory
     * @param instanceProfileDir - Directory to use for instance profile
     * @throws IllegalArgumentException - If it is not a directory
     */
    public DefaultOfficeManagerConfiguration setInstanceProfileDir(File instanceProfileDir) throws IllegalArgumentException {
        if (instanceProfileDir != null) {
        	Preconditions.checkArgument(instanceProfileDir.isDirectory(), "instanceProfileDir must exist and be a directory");
        }
        this.instanceProfileDir = instanceProfileDir;
        return this;
    }

    public DefaultOfficeManagerConfiguration setTaskQueueTimeout(long taskQueueTimeout) {
        this.taskQueueTimeout = taskQueueTimeout;
        return this;
    }

    public DefaultOfficeManagerConfiguration setTaskExecutionTimeout(long taskExecutionTimeout) {
        this.taskExecutionTimeout = taskExecutionTimeout;
        return this;
    }

    public DefaultOfficeManagerConfiguration setMaxTasksPerProcess(int maxTasksPerProcess) {
        this.maxTasksPerProcess = maxTasksPerProcess;
        return this;
    }
    
   /**
   * NB! This should only be used in development. This is because if two applications accidentally start 
   * using the same OOo pipe name. The second app would kill the OOo process started by the first app.
   * @param autokillOpenPipes 
   */
   public DefaultOfficeManagerConfiguration setAutokillOpenPipes(boolean autokillOpenPipes) {
	   this.autokillOpenPipes = autokillOpenPipes;
	   return this;
   }
   
   /**
    * Retry timeout set in milliseconds. Used for retrying office process calls.
    * If not set, it defaults to 2 minutes
    * @param retryTimeout - In milliseconds
	*
    * @see org.artofsolving.jodconverter.office.ManagedOfficeProcessSettings#DEFAULT_RETRY_TIMEOUT
    */
   public DefaultOfficeManagerConfiguration setRetryTimeout(long retryTimeout) {
	   this.retryTimeout = retryTimeout;
	   return this;
   }

   public OfficeManager buildOfficeManager() throws IllegalStateException {
        if (!officeHome.isDirectory()) {
            throw new IllegalStateException("officeHome doesn't exist or is not a directory: " + officeHome);
        } else if (!OfficeUtils.getOfficeExecutable(officeHome).isFile()) {
            throw new IllegalStateException("invalid officeHome: it doesn't contain soffice.bin: " + officeHome);
        }
        if (templateProfileDir != null && !isValidProfileDir(templateProfileDir)) {
            throw new IllegalStateException("invalid templateProfileDir: " + templateProfileDir);
        }
        
        int numInstances = connectionProtocol == OfficeConnectionProtocol.PIPE ? pipeNames.length : portNumbers.length;
        UnoUrl[] unoUrls = new UnoUrl[numInstances];
        for (int i = 0; i < numInstances; i++) {
            unoUrls[i] = (connectionProtocol == OfficeConnectionProtocol.PIPE) ? UnoUrl.pipe(pipeNames[i]) : UnoUrl.socket(portNumbers[i]);
        }
        return new ProcessPoolOfficeManager(officeHome, unoUrls, runAsArgs, templateProfileDir, instanceProfileDir, taskQueueTimeout, taskExecutionTimeout, maxTasksPerProcess, autokillOpenPipes, retryTimeout, new SigarProcessManager());
    }

    private boolean isValidProfileDir(File profileDir) {
        File setupXcu = new File(profileDir, "user/registry/data/org/openoffice/Setup.xcu");
        return setupXcu.exists();
    }

}
