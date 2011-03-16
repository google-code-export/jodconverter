package org.artofsolving.jodconverter.sigar;

import java.util.List;

import org.hyperic.sigar.SigarException;

/**
 * @author Shervin Asgari - <a href="mailto:shervin.asgari@redpill-linpro.com">shervin@redpill-linpro.com</a>
 *
 */
public interface SimpleProcessManager {

	/**
	 * Returns the process id of a single result
	 * @param query
	 * @param searchValue - The value you want to search for
	 * @return Returns the process id. Will throw exception if multiple results are found.
	 * Will return 0L if no results where found
	 */
	Long findSingle(SimplePTQL query) throws NonUniqueResultException, SigarException;
	
	/**
	 * Returns a Set of all the process id's that was found
	 * @param query
	 * @param searchValue - The value you want to search for
	 * @return - Returns an empty List if no results where found
	 */
	List<Long> find(SimplePTQL query) throws SigarException;
}
