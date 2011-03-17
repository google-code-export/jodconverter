package org.artofsolving.jodconverter.sigar;

import java.util.List;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;

import com.google.common.primitives.Longs;

/**
 * @author Shervin Asgari - <a href="mailto:shervin.asgari@redpill-linpro.com">shervin@redpill-linpro.com</a>
 */
public class SimpleProcessManagerImpl implements SimpleProcessManager {

	/**
	 * Best to use for very simple queries <b>without<b> Args.* - Command line argument passed to the process Env.* - Environment variable within the process Modules.* Shared
	 * library loaded within the process Returns a Set of all the process id's that was found
	 * 
	 * @param query
	 * @param searchValue - The value you want to search for
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

		return 0L;
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