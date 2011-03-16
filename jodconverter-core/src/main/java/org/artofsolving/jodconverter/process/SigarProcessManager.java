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

import java.io.IOException;

import org.artofsolving.jodconverter.util.PlatformUtils;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;

/**
 * {@link ProcessManager} implementation for "all" systems. Uses Sigar which 
 * provides a portable interface for gathering system information with native libraries. 
 * 
 * TODO: SigarProcessManager?? due to a lack of a better name
 * 
 * Use the following native files on the system you are using.
 * For example, minimal requirements to use the SIGAR Java API on Windows would be sigar.jar and sigar-x86-winnt.dll
 * <p>
 * libsigar-x86-linux.so 	 			Linux AMD/Intel 32-bit 	
 * libsigar-amd64-linux.so 	 			Linux AMD/Intel 64-bit 	
 * libsigar-ppc-linux.so 	 			Linux PowerPC 32-bit 	
 * libsigar-ppc64-linux.so 	 			Linux PowerPC 64-bit 	
 * libsigar-ia64-linux.so 	 			Linux Itanium 64-bit 	
 * libsigar-s390x-linux.so 	 			Linux zSeries 64-bit 	
 * sigar-x86-winnt.dll 	 				Windows AMD/Intel 32-bit 	
 * sigar-amd64-winnt.dll 	 			Windows AMD/Intel 64-bit 	
 * libsigar-ppc-aix-5.so 	 			AIX PowerPC 32-bit 	
 * libsigar-ppc64-aix-5.so 	 			AIX PowerPC 64-bit 	
 * libsigar-pa-hpux-11.sl 	 			HP-UX PA-RISC 32-bit 	
 * libsigar-ia64-hpux-11.sl 			HP-UX Itanium 64-bt 	
 * libsigar-sparc-solaris.so 			Solaris Sparc 32-bit 	
 * libsigar-sparc64-solaris.so 			Solaris Sparc 64-bit 	
 * libsigar-x86-solaris.so 	 			Solaris AMD/Intel 32-bit 	
 * libsigar-amd64-solaris.so 	 		Solaris AMD/Intel 64-bit 	
 * libsigar-universal-macosx.dylib 	 	Mac OS X PowerPC/Intel 32-bit 	
 * libsigar-universal64-macosx.dylib 	Mac OS X PowerPC/Intel 64-bit 	
 * libsigar-x86-freebsd-5.so 	 		FreeBSD 5.x AMD/Intel 32-bit 	
 * libsigar-x86-freebsd-6.so 	 		FreeBSD 6.x AMD/Intel 64-bit 	
 * libsigar-amd64-freebsd-6.so 	 		FreeBSD 6.x AMD/Intel 64-bit
 * </p>	
 * 
 * See link for more information
 * {@link http://support.hyperic.com/display/SIGAR/Home#Home-license}
 */
public class SigarProcessManager implements ProcessManager {
	private static final String STATE_NAME = "State.Name.re=";
	private static final String OFFICE = "office.*";
	private static final String ARGS = ",Args.1.re=.*";

	/**
	 * Returns the process id if found, or null if not found
	 */
	public String findPid(String regex) throws IOException {
		StringBuilder sb = new StringBuilder(STATE_NAME);
		
		int index = regex.indexOf(OFFICE);
		
		if( index == 0) {
			int index2 = regex.indexOf(regex.replace(OFFICE, ""));
			sb.append(regex.substring(0,index2))
			.append(ARGS)
			.append(PlatformUtils.escapePTQLForRegex(regex.substring(index2)));
		} else {
			//Normal find without the args
			sb.append(PlatformUtils.escapePTQLForRegex(regex));
		}
		
		//TODO: We need to find out how to best instansiate and close sigar
		Sigar sigar = new Sigar();
		ProcessFinder processFinder = new ProcessFinder(sigar);
		try {
			//'re' (Regular expression)
			//Alternatively we can use ct - Contains value (substring)
			//long[] pids = processFinder.find("State.Name.ct=" + regex);
			long[] pids = processFinder.find(sb.toString());
			if(pids.length > 0) {
				//Return the first or give error if there are more than one processes found?
				return String.valueOf(pids[0]);
			}
		} catch (SigarException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			sigar.close();
		}
		
		return null;
	}

	public void kill(Process process, String pid) throws IOException {
		Sigar sigar = new Sigar();
		try {
			sigar.kill(pid, 9);
			sigar.close();
		} catch (SigarException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			sigar.close();
		}
	}
}
