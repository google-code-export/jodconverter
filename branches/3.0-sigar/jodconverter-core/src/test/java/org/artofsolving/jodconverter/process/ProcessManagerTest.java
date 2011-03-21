//
// JODConverter - Java OpenDocument Converter
// Copyright 2009 Art of Solving Ltd
// Copyright 2004-2009 Mirko Nasato
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
package org.artofsolving.jodconverter.process;

import org.artofsolving.jodconverter.ReflectionUtils;
import org.artofsolving.jodconverter.sigar.SimplePTQL;
import org.artofsolving.jodconverter.util.PlatformUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test
public class ProcessManagerTest {

    public void sigarProcessManager() throws Exception {
    	if (PlatformUtils.isWindows()) {
			throw new SkipException("Sleep only works on unix");
		}
    	
    	ProcessManager processManager = new SigarProcessManager();
    	//Difference between unix and sigar is that sigar's find will return the 'basename' of the process 
        Process process = new ProcessBuilder("sleep", "60s").start();
        
        SimplePTQL ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "sleep").createQuery();
        Long pid = processManager.findSingle(ptql);
        Assert.assertTrue(pid.longValue() > 0L);
        Assert.assertEquals(pid.toString(), ReflectionUtils.getPrivateField(process, "pid").toString());
        processManager.kill(pid, 9);
        
        Long findSingle = processManager.findSingle(ptql);
        Assert.assertEquals(findSingle, Long.valueOf(0L));
    }
    
}
