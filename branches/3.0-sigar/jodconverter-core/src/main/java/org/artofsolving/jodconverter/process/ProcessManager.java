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

import java.util.List;

import org.artofsolving.jodconverter.sigar.SimplePTQL;
import org.hyperic.sigar.SigarException;

public interface ProcessManager {

	/**
	 * Returns the process id of a single result
	 * @param query
	 * @param searchValue - The value you want to search for
	 * @return Returns the process id. Will throw exception if multiple results are found.
	 * Will return 0L if no results where found
	 */
	Long findSingle(SimplePTQL query) throws NonUniqueResultException, SigarException;
	
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
	 */
	List<Long> find(SimplePTQL query) throws SigarException;
	
	
	void kill(long pid, int signium) throws SigarException;

}
