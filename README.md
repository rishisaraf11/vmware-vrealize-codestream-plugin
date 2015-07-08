Jenkins CodeStream Plugin
---------------------

Jenkins version supported
------------------------
1.580.1 and above

Integrates Jenkins to vRealize CodeStream
----------------------------------------
This plugin integrates VMware vRealize CodeStream system to Jenkins. The plugin requires the plugin hpi file to be installed on the target machine.
With this plugin you can run any vRealize CodeStream activated pipeline.

Development
===========

Start the local Jenkins instance:

    mvn hpi:run


Installing
----------
Run

	mvn hpi:hpi

to create the plugin .hpi file.


To install:

1. copy the resulting ./target/codestream-jenkins-plugin.hpi file to the $JENKINS_HOME/plugins directory. Don't forget to restart Jenkins afterwards.

2. or use the plugin management console (http://example.com:8080/pluginManager/advanced) to upload the hpi file. You have to restart Jenkins in order to find the pluing in the installed plugins list.


Maintainer
----------
Rishi Saraf<rishisaraf11@gmail.com>




