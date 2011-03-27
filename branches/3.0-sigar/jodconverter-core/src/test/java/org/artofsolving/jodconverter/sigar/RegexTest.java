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

import org.artofsolving.jodconverter.util.PlatformUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class RegexTest {

	public void escapePTQL() throws Exception {
		final String regex = "\\Qpipe,name=office1\\E";
		String newVal = PlatformUtils.escapePTQLForRegex(regex);
		Assert.assertEquals(newVal, "pipe.name=office1");

		String part1 = "office.*";
		String whole = part1 + regex;

		Assert.assertEquals(whole, "office.*\\Qpipe,name=office1\\E");

		int index = whole.indexOf(regex);
		if (index == -1) {
			return;
		}

		Assert.assertEquals(whole.substring(0, index), part1);
		Assert.assertEquals(whole.substring(index), regex);
	}

	public void sigarProcessManagerFindPid() throws Exception {
		final String STATE_NAME = "State.Name.re=";
		final String OFFICE = "office.*";
		final String ARGS = ",Args.1.re=.*";

		StringBuilder sb = new StringBuilder(STATE_NAME);
		String regex = "office.*\\Qpipe,name=office1\\E";
		int index = regex.indexOf(OFFICE);
		Assert.assertEquals(index, 0);
		int index2 = regex.indexOf(regex.replace(OFFICE, ""));
		sb.append(regex.substring(0, index2)).append(ARGS).append(PlatformUtils.escapePTQLForRegex(regex.substring(index2)));
		Assert.assertEquals(sb.toString(), "State.Name.re=office.*,Args.1.re=.*pipe.name=office1");

		sb = new StringBuilder(STATE_NAME);
		regex = "\\Qpipe,name=office1\\E";
		index = regex.indexOf(OFFICE);
		Assert.assertEquals(index, -1);
		sb.append(OFFICE).append(ARGS).append(PlatformUtils.escapePTQLForRegex(regex));

		Assert.assertEquals(sb.toString(), "State.Name.re=office.*,Args.1.re=.*pipe.name=office1");

	}
}
