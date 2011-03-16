JODConverter
============

This is JODConverter 3.0 beta.

JODConverter automates conversions between office document formats
using OpenOffice.org. 

See http://jodconverter.googlecode.com for the latest documentation.

=================
RUNNING THE TESTS
=================

For now if you want to run the tests you will have to put the depends folder in the LD_LIBRARY_PATH env
Something like:

% export LD_LIBRARY_PATH="DIR_where_Your_libsigar-x86-linux.so_is":$LD_LIBRARY_PATH

So if you put your libsigar-x86-linux.so in the depends/ below the current dir ("."): then::
% export LD_LIBRARY_PATH=depends:$LD_LIBRARY_PATH