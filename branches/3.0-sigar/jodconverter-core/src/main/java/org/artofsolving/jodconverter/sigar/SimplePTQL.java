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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.artofsolving.jodconverter.util.PlatformUtils;

import com.google.common.base.Preconditions;

/**
 * A simplified PTQL so to simplify the query building and make it less error prone and typesafe. 
 * 
 * See more information http://support.hyperic.com/display/SIGAR/PTQL
 * 
 * @author Shervin Asgari - <a href="mailto:shervin@asgari.no">shervin@asgari.no</a>
 */
public class SimplePTQL {
	private final Operator operator;
	private final Attribute attribute;
	private final String searchValue;
	private final String args;
	private final Strategy strategy;
	
	public enum Strategy {
		ESCAPE, NOT_ESCAPE
	}

	private SimplePTQL(Attribute attribute, Operator operator, String searchValue, String args, Strategy strategy) {
		this.attribute = attribute;
		this.operator = operator;
		this.searchValue = searchValue;
		this.args = args;
		this.strategy = strategy;
	}
	
	public static class Builder {
		private final Operator operator;
		private final Attribute attribute;
		private final String searchValue;
		
		private Strategy strategy = Strategy.NOT_ESCAPE; 
		private StringBuilder args = new StringBuilder(""); //Default blank
		
		public Builder(Attribute attribute, Operator operator, String searchValue) {
			this.attribute = attribute;
			this.operator = operator;
			this.searchValue = searchValue;
		}

		public Builder addArgs(int argument, Operator operator, String searchValue, Strategy strategy) {
			Preconditions.checkNotNull(searchValue);
			
			if(strategy == Strategy.ESCAPE) {
				args.append(",Args." + String.valueOf(argument) + "." + operator.toString() + PlatformUtils.escapePTQLForRegex(searchValue));
			} else {
				Pattern pattern = Pattern.compile(",|=");
				Matcher matcher = pattern.matcher(searchValue);
				Preconditions.checkArgument(!matcher.find(), "searchValue cannot contain comma or equals sign. Either set Strategy.ESCAPE or remove it from the search value");
				args.append(",Args." + String.valueOf(argument) + "." + operator.toString() + searchValue);
			}
			
			return this;
		}
		
		public Builder setStrategy(Strategy strategy) {
			this.strategy = strategy;
			return this;
		}

		public SimplePTQL createQuery() {
			return new SimplePTQL(attribute, operator, searchValue, args.toString(), strategy);
		}
	}
	
	/**
	 * Returns the entiry PTQL query.
	 * ie: 
	 * <ul>
	 * <li>State.Name.eq=java</li>
	 * <li>Pid.Pid.eq=4245</li>
	 * <li>State.Name.re=^(https?d.*|[Aa]pache2?)$</li>
	 * </ul>
	 * @return
	 */
	public String getQuery() {
		return attribute.toString() + operator.toString() + (strategy == Strategy.ESCAPE ? PlatformUtils.escapePTQLForRegex(searchValue): searchValue) + args;
	}	

	private interface Operator {
		String toString();
	}

	private interface Attribute {
		String toString();
	}

	/**
	 * Equal to value
	 */
	public static Operator EQ() {
		return new Operator() {
			@Override
			public String toString() {
				return "eq=";
			}
		};
	}

	/**
	 * Not Equal to value
	 */
	public static Operator NE() {
		return new Operator() {
			@Override
			public String toString() {
				return "ne";
			}
		};
	}

	/**
	 * Ends with value
	 */
	public static Operator EW() {
		return new Operator() {
			@Override
			public String toString() {
				return "ew=";
			}
		};
	}

	/**
	 * Starts with value
	 */
	public static Operator SW() {
		return new Operator() {
			@Override
			public String toString() {
				return "sw=";
			}
		};
	}

	/**
	 * Contains value (substring)
	 */
	public static Operator CT() {
		return new Operator() {
			@Override
			public String toString() {
				return "ct=";
			}
		};
	}

	/**
	 * Regular expression value matches
	 */
	public static Operator RE() {
		return new Operator() {
			@Override
			public String toString() {
				return "re=";
			}
		};
	}

	/**
	 * <i>Only for numeric value<i> Greater than value
	 */
	public static Operator GT() {
		return new Operator() {
			@Override
			public String toString() {
				return "gt=";
			}
		};
	}

	/**
	 * <i>Only for numeric value<i> Greater than or equal value
	 */
	public static Operator GE() {
		return new Operator() {
			@Override
			public String toString() {
				return "ge=";
			}
		};
	}

	/**
	 * <i>Only for numeric value<i> Less than value
	 */
	public static Operator LT() {
		return new Operator() {
			@Override
			public String toString() {
				return "lt=";
			}
		};
	}

	/**
	 * <i>Only for numeric value<i> Less than value or equal value
	 */
	public static Operator LE() {
		return new Operator() {
			@Override
			public String toString() {
				return "le=";
			}
		};
	}

	/**
	 * The Process ID
	 */
	public static Attribute PID_PID() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Pid.Pid.";
			}
		};
	}

	/**
	 * File containing the process ID
	 */
	public static Attribute PID_PIDFILE() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Pid.PidFile.";
			}
		};
	}

	/**
	 * Windows Service name used to pid from the service manager
	 */
	public static Attribute PID_SERVICE() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Pid.Service.";
			}
		};
	}

	/**
	 * Base name of the process executable
	 */
	public static Attribute STATE_NAME() {
		return new Attribute() {
			@Override
			public String toString() {
				return "State.Name.";
			}
		};
	}

	/**
	 * User Name of the process owner
	 */
	public static Attribute CREDNAME_USER() {
		return new Attribute() {
			@Override
			public String toString() {
				return "CredName.User.";
			}
		};
	}

	/**
	 * Group Name of the process owner
	 */
	public static Attribute CREDNAME_GROUP() {
		return new Attribute() {
			@Override
			public String toString() {
				return "CredName.Group.";
			}
		};
	}

	/**
	 * User ID of the process owner
	 */
	public static Attribute CRED_UID() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Cred.Uid.";
			}
		};
	}

	/**
	 * Group ID of the process owner
	 */
	public static Attribute CRED_GID() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Cred.Gid.";
			}
		};
	}

	/**
	 * Effective User ID of the process owner
	 */
	public static Attribute CRED_EUID() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Cred.Euid.";
			}
		};
	}

	/**
	 * Effective Group ID of the process owner
	 */
	public static Attribute CRED_EGID() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Cred.Egid.";
			}
		};
	}

	/**
	 * Full path name of the process executable
	 */
	public static Attribute EXE_NAME() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Exe.Name.";
			}
		};
	}

	/**
	 * Current Working Directory of the process
	 */
	public static Attribute EXE_CWD() {
		return new Attribute() {
			@Override
			public String toString() {
				return "Exe.Cwd.";
			}
		};
	}
}
