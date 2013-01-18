AT-plugin-Yale-custom-tasks
===========================

Plugin to add custom tasks to the Archivists Toolkit to make container processing more efficient

Inorder for it to work correctly the following changes need to be made to the MySQL backend.
Please note it has not been tested with other database backends.

In order for the caching and indexing functionality to work for the largest resource records, the 
following minor changes need to be made to the MySQL database.  Those changes have no effect on 
normal AT functioning.

1. Change the data type of the "dataString" column in the ATPluginData Table from "Text" to "Medium Text" 
in order to allow container information for the larger resource records to be saved without an truncation errors.

2. Set the max packet from 1MB to 32MB (http://dev.mysql.com/doc/refman/5.5/en/packet-too-large.html)
