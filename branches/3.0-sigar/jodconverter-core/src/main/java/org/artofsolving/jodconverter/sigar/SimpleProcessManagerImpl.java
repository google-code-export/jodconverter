package org.artofsolving.jodconverter.sigar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ptql.ProcessFinder;

/**
 * @author Shervin Asgari - <a href="mailto:shervin.asgari@redpill-linpro.com">shervin@redpill-linpro.com</a>
 */
public class SimpleProcessManagerImpl implements SimpleProcessManager {

	/**
	 * Best to use for very simple queries <b>without<b> Args.* - Command line argument passed to the process Env.* - Environment variable within the process Modules.* Shared
	 * library loaded within the process Returns a Set of all the process id's that was found
	 * 
	 * @param query
	 * @param searchValue
	 *            - The value you want to search for
	 * @return - Returns immutable Set of process id's as long or an empty Set if no results where found
	 * @see org.artofsolving.jodconverter.sigar.SimpleProcessManager#find(SimplePTQL)
	 */
	public List<Long> find(SimplePTQL ptlq) throws SigarException {
		Sigar sigar = new Sigar();
		try {
			ProcessFinder processFinder = new ProcessFinder(sigar);
			long[] find = processFinder.find(ptlq.getQuery());
			if (find.length > 0) {
				List<Long> list = new ArrayList<Long>(find.length);
				for (long value : find) {
					list.add(value);
				}
				return list;
			}

		} finally {
			sigar.close();
		}
		return Collections.emptyList();
	}

	/**
	 * @see org.artofsolving.jodconverter.sigar.SimpleProcessManager#findSingle(org.artofsolving.jodconverter.sigar.SimplePTQL)
	 */
	public Long findSingle(SimplePTQL ptlq) throws NonUniqueResultException, SigarException {
		Sigar sigar = new Sigar();
		try {
			ProcessFinder processFinder = new ProcessFinder(sigar);
			long[] find = processFinder.find(ptlq.getQuery());
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
