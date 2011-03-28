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
package org.artofsolving.jodconverter.sigar;

import java.util.List;

import org.artofsolving.jodconverter.process.NonUniqueResultException;
import org.artofsolving.jodconverter.process.ProcessManager;
import org.artofsolving.jodconverter.process.SigarProcessManager;
import org.artofsolving.jodconverter.sigar.SimplePTQL.Strategy;
import org.artofsolving.jodconverter.util.PlatformUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test
public class SimplePTQLTest {
	
	public void simpleQuery() throws Exception {
		ProcessManager spm = new SigarProcessManager();
		SimplePTQL ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "java").createQuery();
		Assert.assertEquals(ptql.getQuery(), "State.Name.eq=java");
		
		List<Long> find = spm.find(ptql);
		Assert.assertTrue(find.size() > 0);
		
		if(find.size() > 1) {
			try {
				spm.findSingle(ptql);
				Assert.fail("Should not reach here, should get exception");
			} catch(NonUniqueResultException nre) {
				//More than one results where found
			}	
		}
		
		ptql = new SimplePTQL.Builder(SimplePTQL.PID_PID(), SimplePTQL.EQ(), String.valueOf(find.get(0))).createQuery();
		Long findSingle = spm.findSingle(ptql);
		Assert.assertEquals(findSingle, find.get(0));
		
		ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "Hopefully There is no Process called this").createQuery();
		List<Long> find2 = spm.find(ptql);
		Assert.assertTrue(find2.size() == 0);
		
		ptql = new SimplePTQL.Builder(SimplePTQL.PID_PID(), SimplePTQL.GT(), "1").createQuery();
		List<Long> find3 = spm.find(ptql);
		Assert.assertTrue(find3.size() > 1);
	}
	
	public void args() throws Exception {
		SimplePTQL ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.RE(), "office.*")
		.addArgs("*", SimplePTQL.RE(), "\\Qpipe,name,office1\\E", Strategy.ESCAPE)
		.createQuery();
		Assert.assertEquals(ptql.getQuery(), "State.Name.re=office.*,Args.*.re=pipe.name.office1");
		
		ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "office.*")
		.addArgs("*", SimplePTQL.RE(), "\\Qpipe,name,office1\\E", Strategy.ESCAPE)
		.addArgs("*", SimplePTQL.EQ(), "\\Qpipe,name=office2\\E", Strategy.ESCAPE)
		.setStrategy(Strategy.ESCAPE)
		.createQuery();
		
		Assert.assertEquals(ptql.getQuery(), "State.Name.eq=office.*,Args.*.re=pipe.name.office1,Args.*.eq=pipe.name=office2");
		
		try {
			ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "office.*")
			.addArgs("1", SimplePTQL.RE(), "\\Qpipe,name,office1\\E", Strategy.ESCAPE)
			.addArgs("2", SimplePTQL.EQ(), "\\Qpipe,name,office2\\E", Strategy.NOT_ESCAPE)
			.createQuery();
		
			Assert.fail("Method should have thrown IllegalArgumentException");
		} catch(IllegalArgumentException ex) {}
	}

	public void realArguments() throws Exception {
		if (PlatformUtils.isWindows()) {
			throw new SkipException("Sleep only works on unix");
		}
		
		Process process = new ProcessBuilder("sleep", "60s").start();
        Assert.assertNotNull(process);
        
        SimplePTQL ptql = new SimplePTQL.Builder(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "sleep")
        					.addArgs("1", SimplePTQL.EQ(), "60s", Strategy.NOT_ESCAPE).createQuery();
        
        
        ProcessManager spm = new SigarProcessManager();
        Long findSingle = spm.findSingle(ptql);
        Assert.assertTrue(findSingle > 0L);
        
        spm.kill(findSingle, 9);
        Assert.assertEquals(spm.findSingle(ptql).longValue(), 0L);
	}
}
