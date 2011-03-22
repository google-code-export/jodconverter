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
package org.artofsolving.jodconverter.process;

import java.util.List;

import org.artofsolving.jodconverter.sigar.SimplePTQL;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;

import com.google.common.primitives.Longs;

/**
 * {@link ProcessManager} implementation for "all" systems. Uses Sigar which 
 * provides a portable interface for gathering system information with native libraries. 
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
 * 
 * @author Shervin Asgari - <a href="mailto:shervin.asgari@redpill-linpro.com">shervin@redpill-linpro.com</a>
 */
public class SigarProcessManager implements ProcessManager {

	/**
	 * Best to use for simple queries. Does not support:
	 * <ul>
	 * <li>Env.* - Environment variable within the process</li> 
	 * <li>Modules.* Shared library loaded within the process</li>
	 * </ul> 
	 * Returns a List of all the process id's that was found
	 * 
	 * @param ptql
	 * @return - Returns immutable List of process id's as Long or an empty List if no results where found
	 * @see org.artofsolving.jodconverter.sigar.SimpleProcessManager#find(SimplePTQL)
	 */
	public List<Long> find(SimplePTQL ptql) throws SigarException {
		Sigar sigar = new Sigar();
		try {
			ProcessFinder processFinder = new ProcessFinder(sigar);
			long[] find = processFinder.find(ptql.getQuery());
			return Longs.asList(find);
		} finally {
			sigar.close();
		}
	}

	/**
	 * @see org.artofsolving.jodconverter.sigar.SimpleProcessManager#findSingle(org.artofsolving.jodconverter.sigar.SimplePTQL)
	 */
	public Long findSingle(SimplePTQL ptql) throws NonUniqueResultException, SigarException {
		Sigar sigar = new Sigar();
		try {
			ProcessFinder processFinder = new ProcessFinder(sigar);
			long[] find = processFinder.find(ptql.getQuery());
			if (find.length == 1) {
				return Long.valueOf(find[0]);
			} else if (find.length > 1) {
				throw new NonUniqueResultException("Found more than one process id");
			}
		} finally {
			sigar.close();
		}

		return Long.valueOf(0L);
	}

	public void kill(long pid, int signium) throws SigarException {
		Sigar sigar = new Sigar();
		try {
			sigar.kill(pid, signium);
		} finally {
			sigar.close();
		}
	}
}