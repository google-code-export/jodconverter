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

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class SimpleSigarQueryTest {
	
	public void simpleQuery() throws Exception {
		SimpleProcessManager spm = new SimpleProcessManagerImpl();
		SimplePTQL ptql = new SimplePTQL(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "java");
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
		
		ptql = new SimplePTQL(SimplePTQL.PID_PID(), SimplePTQL.EQ(), String.valueOf(find.get(0)));
		Long findSingle = spm.findSingle(ptql);
		Assert.assertEquals(findSingle, find.get(0));
		
		ptql = new SimplePTQL(SimplePTQL.STATE_NAME(), SimplePTQL.EQ(), "Hopefully There is no Process called this");
		List<Long> find2 = spm.find(ptql);
		Assert.assertTrue(find2.size() == 0);
		
		ptql = new SimplePTQL(SimplePTQL.PID_PID(), SimplePTQL.GT(), "1");
		List<Long> find3 = spm.find(ptql);
		Assert.assertTrue(find3.size() > 1);
	}

}
