Jenkins CodeStream Plugin
---------------------

Jenkins version supported
------------------------
1.580.1 and above

Integrates Jenkins to vRealize CodeStream
----------------------------------------
This plugin integrates VMware vRealize CodeStream system to Jenkins. The plugin requires the plugin hpi file to be installed on the target machine.
With this plugin you can run any vRealize CodeStream activated pipeline.


Installing
----------
You can install plugin using Jenkins plugin manager  advance option. Just upload the hpi file and restart the jenkins server

Building
--------

$ mvn hpi:run

This will build the plugin, grab everything needed and start you up a
fresh Jenkins instance on a TCP/IP port for you to test against.

Maven does have a habit of downloading the internet, but it's at least
easy to use to hack on a plugin of something.

Maintainer
----------
Rishi Saraf<rsaraf@vmware.com>
Make change to invoke CodeStreamPipeline after Jenkins Build



